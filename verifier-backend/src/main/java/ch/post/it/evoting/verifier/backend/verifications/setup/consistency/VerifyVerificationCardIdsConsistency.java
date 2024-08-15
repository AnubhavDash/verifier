/*
 * (c) Copyright 2024 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.hasNoDuplicates;
import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.evotinglibraries.domain.validations.Validations;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component("verifySetupVerificationCardIdsConsistency")
public class VerifyVerificationCardIdsConsistency extends AbstractVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyVerificationCardIdsConsistency.class);

	private final ElectionDataExtractionService electionDataExtractionService;

	protected VerifyVerificationCardIdsConsistency(
			final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService electionDataExtractionService) {
		super(resultPublisherService);
		this.electionDataExtractionService = electionDataExtractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification312.description"));
		definition.setId("03.12");
		definition.setName("VerifyVerificationCardIdsConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final boolean verificationCardIdsConsistent = extractVerificationCardIds(inputDirectoryPath).stream()
				.parallel()
				.map(this::verifyConsistency)
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (verificationCardIdsConsistent) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification312.nok.message"));
		}
	}

	private ImmutableList<PayloadsVerificationCardIds> extractVerificationCardIds(final Path inputDirectoryPath) {
		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(
				inputDirectoryPath);
		final ImmutableList<VerificationCardSetContext> verificationCardSetContexts = electionEventContextPayload.getElectionEventContext()
				.verificationCardSetContexts();

		final ImmutableList<Path> contextVerificationCardSetPaths = electionDataExtractionService.getContextVerificationCardSetPaths(
				inputDirectoryPath);

		return electionDataExtractionService.getSetupVerificationCardSetPaths(inputDirectoryPath).stream()
				.parallel()
				.map(verificationCardSetIdPath -> {

					final ImmutableList<String> verificationDataIds = electionDataExtractionService.getSetupComponentVerificationDataPayloadsDataExtractionsSortedByChunkId(
									verificationCardSetIdPath)
							.map(dataExtraction -> {
								final ImmutableList<String> verificationCardIds = dataExtraction.verificationCardIds();

								checkState(hasNoDuplicates(verificationCardIds));
								return verificationCardIds;
							})
							.flatMap(ImmutableList::stream)
							.collect(toImmutableList());

					final ConcurrentMap<Integer, ImmutableList<String>> nodeIdsToCodeSharesIds = new ConcurrentHashMap<>();
					electionDataExtractionService.getControlComponentCodeSharesPayloadsDataExtractions(verificationCardSetIdPath)
							.sorted(Comparator.comparingInt(dataExtraction -> dataExtraction.chunkIds().iterator().next()))
							.forEachOrdered(dataExtraction -> {
								final ImmutableList<String> verificationCardIdsNode1 = dataExtraction.verificationCardIdsNode1().stream()
										.collect(toImmutableList());
								final ImmutableList<String> verificationCardIdsNode2 = dataExtraction.verificationCardIdsNode2().stream()
										.collect(toImmutableList());
								final ImmutableList<String> verificationCardIdsNode3 = dataExtraction.verificationCardIdsNode3().stream()
										.collect(toImmutableList());
								final ImmutableList<String> verificationCardIdsNode4 = dataExtraction.verificationCardIdsNode4().stream()
										.collect(toImmutableList());

								checkState(hasNoDuplicates(verificationCardIdsNode1));
								checkState(hasNoDuplicates(verificationCardIdsNode2));
								checkState(hasNoDuplicates(verificationCardIdsNode3));
								checkState(hasNoDuplicates(verificationCardIdsNode4));

								nodeIdsToCodeSharesIds.merge(1, verificationCardIdsNode1,
										(l1, l2) -> Stream.concat(l1.stream(), l2.stream()).collect(toImmutableList()));
								nodeIdsToCodeSharesIds.merge(2, verificationCardIdsNode2,
										(l1, l2) -> Stream.concat(l1.stream(), l2.stream()).collect(toImmutableList()));
								nodeIdsToCodeSharesIds.merge(3, verificationCardIdsNode3,
										(l1, l2) -> Stream.concat(l1.stream(), l2.stream()).collect(toImmutableList()));
								nodeIdsToCodeSharesIds.merge(4, verificationCardIdsNode4,
										(l1, l2) -> Stream.concat(l1.stream(), l2.stream()).collect(toImmutableList()));
							});

					final String verificationCardSetId = verificationCardSetIdPath.getFileName().toString();

					final ImmutableList<String> tallyDataIds = contextVerificationCardSetPaths.stream()
							.parallel()
							.filter(vcsPath -> vcsPath.getFileName().toString().equals(verificationCardSetId))
							.flatMap(electionDataExtractionService::getSetupComponentTallyDataPayloadsDataExtractions)
							.map(dataExtraction -> {
								final ImmutableList<String> verificationCardIds = ImmutableList.of(dataExtraction.verificationCardIds());

								checkState(hasNoDuplicates(verificationCardIds));
								return verificationCardIds;
							})
							.flatMap(ImmutableList::stream)
							.collect(toImmutableList());

					final int numberOfVotingCards = verificationCardSetContexts.stream()
							.parallel()
							.filter(vcs -> vcs.getVerificationCardSetId().equals(verificationCardSetId))
							.collect(MoreCollectors.onlyElement())
							.getNumberOfVotingCards();

					return new PayloadsVerificationCardIds(verificationDataIds, nodeIdsToCodeSharesIds, tallyDataIds, numberOfVotingCards);
				})
				.collect(toImmutableList());
	}

	/**
	 * Verifies:
	 * <ul>
	 *     <li>the verification card ids are unique among the SetupComponentVerificationDataPayload chunks.</li>
	 *     <li>the verification card ids are unique among the ControlComponentCodeSharesPayload chunks.</li>
	 *     <li>the ControlComponentCodeSharesPayloads' verification card ids have the same content and order across all nodes.</li>
	 *     <li>the verification card ids of each payload have the same content and order.</li>
	 *     <li>the verification card ids of the SetupComponentVerificationDataPayload's chunks have the same content and order than the
	 *     verification card ids of the ControlComponentCodeSharesPayload's chunks.</li>
	 *     <li>the number of verification card ids in each payload is equal to the {@code numberOfVotingCards}.</li>
	 * </ul>
	 */
	private boolean verifyConsistency(final PayloadsVerificationCardIds payloadsVerificationCardIds) {
		final ImmutableList<String> verificationDataIds = payloadsVerificationCardIds.verificationDataIds;
		final Map<Integer, ImmutableList<String>> nodeIdsToCodeSharesIds = payloadsVerificationCardIds.nodeIdsToVerificationCardIds;
		final ImmutableList<String> tallyDataIds = payloadsVerificationCardIds.tallyDataIds;
		final int numberOfVotingCards = payloadsVerificationCardIds.numberOfVotingCards;

		if (!hasNoDuplicates(verificationDataIds)) {
			LOGGER.info("There are duplicated verification card ids among the SetupComponentVerificationDataPayload chunks.");
			return false;
		}

		final ImmutableList<String> codeSharesVerificationIds = nodeIdsToCodeSharesIds.get(1);
		final boolean allCodeSharesIdsUniquePerNode = nodeIdsToCodeSharesIds.values().stream()
				.parallel()
				.allMatch(Validations::hasNoDuplicates);
		if (!allCodeSharesIdsUniquePerNode) {
			LOGGER.info(
					"There are either duplicated verification card ids among the ControlComponentCodeSharesPayload chunks.");
			return false;
		}

		// The SetupComponentTallyData payload ensured no verification card id duplicates.

		final boolean allCodeSharesIdsEqualAcrossNodes = nodeIdsToCodeSharesIds.values().stream()
				.parallel()
				.allMatch(codeSharesIds -> codeSharesIds.equals(codeSharesVerificationIds));
		if (!allCodeSharesIdsEqualAcrossNodes) {
			LOGGER.info(
					"The ControlComponentCodeSharesPayload's verification card ids are different across nodes.");
			return false;
		}

		return tallyDataIds.equals(verificationDataIds)
				&& tallyDataIds.equals(codeSharesVerificationIds)
				&& tallyDataIds.size() == numberOfVotingCards;
	}

	private record PayloadsVerificationCardIds(ImmutableList<String> verificationDataIds,
											   Map<Integer, ImmutableList<String>> nodeIdsToVerificationCardIds,
											   ImmutableList<String> tallyDataIds, int numberOfVotingCards) {
	}

}
