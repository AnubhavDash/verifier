/*
 * Copyright 2022 Post CH Ltd
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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ControlComponentCodeShare;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ControlComponentCodeSharesPayload;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SetupComponentVerificationData;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.configuration.SetupComponentTallyDataPayload;

@Component("VerifySetupVerificationCardIdsConsistency")
public class VerifyVerificationCardIdsConsistency extends AbstractVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyVerificationCardIdsConsistency.class);

	private final PathService pathService;
	private final ElectionDataExtractionService electionDataExtractionService;

	protected VerifyVerificationCardIdsConsistency(
			final PathService pathService,
			final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService electionDataExtractionService) {
		super(resultPublisherService);
		this.pathService = pathService;
		this.electionDataExtractionService = electionDataExtractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification311.description"));
		definition.setId(311);
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
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification311.nok.message"));
		}
	}

	private List<PayloadsVerificationCardIds> extractVerificationCardIds(final Path inputDirectoryPath) {
		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(
				inputDirectoryPath);
		final List<VerificationCardSetContext> verificationCardSetContexts = electionEventContextPayload.getElectionEventContext()
				.verificationCardSetContexts();

		final PathNode verificationCardSets = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		return verificationCardSets.getRegexPaths().stream()
				.parallel()
				.map(verificationCardSetIdPath -> {
					// The SetupComponentVerificationDataPayload ensures no duplicated verification card ids.
					final List<String> verificationDataIds = electionDataExtractionService.deserializeSetupComponentVerificationDataPayloadOrderByChunkId(
									verificationCardSetIdPath)
							.parallel()
							.flatMap(
									setupComponentVerificationDataPayload -> setupComponentVerificationDataPayload.getSetupComponentVerificationData()
											.stream())
							.map(SetupComponentVerificationData::verificationCardId)
							.toList();

					// The ControlComponentCodeSharesPayload ensures no duplicated verification card ids.
					final List<List<ControlComponentCodeSharesPayload>> controlComponentCodeSharesPayloads = electionDataExtractionService.deserializeControlComponentCodeSharesPayloadsOrderByChunkIdAndNodeId(
							verificationCardSetIdPath);
					final ConcurrentMap<Integer, List<String>> nodeIdsToCodeSharesIds = controlComponentCodeSharesPayloads.stream()
							.flatMap(Collection::stream)
							.collect(Collectors.groupingByConcurrent(
									ControlComponentCodeSharesPayload::getNodeId,
									Collectors.flatMapping(codeSharesPayload -> codeSharesPayload.getControlComponentCodeShares().stream()
													.map(ControlComponentCodeShare::verificationCardId),
											Collectors.toList())));

					// The SetupComponentTallyDataPayload ensures no duplicated verification card ids.
					final SetupComponentTallyDataPayload setupComponentTallyDataPayload = electionDataExtractionService.getSetupComponentTallyDataPayload(
							verificationCardSetIdPath);
					final List<String> tallyDataIds = setupComponentTallyDataPayload.getVerificationCardIds();

					final String verificationCardSetId = verificationCardSetIdPath.getFileName().toString();
					final int numberOfVotingCards = verificationCardSetContexts.stream()
							.parallel()
							.filter(vcs -> vcs.verificationCardSetId().equals(verificationCardSetId))
							.collect(MoreCollectors.onlyElement())
							.numberOfVotingCards();

					return new PayloadsVerificationCardIds(verificationDataIds, nodeIdsToCodeSharesIds, tallyDataIds, numberOfVotingCards);
				})
				.toList();
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
		final List<String> verificationDataIds = List.copyOf(payloadsVerificationCardIds.verificationDataIds);
		final Map<Integer, List<String>> nodeIdsToCodeSharesIds = payloadsVerificationCardIds.nodeIdsToVerificationCardIds;
		final List<String> tallyDataIds = List.copyOf(payloadsVerificationCardIds.tallyDataIds);
		final int numberOfVotingCards = payloadsVerificationCardIds.numberOfVotingCards;

		final Set<String> verificationDataVerificationCardIds = Set.copyOf(verificationDataIds);
		if (verificationDataIds.size() != verificationDataVerificationCardIds.size()) {
			LOGGER.info("There are duplicated verification card ids among the SetupComponentVerificationDataPayload chunks.");
			return false;
		}

		final List<String> codeSharesVerificationIds = List.copyOf(nodeIdsToCodeSharesIds.get(1));
		final boolean allCodeSharesIdsUniquePerNode = nodeIdsToCodeSharesIds.values().stream()
				.allMatch(codeSharesIds -> codeSharesIds.size() == Set.copyOf(codeSharesIds).size());
		if (!allCodeSharesIdsUniquePerNode) {
			LOGGER.info(
					"There are either duplicated verification card ids among the ControlComponentCodeSharesPayload chunks.");
			return false;
		}

		// The SetupComponentTallyData payload ensured no verification card id duplicates.

		final boolean allCodeSharesIdsEqualAcrossNodes = nodeIdsToCodeSharesIds.values().stream()
				.allMatch(codeSharesIds -> List.copyOf(codeSharesIds).equals(codeSharesVerificationIds));
		if (!allCodeSharesIdsEqualAcrossNodes) {
			LOGGER.info(
					"The ControlComponentCodeSharesPayload's verification card ids are different across nodes.");
			return false;
		}

		return tallyDataIds.equals(verificationDataIds)
				&& tallyDataIds.equals(codeSharesVerificationIds)
				&& tallyDataIds.size() == numberOfVotingCards;
	}

	private record PayloadsVerificationCardIds(List<String> verificationDataIds, Map<Integer, List<String>> nodeIdsToVerificationCardIds,
											   List<String> tallyDataIds, int numberOfVotingCards) {
	}

}
