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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ControlComponentCodeShare;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ControlComponentCodeSharesPayload;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SetupComponentVerificationData;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SetupComponentVerificationDataPayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.configuration.SetupComponentTallyDataPayload;

@Component
public class VerifyVerificationCardIdsConsistency extends AbstractVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyVerificationCardIdsConsistency.class);

	private final PathService pathService;
	private final ElectionDataExtractionService electionDataExtractionService;

	protected VerifyVerificationCardIdsConsistency(
			final PathService pathService,
			final ApplicationEventPublisher applicationEventPublisher,
			final ElectionDataExtractionService electionDataExtractionService) {
		super(applicationEventPublisher);
		this.pathService = pathService;
		this.electionDataExtractionService = electionDataExtractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification308.description"));
		definition.setId(308);
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
				.orElseThrow();

		if (verificationCardIdsConsistent) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification308.nok.message"));
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
					final List<SetupComponentVerificationDataPayload> setupComponentVerificationDataPayloads = electionDataExtractionService.deserializeSetupComponentVerificationDataPayload(
							verificationCardSetIdPath);
					final List<String> verificationDataIds = setupComponentVerificationDataPayloads.stream()
							.flatMap(
									setupComponentVerificationDataPayload -> setupComponentVerificationDataPayload.getSetupComponentVerificationData()
											.stream())
							.map(SetupComponentVerificationData::verificationCardId)
							.toList();

					// The ControlComponentCodeSharesPayload ensures no duplicated verification card ids.
					final List<List<ControlComponentCodeSharesPayload>> controlComponentCodeSharesPayloads = electionDataExtractionService.deserializeControlComponentCodeSharesPayloads(
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
							.filter(vcs -> vcs.verificationCardSetId().equals(verificationCardSetId))
							.collect(MoreCollectors.onlyElement())
							.numberOfVotingCards();

					return new PayloadsVerificationCardIds(verificationDataIds, nodeIdsToCodeSharesIds, tallyDataIds, numberOfVotingCards);
				})
				.toList();
	}

	private boolean verifyConsistency(final PayloadsVerificationCardIds payloadsVerificationCardIds) {
		final List<String> verificationDataIds = payloadsVerificationCardIds.verificationDataIds;
		final Map<Integer, List<String>> nodeIdsToCodeSharesIds = payloadsVerificationCardIds.nodeIdsToVerificationCardIds;
		final List<String> tallyDataIds = payloadsVerificationCardIds.tallyDataIds;
		final int numberOfVotingCards = payloadsVerificationCardIds.numberOfVotingCards;

		final Set<String> verificationDataVerificationCardIds = Set.copyOf(verificationDataIds);
		if (verificationDataIds.size() != verificationDataVerificationCardIds.size()) {
			LOGGER.info("There are duplicated verification card ids among the SetupComponentVerificationDataPayload chunks.");
			return false;
		}

		final Set<String> codeSharesVerificationIds = Set.copyOf(nodeIdsToCodeSharesIds.get(1));
		final boolean allCodeSharesIdsUniquePerNode = nodeIdsToCodeSharesIds.values().stream()
				.allMatch(codeSharesIds -> codeSharesIds.size() == Set.copyOf(codeSharesIds).size());
		final boolean allCodeSharesIdsEqualAcrossNodes = nodeIdsToCodeSharesIds.values().stream()
				.allMatch(codeSharesIds -> Set.copyOf(codeSharesIds).equals(codeSharesVerificationIds));
		if (!allCodeSharesIdsUniquePerNode || !allCodeSharesIdsEqualAcrossNodes) {
			LOGGER.info(
					"There are either duplicated verification card ids among the ControlComponentCodeSharesPayload chunks or they are different across nodes.");
			return false;
		}

		// The payload ensured no duplicates.
		final Set<String> tallyDataVerificationCardIds = Set.copyOf(tallyDataIds);

		return tallyDataVerificationCardIds.equals(verificationDataVerificationCardIds)
				&& tallyDataVerificationCardIds.equals(codeSharesVerificationIds)
				&& tallyDataVerificationCardIds.size() == numberOfVotingCards;
	}

	private record PayloadsVerificationCardIds(List<String> verificationDataIds, Map<Integer, List<String>> nodeIdsToVerificationCardIds,
											   List<String> tallyDataIds, int numberOfVotingCards) {
	}

}
