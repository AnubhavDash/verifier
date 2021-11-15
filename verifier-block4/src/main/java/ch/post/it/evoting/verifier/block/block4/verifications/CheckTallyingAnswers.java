/*
 * Copyright 2021 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.block.block4.verifications;

import java.io.IOException;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0110._3.Delivery;
import ch.evoting.xmlns.config._4.Configuration;
import ch.evoting.xmlns.decrypt._1.Results;
import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.AbstractVerification;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationPreconditionException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;

@Component
public class CheckTallyingAnswers extends AbstractVerification {

	private static final String YES = "YES";
	private static final String NO = "NO";
	private static final String EMPTY = "EMPTY";
	private static final String VERIFICATION_02_NOK_MESSAGE = "verification02.nok.message";

	private final PathService pathService;

	public CheckTallyingAnswers(final PathService pathService, final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(4);
		definition.setCategory(Category.COMPLETENESS);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification02.description"));
		definition.setId(2);
		definition.setName("checkTallyingAnswers");
		definition.addVerificationTrait(VerificationTrait.BLOCK_4);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		// 1, config file => map<Tuple<Qid, Atype>, answerId> => map1
		// 2, decrypt file => map<countingCircle, map<answerId, count>> => map2
		// 3, ech0110 file foreach cc do a loop for each Question get the count
		// ask the map1 and get the right answerId
		// check the count by asking map2

		// 1, config file => map<Tuple<Qid, Atype>, answerId> => map1
		final var configurationAnonymizedPathNode = pathService.buildFromRootPath(StructureKey.CONFIG_ANONYMIZED, inputDirectoryPath);
		final Configuration configuration;
		try {
			configuration = Deserializer.fromXml(configurationAnonymizedPathNode.getPath(), Configuration.class);
		} catch (IOException | JAXBException | XMLStreamException e) {
			throw new VerificationPreconditionException("Failed to deserialize configuration anonymized.", e);
		}
		final Map<Map.Entry<String, String>, String> mapConfig = configuration.getContest().getVoteInformation().stream()
				.flatMap(vi -> vi.getVote().getBallot().stream())
				.flatMap(b -> {
					final List<AbstractMap.SimpleEntry<AbstractMap.SimpleEntry<String, String>, String>> answers = new LinkedList<>();

					if (b.getStandardBallot() != null) {
						final String qId = b.getStandardBallot().getQuestionIdentification();
						answers.addAll(b.getStandardBallot().getAnswer().stream().map(a -> {
							final String aType = a.getStandardAnswerType();
							final String answerId = a.getAnswerIdentification();
							final AbstractMap.SimpleEntry<String, String> se = new AbstractMap.SimpleEntry<>(qId, aType);
							return new AbstractMap.SimpleEntry<>(se, answerId);
						}).collect(Collectors.toList()));
					}

					if (b.getVariantBallot() != null) {
						answers.addAll(b.getVariantBallot().getStandardQuestion().stream().flatMap(q -> {
							final String qId = q.getQuestionIdentification();
							return q.getAnswer().stream().map(a -> {
								final String aType = a.getStandardAnswerType();
								final String answerId = a.getAnswerIdentification();
								final AbstractMap.SimpleEntry<String, String> se = new AbstractMap.SimpleEntry<>(qId, aType);
								return new AbstractMap.SimpleEntry<>(se, answerId);
							});
						}).collect(Collectors.toList()));
					}

					return answers.stream();
				}).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

		// 2, decrypt file => map<countingCircle, map<answerId, count>> => map2
		final var eVotingDecryptResultPathNode = pathService.buildFromRootPath(StructureKey.EVOTING_DECRYPT_RESULT, inputDirectoryPath);
		final Results results;
		try {
			results = Deserializer.fromXml(eVotingDecryptResultPathNode.getPath(), Results.class);
		} catch (IOException | JAXBException | XMLStreamException e) {
			throw new VerificationPreconditionException("Failed to deserialize evoting decrypt result.", e);
		}
		final Map<String, Map<String, Long>> mapDecrypt = results.getBallotsBox().stream()
				.flatMap(bb -> bb.getCountingCircle().stream())
				.map(cc -> {
					final String ccId = cc.getCountingCircleIdentification();

					final Map<String, Long> answerCount = cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getVote().stream())
							.flatMap(v -> v.getBallot().stream())
							.flatMap(b -> b.getChosenAnswerIdentification().stream())
							.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

					return new AbstractMap.SimpleEntry<>(ccId, answerCount);
				})
				.collect(Collectors.toMap(
						AbstractMap.SimpleEntry::getKey,
						AbstractMap.SimpleEntry::getValue,
						(ccId1, ccId2) -> {
							final Map<String, Long> concat = new HashMap<>(ccId1);
							ccId2.forEach((k, v) -> concat.merge(k, v, Long::sum));
							return concat;
						}
				));

		// 3, ech0110 file foreach cc do a loop for each Question get the count
		final var eCH0110PathNode = pathService.buildFromRootPath(StructureKey.ECH0110, inputDirectoryPath);
		final Delivery ech110;
		try {
			ech110 = Deserializer.fromXml(eCH0110PathNode.getPath(), Delivery.class);
		} catch (IOException | JAXBException | XMLStreamException e) {
			throw new VerificationPreconditionException("Failed to deserialize ech0110", e);
		}

		final List<String> failedStandardYes = new ArrayList<>();
		final List<String> failedStandardNo = new ArrayList<>();
		final List<String> failedStandardEmpty = new ArrayList<>();
		final List<String> failedVariantYes = new ArrayList<>();
		final List<String> failedVariantNo = new ArrayList<>();
		final List<String> failedVariantEmpty = new ArrayList<>();

		ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
			final String ccId = cc.getCountingCircle().getCountingCircleId();
			cc.getVoteResults().stream().flatMap(vr -> vr.getBallotResult().stream())
					.forEachOrdered(br -> { // Ordered because ArrayLists are not concurrent.
						final BigInteger nbUnaccountedBlanks = br.getCountOfUnaccountedBlankBallots().getTotal();
						if (br.getStandardBallot() != null) {
							final String qId = br.getStandardBallot().getQuestionIdentification();
							if (!MathHelper.areEqual(br.getStandardBallot().getCountOfAnswerYes().getTotal(),
									getDecryptCount(mapDecrypt, mapConfig, ccId, qId, YES))) {
								failedStandardYes.add(qId);
							}
							if (!MathHelper.areEqual(br.getStandardBallot().getCountOfAnswerNo().getTotal(),
									getDecryptCount(mapDecrypt, mapConfig, ccId, qId, NO))) {
								failedStandardNo.add(qId);
							}
							if (!MathHelper.areEqual(
									br.getStandardBallot().getCountOfAnswerEmpty().getTotal().add(nbUnaccountedBlanks),
									getDecryptCount(mapDecrypt, mapConfig, ccId, qId, EMPTY))) {
								failedStandardEmpty.add(qId);
							}
						}
						if (br.getVariantBallot() != null) {
							br.getVariantBallot().getQuestionInformation().forEach(q -> {
								final String qId = q.getQuestionIdentification();
								if (!MathHelper.areEqual(q.getCountOfAnswerYes().getTotal(),
										getDecryptCount(mapDecrypt, mapConfig, ccId, qId, YES))) {
									failedVariantYes.add(qId);
								}
								if (!MathHelper.areEqual(q.getCountOfAnswerNo().getTotal(),
										getDecryptCount(mapDecrypt, mapConfig, ccId, qId, NO))) {
									failedVariantNo.add(qId);
								}
								if (!MathHelper.areEqual(q.getCountOfAnswerEmpty().getTotal().add(nbUnaccountedBlanks),
										getDecryptCount(mapDecrypt, mapConfig, ccId, qId, EMPTY))) {
									failedVariantEmpty.add(qId);
								}
							});
						}
					});
		});

		if (!failedStandardYes.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, VERIFICATION_02_NOK_MESSAGE,
							failedStandardYes.get(0), YES));
		}
		if (!failedStandardNo.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, VERIFICATION_02_NOK_MESSAGE,
							failedStandardNo.get(0), NO));
		}
		if (!failedStandardEmpty.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, VERIFICATION_02_NOK_MESSAGE,
							failedStandardEmpty.get(0), EMPTY));
		}
		if (!failedVariantYes.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, VERIFICATION_02_NOK_MESSAGE,
							failedVariantYes.get(0), YES));
		}
		if (!failedVariantNo.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, VERIFICATION_02_NOK_MESSAGE,
							failedVariantNo.get(0), NO));
		}
		if (!failedVariantEmpty.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, VERIFICATION_02_NOK_MESSAGE,
							failedVariantEmpty.get(0), EMPTY));
		}

		return VerificationResultEvent.success(this, getVerificationDefinition());
	}

	private BigInteger getDecryptCount(final Map<String, Map<String, Long>> mapDecrypt, final Map<Map.Entry<String, String>, String> mapConfig,
			final String ccId, final String qId, final String answerType) {
		final Map<String, Long> countByCC = mapDecrypt.get(ccId);
		final String answerId = mapConfig.get(new AbstractMap.SimpleEntry<>(qId, answerType));

		if (countByCC == null) {
			throw new IllegalArgumentException("cannot find the decrypt data for given countingCircle : " + ccId);
		}
		if (StringUtils.isEmpty(answerId)) {
			throw new IllegalArgumentException(String.format("cannot find the answerId for answer %s on question %s", answerType, qId));
		}
		return countByCC.get(answerId) == null ? BigInteger.ZERO : BigInteger.valueOf(countByCC.get(answerId));
	}

}
