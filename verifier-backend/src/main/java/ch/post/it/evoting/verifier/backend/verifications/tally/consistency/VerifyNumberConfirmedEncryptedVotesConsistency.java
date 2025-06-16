/*
 * (c) Copyright 2025 Swiss Post Ltd.
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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.verifier.backend.tools.TranslationHelper.getFromResourceBundle;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.TallyComponentShufflePayload;
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
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;

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
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification811.description"));
		definition.setId("08.11");
		definition.setName("VerifyNumberConfirmedEncryptedVotesConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final boolean isNumberConfirmedEncryptedVotesConsistent = extractTallyPayloadsSizes(inputDirectoryPath).stream()
				.parallel()
				.map(this::verifyNumberConfirmedEncryptedVotesConsistency)
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (isNumberConfirmedEncryptedVotesConsistent) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification811.nok.message"));
		}
	}

	@SuppressWarnings("java:S117")
	private boolean verifyNumberConfirmedEncryptedVotesConsistency(final TallyPayloadsSizes tallyPayloadsSizes) {

		// Operation.
		final int N_C_TallyControlComponentVotes = tallyPayloadsSizes.numberOfConfirmedVotesTallyComponentVotes();
		final ImmutableList<Integer> numberOfConfirmedVotesControlComponentBallotBoxes = tallyPayloadsSizes.numberOfConfirmedVotesControlComponentBallotBoxes();
		final boolean identicalNumberOfConfirmedVotes = numberOfConfirmedVotesControlComponentBallotBoxes.stream()
				.parallel()
				.allMatch(N_C_ControlComponentBallotBox -> N_C_ControlComponentBallotBox == N_C_TallyControlComponentVotes);

		final int N_C_hat_TallyControlComponentShuffle = tallyPayloadsSizes.numberOfMixedVotesTallyComponentShuffles();
		final ImmutableList<Integer> numberOfMixedVotesControlComponentShuffles = tallyPayloadsSizes.numberOfMixedVotesControlComponentShuffles();
		final boolean identicalNumberOfMixedVotes = numberOfMixedVotesControlComponentShuffles.stream()
				.parallel()
				.allMatch(N_C_hat_OnlineControlComponentShuffle -> N_C_hat_OnlineControlComponentShuffle == N_C_hat_TallyControlComponentShuffle);

		final int N_C = N_C_TallyControlComponentVotes;
		final int N_C_hat = N_C_hat_TallyControlComponentShuffle;
		final boolean correctCorrespondanceConfirmedVotesAndMixedVotes =
				// If there are less than 2 confirmed votes, TallyComponentShufflePayload and ControlComponentShufflePayload contain 2 dummy votes.
				(N_C < 2) ?
						N_C_hat == N_C + 2 :
						N_C_hat == N_C;

		return identicalNumberOfConfirmedVotes
				&& identicalNumberOfMixedVotes
				&& correctCorrespondanceConfirmedVotesAndMixedVotes;
	}

	private ImmutableList<TallyPayloadsSizes> extractTallyPayloadsSizes(final Path inputDirectoryPath) {
		final PathNode ballotBoxesPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxesPathNode.getRegexPaths().stream()
				.parallel()
				.map(ballotBoxPath -> {
					final ImmutableList<Integer> numberOfConfirmedVotesControlComponentBallotBoxes = electionDataExtractionService.getControlComponentBallotBoxPayloadsOrderedByNodeId(
									ballotBoxPath)
							.parallel()
							.map(controlComponentBallotBoxPayload -> controlComponentBallotBoxPayload.getConfirmedEncryptedVotes().size())
							.collect(toImmutableList());

					final ImmutableList<Integer> numberOfMixedVotesControlComponentShuffles = electionDataExtractionService.getControlComponentShufflePayloadsOrderedByNodeId(
									ballotBoxPath)
							.parallel()
							.map(controlComponentShufflePayload -> controlComponentShufflePayload.getVerifiableShuffle().shuffledCiphertexts().size())
							.collect(toImmutableList());

					final TallyComponentShufflePayload tallyComponentShufflePayload =
							electionDataExtractionService.getTallyComponentShufflePayload(ballotBoxPath);
					final int numberOfMixedVotesTallyComponentShuffles = tallyComponentShufflePayload.getVerifiableShuffle().shuffledCiphertexts()
							.size();

					final TallyComponentVotesPayload tallyComponentVotesPayload =
							electionDataExtractionService.getTallyComponentVotesPayload(ballotBoxPath);
					final int numberOfConfirmedVotesTallyComponentVotes = tallyComponentVotesPayload.getDecryptedVotes().size();

					return new TallyPayloadsSizes(numberOfConfirmedVotesControlComponentBallotBoxes, numberOfMixedVotesControlComponentShuffles,
							numberOfMixedVotesTallyComponentShuffles, numberOfConfirmedVotesTallyComponentVotes
					);
				}).collect(toImmutableList());
	}

	private record TallyPayloadsSizes(ImmutableList<Integer> numberOfConfirmedVotesControlComponentBallotBoxes,
									  ImmutableList<Integer> numberOfMixedVotesControlComponentShuffles,
									  int numberOfMixedVotesTallyComponentShuffles,
									  int numberOfConfirmedVotesTallyComponentVotes
	) {
		private TallyPayloadsSizes {
			checkNotNull(numberOfConfirmedVotesControlComponentBallotBoxes);
			checkNotNull(numberOfMixedVotesControlComponentShuffles);
			checkArgument(numberOfMixedVotesTallyComponentShuffles >= 0);
			checkArgument(numberOfConfirmedVotesTallyComponentVotes >= 0);
		}
	}

}
