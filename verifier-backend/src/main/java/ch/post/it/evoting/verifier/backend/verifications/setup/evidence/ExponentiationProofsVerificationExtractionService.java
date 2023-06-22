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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static ch.post.it.evoting.evotinglibraries.domain.ControlComponentConstants.NODE_IDS;
import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.hasNoDuplicates;
import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.evotinglibraries.domain.returncodes.ControlComponentCodeShare;
import ch.post.it.evoting.evotinglibraries.domain.returncodes.ControlComponentCodeSharesPayload;
import ch.post.it.evoting.evotinglibraries.domain.returncodes.SetupComponentVerificationData;
import ch.post.it.evoting.evotinglibraries.domain.returncodes.SetupComponentVerificationDataPayload;
import ch.post.it.evoting.evotinglibraries.domain.validations.Validations;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;

@Service
public class ExponentiationProofsVerificationExtractionService {

	private final ElectionDataExtractionService extractionService;
	private final PathService pathService;

	public ExponentiationProofsVerificationExtractionService(final ElectionDataExtractionService extractionService,
			final PathService pathService) {
		this.extractionService = extractionService;
		this.pathService = pathService;
	}

	public VerifyEncryptedExponentiationProofsInput extractInput(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final ElectionEventContext electionEventContext = extractionService
				.getElectionEventContextPayload(inputDirectoryPath)
				.getElectionEventContext();
		final String electionEventId = electionEventContext.electionEventId();
		final List<String> verificationCardSetIds = electionEventContext
				.verificationCardSetContexts()
				.stream()
				.map(VerificationCardSetContext::verificationCardSetId)
				.toList();

		validateUUID(electionEventId);
		checkNotNull(verificationCardSetIds);
		verificationCardSetIds.forEach(Validations::validateUUID);
		checkArgument(hasNoDuplicates(verificationCardSetIds), "The verification card set IDs must not contain duplicates.");

		return new VerifyEncryptedExponentiationProofsInput.Builder()
				.setElectionEventId(electionEventId)
				.setVerificationCardSetIds(verificationCardSetIds)
				.build();
	}

	public Stream<ContextAndInputForVerificationCardSetAndControlComponent> extractContextAndInputs(
			final Path inputDirectoryPath, final VerifyEncryptedExponentiationProofsInput input) {
		checkNotNull(inputDirectoryPath);
		checkNotNull(input);

		final String electionEventId = input.getElectionEventId();
		final ElectionEventContextPayload electionEventContextPayload = extractionService.getElectionEventContextPayload(inputDirectoryPath);
		final ElectionEventContext electionEventContext = electionEventContextPayload.getElectionEventContext();
		final Map<String, Integer> numberOfVoters = electionEventContext.verificationCardSetContexts()
				.stream()
				.parallel()
				.collect(Collectors.toMap(VerificationCardSetContext::verificationCardSetId, VerificationCardSetContext::numberOfVotingCards));

		checkArgument(electionEventContext.electionEventId().equals(electionEventId));
		checkArgument(electionEventContext.verificationCardSetContexts().size() == input.getVerificationCardSetIds().size());

		final PathNode verificationCardSetIdPaths = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		return verificationCardSetIdPaths
				.getRegexPaths()
				.stream()
				.parallel()
				.map(verificationCardSetIdPath -> {
					final ContributionRequestResponse contributionRequestResponse = assembleRequestResponse(verificationCardSetIdPath,
							electionEventId);
					return contributionRequestResponse.contributionResponses()
							.stream()
							.parallel()
							.map(contributionResponse -> new ContextAndInputForVerificationCardSetAndControlComponent(
									new VerifyEncryptedExponentiationProofsVerificationCardSetContext.Builder()
											.setEncryptionGroup(electionEventContextPayload.getEncryptionGroup())
											.setJ(contributionResponse.nodeId())
											.setElectionEventId(electionEventId)
											.setVerificationCardSetId(verificationCardSetIdPath.getFileName().toString())
											.setNumberOfVoters(numberOfVoters.get(verificationCardSetIdPath.getFileName().toString()))
											.setNumberOfVotingOptions(
													contributionRequestResponse.contributionRequest().totalNumberOfVotingOptions())
											.build(),
									new VerifyEncryptedExponentiationProofsVerificationCardSetInput(
											contributionRequestResponse.contributionRequest.requestValues(),
											contributionResponse.responseValues()))
							).toList();
				})
				.flatMap(Collection::stream);
	}

