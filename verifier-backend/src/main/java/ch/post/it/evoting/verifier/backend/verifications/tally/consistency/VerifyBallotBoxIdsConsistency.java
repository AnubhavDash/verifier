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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

@Component
public class VerifyBallotBoxIdsConsistency extends AbstractVerification {

	private final PathService pathService;
	private final ElectionDataExtractionService extractionService;

	protected VerifyBallotBoxIdsConsistency(final ResultPublisherService resultPublisherService, final PathService pathService,
			final ElectionDataExtractionService extractionService) {
		super(resultPublisherService);
		this.pathService = pathService;
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification304.description"));
		definition.setId("08.05");
		definition.setName("VerifyBallotBoxIdsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final boolean sameBallotBoxIds = extractBallotBoxIds(inputDirectoryPath).stream()
				.parallel()
				.map(payloadsBallotBoxIds -> payloadsBallotBoxIds.ballotBoxId().equals(payloadsBallotBoxIds.ccBallotBoxIds())
						&& payloadsBallotBoxIds.ballotBoxId().equals(payloadsBallotBoxIds.ccShuffleIds())
						&& payloadsBallotBoxIds.ballotBoxId().equals(payloadsBallotBoxIds.tcShuffleId())
						&& payloadsBallotBoxIds.ballotBoxId().equals(payloadsBallotBoxIds.tcVotesId()))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (sameBallotBoxIds) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification304.nok.message"));
		}
	}

	private List<PayloadsBallotBoxIds> extractBallotBoxIds(final Path inputDirectoryPath) {
		final PathNode ballotBoxIds = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
		return ballotBoxIds.getRegexPaths().stream()
				.parallel()
				.map(ballotBoxIdPath -> {
					final String bb = ballotBoxIdPath.getFileName().toString();
					final Set<String> ballotBoxId = Set.of(bb);

					final Set<String> controlComponentBallotBoxIds = extractionService.getControlComponentBallotBoxPayloadsOrderedByNodeId(
									inputDirectoryPath, bb)
							.map(ControlComponentBallotBoxPayload::getBallotBoxId)
							.collect(Collectors.toSet());

					final Set<String> controlComponentShuffleIds = extractionService.getControlComponentShufflePayloadsOrderedByNodeId(
									inputDirectoryPath, bb)
							.parallel()
							.map(ControlComponentShufflePayload::getBallotBoxId)
							.collect(Collectors.toSet());

					final TallyComponentShufflePayload tallyComponentShufflePayload = extractionService.getTallyComponentShufflePayload(
							inputDirectoryPath, bb);
					final Set<String> tallyComponentShufflePayloadId = Set.of(tallyComponentShufflePayload.getBallotBoxId());

					final TallyComponentVotesPayload tallyComponentVotesPayload = extractionService.getTallyComponentVotesPayload(ballotBoxIdPath);
					final Set<String> tallyComponentVotesPayloadId = Set.of(tallyComponentVotesPayload.getBallotBoxId());

					return new PayloadsBallotBoxIds(ballotBoxId, controlComponentBallotBoxIds, controlComponentShuffleIds,
							tallyComponentShufflePayloadId, tallyComponentVotesPayloadId);
				})
				.toList();
	}

	private record PayloadsBallotBoxIds(Set<String> ballotBoxId, Set<String> ccBallotBoxIds, Set<String> ccShuffleIds, Set<String> tcShuffleId,
										Set<String> tcVotesId) {
	}
}
