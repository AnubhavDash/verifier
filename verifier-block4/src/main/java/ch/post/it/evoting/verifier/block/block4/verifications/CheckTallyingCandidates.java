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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0110._3.Delivery;
import ch.evoting.xmlns.config._4.CandidatePositionType;
import ch.evoting.xmlns.config._4.Configuration;
import ch.evoting.xmlns.decrypt._1.Results;
import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.AbstractVerification;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationPreconditionException;
import ch.post.it.evoting.verifier.common.block.tools.CountMap;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;

@Component
public class CheckTallyingCandidates extends AbstractVerification {

	private static final String VERIFICATION_03_NOK_MESSAGE = "verification03.nok.message";

	private final PathService pathService;

	public CheckTallyingCandidates(final PathService pathService, final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(4);
		definition.setId(3);
		definition.setCategory(Category.COMPLETENESS);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.description"));
		definition.setName("checkTallyingCandidates");
		definition.addVerificationTrait(VerificationTrait.BLOCK_4);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		//1, config file => map<candidateListId, candidateId> => map1
		final var configurationAnonymizedPathNode = pathService.buildFromRootPath(StructureKey.CONFIG_ANONYMIZED, inputDirectoryPath);
		final Configuration configuration;
		try {
			configuration = Deserializer.fromXml(configurationAnonymizedPathNode.getPath(), Configuration.class);
		} catch (IOException | JAXBException | XMLStreamException e) {
			throw new VerificationPreconditionException("Failed to deserialize config anonymized.", e);
		}
		final Map<String, String> mapConfig = configuration.getContest().getElectionInformation().stream()
				.flatMap(ei -> ei.getList().stream())
				.flatMap(l -> l.getCandidatePosition().stream())
				.filter(cp -> StringUtils.isNotEmpty(cp.getCandidateIdentification()))
				.collect(Collectors.toMap(CandidatePositionType::getCandidateListIdentification,
						CandidatePositionType::getCandidateIdentification, (id1, id2) -> id1));

		// 2, decrypt file => map<countingCircle, map<ListCandidateId||CandidateId, count>> => map2
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

					final Map<String, Long> answerCount =
							cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getElection().stream())
									.flatMap(e -> e.getBallot().stream())
									.flatMap(b -> Stream.of(b.getChosenCandidateIdentification().stream(),
											b.getChosenCandidateListIdentification().stream(),
											b.getChosenWriteInsCandidateValue().stream()).flatMap(Function.identity()))
									.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