	private ContributionRequestResponse assembleRequestResponse(final Path verificationCardSetIdPath,
			final String electionEventId) {

		// Extract requests
		final Map<Integer, SetupComponentVerificationDataPayload> chunkIdToContributionRequests = extractionService.deserializeSetupComponentVerificationDataPayloadOrderByChunkId(
						verificationCardSetIdPath)
				.parallel()
				.collect(Collectors.toConcurrentMap(SetupComponentVerificationDataPayload::getChunkId, Function.identity()));

		// Extract responses
		final List<List<ControlComponentCodeSharesPayload>> contributionResponsesPayloads = extractionService.deserializeControlComponentCodeSharesPayloadsOrderByChunkIdAndNodeId(
				verificationCardSetIdPath);
		verifyControlComponentCodeSharesConsistency(contributionResponsesPayloads, electionEventId,
				verificationCardSetIdPath.getFileName().toString());
		final Map<Integer, List<ControlComponentCodeSharesPayload>> chunkIdToContributionResponses = contributionResponsesPayloads.stream()
				.parallel()
				.filter(chunk -> chunk.stream().findFirst().isPresent())
				.collect(Collectors.toMap(chunk -> chunk.stream().findFirst().get().getChunkId(), Function.identity()));

		checkArgument(chunkIdToContributionResponses.keySet().equals(chunkIdToContributionRequests.keySet()),
				"Mismatch between the SetupComponentVerificationData and ControlComponentCodeShares chunks ids.");

		return new ContributionRequestResponse(
				mergeContributionRequestChunks(chunkIdToContributionRequests),
				mergeContributionResponsesChunks(chunkIdToContributionResponses)
		);
	}

	private List<ContributionResponse> mergeContributionResponsesChunks(
			final Map<Integer, List<ControlComponentCodeSharesPayload>> chunkIdToContributionResponses) {
		return NODE_IDS.stream()
				.map(nodeId -> {
					final List<ControlComponentCodeShare> controlComponentCodeSharesByNode =
							IntStream.range(0, chunkIdToContributionResponses.size())
									.mapToObj(chunkIdToContributionResponses::get)
									.flatMap(Collection::stream)
									.filter(controlComponentCodeSharesPayload -> controlComponentCodeSharesPayload.getNodeId() == nodeId)
									.map(ControlComponentCodeSharesPayload::getControlComponentCodeShares)
									.flatMap(Collection::stream)
									.toList();
					return new ContributionResponse(nodeId, controlComponentCodeSharesByNode);
				}).toList();
	}

	private ContributionRequest mergeContributionRequestChunks(
			final Map<Integer, SetupComponentVerificationDataPayload> chunkIdToContributionRequests) {
		final List<SetupComponentVerificationData> setupComponentVerificationData = IntStream.range(0, chunkIdToContributionRequests.size())
				.mapToObj(chunkId -> chunkIdToContributionRequests.get(chunkId).getSetupComponentVerificationData())
				.flatMap(Collection::stream)
				.toList();

		// We assume the combinedCorrectnessInformation are the same between the contribution request.
		return new ContributionRequest(
				chunkIdToContributionRequests.get(0).getCombinedCorrectnessInformation().getTotalNumberOfVotingOptions(),
				setupComponentVerificationData
		);
	}

	private void verifyControlComponentCodeSharesConsistency(final List<List<ControlComponentCodeSharesPayload>> controlComponentCodeSharesPayloads,
			final String electionEventId, final String verificationCardSetId) {
		checkArgument(!controlComponentCodeSharesPayloads.isEmpty(),
				"There must be at least one control component code share payload. [electionEventId: %s, verificationCardSetId: %s]", electionEventId,
				verificationCardSetId);
		controlComponentCodeSharesPayloads.stream().parallel()
				.forEach(controlComponentCodeSharesPayload -> checkArgument(!controlComponentCodeSharesPayload.isEmpty(),
						"There must be at least one chunk of control component code shares payload. [electionEventId: %s, verificationCardSetId: %s]",
						electionEventId, verificationCardSetId));

		checkArgument(IntStream.range(0, controlComponentCodeSharesPayloads.size())
						.parallel()
						.allMatch(i -> controlComponentCodeSharesPayloads.get(i).size() == NODE_IDS.size()
								&& controlComponentCodeSharesPayloads.get(i).stream()
								.allMatch(nodeContributionResponse -> nodeContributionResponse.getChunkId() == i)),
				"The chunk id sequence is interrupted or incomplete. [electionEventId: %s, verificationCardSetId: %s]", electionEventId,
				verificationCardSetId);

		checkState(controlComponentCodeSharesPayloads.parallelStream()
						.flatMap(Collection::stream)
						.allMatch(payload -> electionEventId.equals(payload.getElectionEventId())
								&& verificationCardSetId.equals(payload.getVerificationCardSetId())),
				"All control component code shares payload must be related to the correct election event id and "
						+ "verification card set id. [electionEventId: %s, verificationCardSetId: %s]", electionEventId, verificationCardSetId);
	}

	public record ContextAndInputForVerificationCardSetAndControlComponent(
			VerifyEncryptedExponentiationProofsVerificationCardSetContext context,
			VerifyEncryptedExponentiationProofsVerificationCardSetInput input) {
	}

	record ContributionResponse(
			int nodeId,
			List<ControlComponentCodeShare> responseValues) {
	}

	record ContributionRequest(
			int totalNumberOfVotingOptions,
			List<SetupComponentVerificationData> requestValues) {
	}

	record ContributionRequestResponse(
			ContributionRequest contributionRequest,
			List<ContributionResponse> contributionResponses) {
	}
}
