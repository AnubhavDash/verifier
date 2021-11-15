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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0110._3.Delivery;
import ch.ech.xmlns.ech_0110._3.ListResultsType;
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
public class CheckTallyingLists extends AbstractVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(CheckTallyingLists.class);

	private final PathService pathService;

	public CheckTallyingLists(final PathService pathService, final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(4);
		definition.setId(4);
		definition.setCategory(Category.COMPLETENESS);
		definition
				.setDescription(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification04.description"));
		definition.setName("checkTallyingLists");
		definition.addVerificationTrait(VerificationTrait.BLOCK_4);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		final var configurationAnonymizedPathNode = pathService.buildFromRootPath(StructureKey.CONFIG_ANONYMIZED, inputDirectoryPath);
		final Configuration configuration;
		try {
			configuration = Deserializer.fromXml(configurationAnonymizedPathNode.getPath(), Configuration.class);
		} catch (IOException | JAXBException | XMLStreamException e) {
			throw new VerificationPreconditionException("Failed to deserialize config anonymized.", e);
		}

		final Map<String, Boolean> mapListIsEmpty = configuration.getContest().getElectionInformation().stream()
				.flatMap(ei -> ei.getList().stream())
				.map(l -> new AbstractMap.SimpleEntry<>(l.getListIdentification(), l.isListEmpty()))
				.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

		final Map<String, String> mapLcIdListId = configuration.getContest().getElectionInformation().stream()
				.flatMap(ei -> ei.getList().stream())
				.map(l -> {
					final String listIden = l.getListIdentification();
					return l.getCandidatePosition().stream()
							.map(cp -> new AbstractMap.SimpleEntry<>(cp.getCandidateListIdentification(), listIden))
							.collect(Collectors.toMap(
									AbstractMap.SimpleEntry::getKey,
									AbstractMap.SimpleEntry::getValue,
									(listId1, listId2) -> listId1));
				}).flatMap(map -> map.entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		// 2, decrypt file => map<countingCircle, map<ElectionId, map<listId, count>>>
		final var eVotingDecryptResultPathNode = pathService.buildFromRootPath(StructureKey.EVOTING_DECRYPT_RESULT, inputDirectoryPath);
		final Results results;
		try {
			results = Deserializer.fromXml(eVotingDecryptResultPathNode.getPath(), Results.class);
		} catch (IOException | JAXBException | XMLStreamException e) {
			throw new VerificationPreconditionException("Failed to deserialize evoting decrypt result.", e);
		}
		Map<String, Map<String, Map<String, Long>>> countByListId = results.getBallotsBox().stream()
				.flatMap(bb -> bb.getCountingCircle().stream())
				.map(cc -> {
					final String ccId = cc.getCountingCircleIdentification();
					final Map<String, Map<String, Long>> electionCount =
							cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getElection().stream())
									.map(e -> {
										final String electionId = e.getElectionIdentification();
										final CountMap<String> listIdCountMap = new CountMap<>();
										e.getBallot().forEach(ballot -> {
											if (ballot.getChosenListIdentification() == null) {
												//candidate only election, nothing to do
											} else {
												// empty list
												if (mapListIsEmpty.get(ballot.getChosenListIdentification())) {
													ballot.getChosenCandidateListIdentification().forEach(lcId -> {
														final String candidateListId = mapLcIdListId.get(lcId);
														if (!mapListIsEmpty.get(candidateListId)) {
															listIdCountMap.increment(candidateListId);
														}
													});
												} else {
													// normal list
													ballot.getChosenCandidateListIdentification().forEach(lcId -> {
														final String candidateListId = mapLcIdListId.get(lcId);
														if (!mapListIsEmpty.get(candidateListId)) {
															//real candidate.
															listIdCountMap.increment(candidateListId);
														} else {
															//empty candidate
															listIdCountMap.increment(ballot.getChosenListIdentification());
														}
													});
												}
											}
										});
										return new AbstractMap.SimpleEntry<>(electionId, listIdCountMap);
									}).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
											AbstractMap.SimpleEntry::getValue));
					return new AbstractMap.SimpleEntry<>(ccId, electionCount);
				})
				.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue,
						(electionMap1, electionMap2) -> {
							return concatMap(electionMap1, electionMap2, (listmap1, listmap2) -> {
								return concatMap(listmap1, listmap2, Long::sum);
							});
						}));

		Map<String, Map<String, Map<String, Long>>> countOfEmptyValuesByListId = results.getBallotsBox().stream()
				.flatMap(bb -> bb.getCountingCircle().stream())
				.map(cc -> {
					final String ccId = cc.getCountingCircleIdentification();
					final Map<String, Map<String, Long>> electionCount =
							cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getElection().stream())
									.map(e -> {
										final String electionId = e.getElectionIdentification();
										final CountMap<String> listIdCountMap = new CountMap<>();
										e.getBallot().forEach(ballot -> {
											if (ballot.getChosenListIdentification() == null) {
												//candidate only election, nothing to do
											} else {
												if (!mapListIsEmpty.get(ballot.getChosenListIdentification())) {
													//normal list
													ballot.getChosenCandidateListIdentification().forEach(lcId -> {
														final String choosenList = ballot.getChosenListIdentification();
														final String candidateListId = mapLcIdListId.get(lcId);
														if (mapListIsEmpty.get(candidateListId)) {
															//empty candidate
															listIdCountMap.increment(choosenList);
														}
													});
												}
											}
										});
										return new AbstractMap.SimpleEntry<>(electionId, listIdCountMap);
									}).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
											AbstractMap.SimpleEntry::getValue));
					return new AbstractMap.SimpleEntry<>(ccId, electionCount);
				})
				.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue,
						(electionMap1, electionMap2) -> {
							return concatMap(electionMap1, electionMap2, (listCountMap1, listCountMap2) -> {
								return concatMap(listCountMap1, listCountMap2, Long::sum);
							});
						}));

		final var eCH0110PathNode = pathService.buildFromRootPath(StructureKey.ECH0110, inputDirectoryPath);
		final Delivery ech110;
		try {
			ech110 = Deserializer.fromXml(eCH0110PathNode.getPath(), Delivery.class);
		} catch (IOException | JAXBException | XMLStreamException e) {
			throw new VerificationPreconditionException("Failed to deserialize ech0110.", e);
		}

		final Map<String, String> failedOccurrences = new ConcurrentHashMap<>();
		ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
			final String ccId = cc.getCountingCircle().getCountingCircleId();
			cc.getElectionResults().stream()
					.filter(er -> er.getProportionalElection() != null)
					.forEach(er -> {
						final String electionId = er.getElection().getElectionIdentification();
						er.getProportionalElection().getList()
								.forEach(l -> {
									final String listId = l.getListInformation().getListIdentification();
									final BigInteger countOfPartyVotes = getCountOfPartyVotes(l);
									final BigInteger lcpCount = getVoteCount(countByListId, ccId, electionId, listId);
									final BigInteger countOfAdditionalVotes = getCountOfAdditionalVotes(l);
									final BigInteger emptyCount = getVoteCount(countOfEmptyValuesByListId, ccId, electionId
											, listId);
									if (!MathHelper.areEqual(countOfPartyVotes, lcpCount)
											|| !MathHelper.areEqual(countOfAdditionalVotes, emptyCount)) {
										LOGGER.debug("count not equal : CC:{} electionId:{} list:{} decrypt:{} 110:{}", ccId, electionId, listId,
												lcpCount, countOfPartyVotes);
										failedOccurrences.put(ccId, listId);
									}
								});
					});
		});
		if (!failedOccurrences.isEmpty()) {
			final Map.Entry<String, String> entry = failedOccurrences.entrySet().iterator().next();
			final String ccId = entry.getKey();
			final String listId = entry.getValue();
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification04.nok.message", listId,
							ccId));
		}

		return VerificationResultEvent.success(this, getVerificationDefinition());
	}

	private <K, V> Map<K, V> concatMap(Map<K, V> map1, Map<K, V> map2, BiFunction<V, V, V> mergeFunction) {
		Map<K, V> result = new HashMap<>(map2);
		for (Map.Entry<K, V> entry : map1.entrySet()) {
			if (result.containsKey(entry.getKey())) {
				//map2 also contains this key, then merge both values together
				result.replace(entry.getKey(), mergeFunction.apply(entry.getValue(), map2.get(entry.getKey())));
			} else {
				//map2 doesn't contains the key, then adding it
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	private BigInteger getCountOfPartyVotes(ListResultsType l) {
		if (l.getCountOfPartyVotes() != null) {
			return l.getCountOfPartyVotes().getTotal();
		} else {
			return BigInteger.ZERO;
		}
	}

	private BigInteger getCountOfAdditionalVotes(ListResultsType l) {
		if (l.getCountOfCandidateVotes() != null) {
			return l.getCountOfAdditionalVotes().getTotal();
		} else {
			return BigInteger.ZERO;
		}
	}

	private BigInteger getVoteCount(Map<String, Map<String, Map<String, Long>>> resultMap, String ccId, String electionId, String listId) {
		Map<String, Map<String, Long>> countByCC = resultMap.get(ccId);
		if (countByCC == null) {
			throw new IllegalArgumentException("cannot find the decrypt data for given countingCircle : " + ccId);
		}
		Map<String, Long> countByElection = countByCC.get(electionId);
		if (countByElection == null) {
			throw new IllegalArgumentException("cannot find the decrypt data for given election : " + electionId);
		}
		return BigInteger.valueOf(countByElection.get(listId) == null ? 0L : countByElection.get(listId));
	}
}