					return new AbstractMap.SimpleEntry<>(ccId, answerCount);
				})
				.collect(Collectors.toMap(
						AbstractMap.SimpleEntry::getKey,
						AbstractMap.SimpleEntry::getValue,
						(ccId1, ccId2) -> {
							Map<String, Long> concat = new HashMap<>(ccId1);
							ccId2.forEach((k, v) -> concat.merge(k, v, Long::sum));
							return concat;
						}
				));

		final var eCH0110PathNode = pathService.buildFromRootPath(StructureKey.ECH0110, inputDirectoryPath);
		final Delivery ech110;
		try {
			ech110 = Deserializer.fromXml(eCH0110PathNode.getPath(), Delivery.class);
		} catch (IOException | JAXBException | XMLStreamException e) {
			throw new VerificationPreconditionException("Failed to deserialize ech0110.", e);
		}

		final List<String> failedMajoralVotes = new ArrayList<>();
		final List<String> failedProportionalVotes = new ArrayList<>();
		ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
			String ccId = cc.getCountingCircle().getCountingCircleId();
			cc.getElectionResults().stream()
					.filter(er -> er.getMajoralElection() != null)
					.flatMap(er -> er.getMajoralElection().getCandidate().stream())
					.forEachOrdered(c -> {
						if (c.getCandidateInformation().isOfficialCandidateYesNo()) {
							final String cId = c.getCandidateInformation().getCandidateIdentification();
							final BigInteger decryptCount = getDecryptCount(mapConfig, mapDecrypt, ccId, cId);
							if (!MathHelper.areEqual(c.getCountOfVotesTotal(), decryptCount)) {
								failedMajoralVotes.add(cId);
							}
						}
					});
			cc.getElectionResults().stream()
					.filter(er -> er.getProportionalElection() != null)
					.flatMap(er -> er.getProportionalElection().getCandidate().stream())
					.forEach(c -> {
						if (c.getCandidateInformation().isOfficialCandidateYesNo()) {
							final String cId = c.getCandidateInformation().getCandidateIdentification();
							final BigInteger decryptCount = getDecryptCount(mapConfig, mapDecrypt, ccId, cId);
							if (!MathHelper.areEqual(c.getCountOfVotesTotal(), decryptCount)) {
								failedProportionalVotes.add(cId);
							}
						}
					});
		});
		if (!failedMajoralVotes.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, VERIFICATION_03_NOK_MESSAGE,
							failedMajoralVotes.get(0)));
		}
		if (!failedProportionalVotes.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, VERIFICATION_03_NOK_MESSAGE,
							failedProportionalVotes.get(0)));
		}

		//Write Ins
		// writeInsDecryptMap
		final Map<String, Map<String, Long>> writeInsDecryptMap = results.getBallotsBox().stream()
				.flatMap(bb -> bb.getCountingCircle().stream())
				.map(cc -> {
					final String ccId = cc.getCountingCircleIdentification();

					final Map<String, Long> answerCount =
							cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getElection().stream())
									.flatMap(e -> e.getBallot().stream())
									.flatMap(b -> b.getChosenWriteInsCandidateValue().stream())
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

		// writeInsEch110Map
		final CountMap<String> writeInsEch110Map = new CountMap<>();
		//check writeIns content, fill writeInsEch110Map
		final List<String> failedCandidateWriteInsContainsKeyMajoral = new ArrayList<>();
		final List<String> failedCandidateWriteInsContainsKeyProportional = new ArrayList<>();
		ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
			final String ccId = cc.getCountingCircle().getCountingCircleId();
			cc.getElectionResults().stream()
					.filter(er -> er.getMajoralElection() != null)
					.flatMap(er -> er.getMajoralElection().getCandidate().stream())
					.filter(c -> !c.getCandidateInformation().isOfficialCandidateYesNo())
					.forEach(c -> {
						final String cId =
								c.getCandidateInformation().getFamilyName() + " " + c.getCandidateInformation().getCallName();
						final Map<String, Long> writeInsAndCount = writeInsDecryptMap.get(ccId);
						writeInsEch110Map.increment(cId);
						if (writeInsAndCount != null && !writeInsAndCount.isEmpty()) {
							if (!writeInsAndCount.containsKey(cId)) {
								failedCandidateWriteInsContainsKeyMajoral.add(cId);
							}
						}
					});
			cc.getElectionResults().stream()
					.filter(er -> er.getProportionalElection() != null)
					.flatMap(er -> er.getProportionalElection().getCandidate().stream())
					.filter(c -> !c.getCandidateInformation().isOfficialCandidateYesNo())
					.forEach(c -> {
						final String cId =
								c.getCandidateInformation().getFamilyName() + " " + c.getCandidateInformation().getCallName();
						final Map<String, Long> writeInsAndCount = writeInsDecryptMap.get(ccId);
						writeInsEch110Map.increment(cId);
						if (writeInsAndCount != null && !writeInsAndCount.isEmpty()) {
							if (!writeInsAndCount.containsKey(cId)) {
								failedCandidateWriteInsContainsKeyProportional.add(cId);
							}
						}
					});
		});
		if (!failedCandidateWriteInsContainsKeyMajoral.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, VERIFICATION_03_NOK_MESSAGE,
							failedCandidateWriteInsContainsKeyMajoral.get(0)));
		}
		if (!failedCandidateWriteInsContainsKeyProportional.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, VERIFICATION_03_NOK_MESSAGE,
							failedCandidateWriteInsContainsKeyProportional.get(0)));
		}

		//check writeIns count
		final List<String> failedCandidateWriteInsEqualsMajoral = new ArrayList<>();
		final List<String> failedCandidateWriteInsEqualsProportional = new ArrayList<>();
		ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
			String ccId = cc.getCountingCircle().getCountingCircleId();
			cc.getElectionResults().stream()
					.filter(er -> er.getMajoralElection() != null)
					.flatMap(er -> er.getMajoralElection().getCandidate().stream())
					.filter(c -> !c.getCandidateInformation().isOfficialCandidateYesNo())
					.forEach(c -> {
						String cId =
								c.getCandidateInformation().getFamilyName() + " " + c.getCandidateInformation().getCallName();
						Map<String, Long> writeInsAndCount = writeInsDecryptMap.get(ccId);
						if (writeInsAndCount != null && !writeInsAndCount.isEmpty()) {
							if (!writeInsAndCount.get(cId).equals(writeInsEch110Map.get(cId))) {
								failedCandidateWriteInsEqualsMajoral.add(cId);
							}
						}
					});
			cc.getElectionResults().stream()
					.filter(er -> er.getProportionalElection() != null)
					.flatMap(er -> er.getProportionalElection().getCandidate().stream())
					.filter(c -> !c.getCandidateInformation().isOfficialCandidateYesNo())
					.forEach(c -> {
						String cId =
								c.getCandidateInformation().getFamilyName() + " " + c.getCandidateInformation().getCallName();
						Map<String, Long> writeInsAndCount = writeInsDecryptMap.get(ccId);
						if (writeInsAndCount != null && !writeInsAndCount.isEmpty()) {
							if (!writeInsAndCount.get(cId).equals(writeInsEch110Map.get(cId))) {
								failedCandidateWriteInsEqualsProportional.add(cId);
							}
						}
					});
		});
		if (!failedCandidateWriteInsEqualsMajoral.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, VERIFICATION_03_NOK_MESSAGE,
							failedCandidateWriteInsEqualsMajoral.get(0)));
		}
		if (!failedCandidateWriteInsEqualsProportional.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, VERIFICATION_03_NOK_MESSAGE,
							failedCandidateWriteInsEqualsProportional.get(0)));
		}

		return VerificationResultEvent.success(this, getVerificationDefinition());
	}

	private BigInteger getDecryptCount(Map<String, String> mapConfig, Map<String, Map<String, Long>> mapDecrypt,
			String ccId, String cId) {
		Map<String, Long> countByCC = mapDecrypt.get(ccId);
		if (countByCC == null) {
			throw new IllegalArgumentException("cannot find the decrypt data for given countingCircle : " + ccId);
		}

		long count = 0;

		//candidateListId
		if (mapConfig.containsValue(cId)) {
			count += mapConfig.entrySet().stream()
					.filter(e -> e.getValue().equals(cId))
					.mapToLong(e -> Optional.ofNullable(countByCC.get(e.getKey())).orElse(0L))
					.sum();
		}

		//candidateId
		count += Optional.ofNullable(countByCC.get(cId)).orElse(0L);

		return BigInteger.valueOf(count);
	}

}
