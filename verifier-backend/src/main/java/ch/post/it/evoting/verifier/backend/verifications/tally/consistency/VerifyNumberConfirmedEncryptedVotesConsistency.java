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

import static ch.post.it.evoting.verifier.backend.tools.TranslationHelper.getFromResourceBundle;

import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.TallyComponentShufflePayload;
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
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.verifier.protocol.domain.tally.TallyComponentVotesPayload;

@Component
public class VerifyNumberConfirmedEncryptedVotesConsistency extends AbstractVerification {

	private final PathService pathService;
	private final ElectionDataExtractionService electionDataExtractionService;

	public VerifyNumberConfirmedEncryptedVotesConsistency(final ResultPublisherService resultPublisherService,
			final PathService pathService, final ElectionDataExtractionService electionDataExtractionService) {
		super(resultPublisherService);
		this.pathService = pathService;
		this.electionDataExtractionService = electionDataExtractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification306.description"));
		definition.setId(306);
		definition.setName("VerifyNumberConfirmedEncryptedVotesConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final boolean isNumberConfirmedEncryptedVotesConsistent = extractTallyPayloadsSizes(inputDirectoryPath).stream()
				.parallel()
				.map(this::verifyConsistency)
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (isNumberConfirmedEncryptedVotesConsistent) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification306.nok.message"));
		}
	}

	private List<TallyPayloadsSizes> extractTallyPayloadsSizes(final Path inputDirectoryPath) {
		final PathNode ballotBoxesPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxesPathNode.getRegexPaths().stream()
				.parallel()
				.map(ballotBoxPath -> {
					final TallyComponentVotesPayload tallyComponentVotesPayload =
							electionDataExtractionService.getTallyComponentVotesPayload(ballotBoxPath);
					final int tallyComponentVotesPayloadSize = tallyComponentVotesPayload.getVotes().size();

					final TallyComponentShufflePayload tallyComponentShufflePayload =
							electionDataExtractionService.getTallyComponentShufflePayload(ballotBoxPath);
					final int tallyComponentShufflePayloadSize = tallyComponentShufflePayload.getVerifiableShuffle().shuffledCiphertexts().size();

					final List<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloads =
							electionDataExtractionService.getControlComponentBallotBoxPayloadsOrderedByNodeId(ballotBoxPath);
					final List<Integer> controlComponentBallotBoxPayloadsSizes = controlComponentBallotBoxPayloads.stream()
							.map(controlComponentBallotBoxPayload -> controlComponentBallotBoxPayload.getConfirmedEncryptedVotes().size())
							.toList();

					final List<ControlComponentShufflePayload> controlComponentShufflePayloads =
							electionDataExtractionService.getControlComponentShufflePayloadsOrderedByNodeId(ballotBoxPath);
					final List<Integer> controlComponentShufflePayloadsSizes = controlComponentShufflePayloads.stream()
							.map(controlComponentShufflePayload -> controlComponentShufflePayload.getVerifiableShuffle().shuffledCiphertexts().size())
							.toList();

					return new TallyPayloadsSizes(tallyComponentVotesPayloadSize, tallyComponentShufflePayloadSize,
							controlComponentBallotBoxPayloadsSizes, controlComponentShufflePayloadsSizes);
				}).toList();
	}

	private boolean verifyConsistency(final TallyPayloadsSizes tallyPayloadsSizes) {
		final int tallyComponentVotesPayloadSize = tallyPayloadsSizes.tallyComponentVotesPayloadSize();
		final int tallyComponentShufflePayloadSize = tallyPayloadsSizes.tallyComponentShufflePayloadSize();
		final List<Integer> controlComponentBallotBoxPayloadsSizes = tallyPayloadsSizes.controlComponentBallotBoxPayloadsSizes();
		final List<Integer> controlComponentShufflePayloadsSizes = tallyPayloadsSizes.controlComponentShufflePayloadsSizes();
		final int expectedSize = tallyComponentVotesPayloadSize;

		final boolean isTallyComponentShufflePayloadConsistent =
				// TallyComponentShufflePayload contains 2 dummy votes for the case there is less than 2 actual votes.
				(expectedSize < 2) ? tallyComponentShufflePayloadSize == expectedSize + 2 : tallyComponentShufflePayloadSize == expectedSize;

		final boolean areControlComponentBallotBoxPayloadsConsistent = controlComponentBallotBoxPayloadsSizes.stream()
				.parallel()
				.allMatch(controlComponentBallotBoxPayloadsSize -> controlComponentBallotBoxPayloadsSize == expectedSize);

		final boolean areControlComponentShufflePayloadsConsistent = controlComponentShufflePayloadsSizes.stream()
				.parallel()
				// controlComponentShufflePayload contains 2 dummy votes for the case there is less than 2 actual votes.
				.allMatch(controlComponentShufflePayloadsSize -> (expectedSize < 2) ?
						controlComponentShufflePayloadsSize == expectedSize + 2 : controlComponentShufflePayloadsSize == expectedSize);

		return isTallyComponentShufflePayloadConsistent
				&& areControlComponentBallotBoxPayloadsConsistent
				&& areControlComponentShufflePayloadsConsistent;
	}

	private record TallyPayloadsSizes(int tallyComponentVotesPayloadSize,
									  int tallyComponentShufflePayloadSize,
									  List<Integer> controlComponentBallotBoxPayloadsSizes,
									  List<Integer> controlComponentShufflePayloadsSizes) {
	}

}
