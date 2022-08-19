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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence.algorithms;

import static ch.post.it.evoting.cryptoprimitives.domain.ControlComponentConstants.NODE_IDS;
import static ch.post.it.evoting.cryptoprimitives.utils.Validations.allEqual;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.cryptoprimitives.domain.election.ControlComponentPublicKeys;
import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.VerifiableDecryptions;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.VerifyMixDecInput;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.VerifyMixDecOfflineAlgorithm;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.VerifyMixDecOfflineContext;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.VerifyVotingClientProofsAlgorithm;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.VerifyVotingClientProofsContext;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.VerifyVotingClientProofsInput;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixonline.GetMixnetInitialCiphertextsAlgorithm;
import ch.post.it.evoting.verifier.protocol.domain.EncryptedVerifiableVote;
import ch.post.it.evoting.verifier.protocol.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

/**
 * Implements the VerifyOnlineControlComponentsBallotBoxAlgorithm algorithm.
 */
@Service
public class VerifyOnlineControlComponentsBallotBoxAlgorithm {

	private final VerifyMixDecOfflineAlgorithm verifyMixDecOfflineAlgorithm;
	private final VerifyVotingClientProofsAlgorithm verifyVotingClientProofsAlgorithm;
	private final GetMixnetInitialCiphertextsAlgorithm getMixnetInitialCiphertextsAlgorithm;

	public VerifyOnlineControlComponentsBallotBoxAlgorithm(
			final VerifyMixDecOfflineAlgorithm verifyMixDecOfflineAlgorithm,
			final VerifyVotingClientProofsAlgorithm verifyVotingClientProofsAlgorithm,
			final GetMixnetInitialCiphertextsAlgorithm getMixnetInitialCiphertextsAlgorithm) {
		this.verifyMixDecOfflineAlgorithm = verifyMixDecOfflineAlgorithm;
		this.verifyVotingClientProofsAlgorithm = verifyVotingClientProofsAlgorithm;
		this.getMixnetInitialCiphertextsAlgorithm = getMixnetInitialCiphertextsAlgorithm;
	}

	public boolean verifyOnlineControlComponentsBallotBox(
			final ElectionEventContext electionEventContext, final String ballotBoxId, final int numberOfSelectableVotingOptions,
			final List<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloads,
			final List<ControlComponentShufflePayload> controlComponentShufflePayloads,
			final SetupComponentTallyDataPayload setupComponentTallyDataPayload) {

		// Verify mixnet payloads consistency.
		verifyConsistency(controlComponentBallotBoxPayloads, controlComponentShufflePayloads);

		// After consistency checks, we know all payloads have the same votes, so we can pick one (first here).
		final List<EncryptedVerifiableVote> confirmedEncryptedVotes = controlComponentBallotBoxPayloads.get(0).getConfirmedEncryptedVotes();

		final int numberOfAllowedWriteInsPlusOne = electionEventContext.verificationCardSetContexts().stream()
				.filter(verificationCardSetContext -> verificationCardSetContext.ballotBoxId().equals(ballotBoxId))
				.collect(MoreCollectors.onlyElement())
				.numberOfWriteInFields() + 1;

		final String electionEventId = electionEventContext.electionEventId();
		final ElGamalMultiRecipientPublicKey electionPublicKey = electionEventContext.electionPublicKey();

		// Verify voting client proofs.
		// The algorithm VerifyVotingClientProofs runs with at least one confirmed vote.
		final boolean vcProofsVerif;
		if (!confirmedEncryptedVotes.isEmpty()) {
			final List<String> verificationCardIds = setupComponentTallyDataPayload.getVerificationCardIds();
			final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> verificationCardPublicKeys = setupComponentTallyDataPayload.getVerificationCardPublicKeys();
			final Map<String, ElGamalMultiRecipientPublicKey> verificationCardPublicKeysMap = IntStream.range(0, verificationCardIds.size()).boxed()
					.collect(Collectors.toMap(verificationCardIds::get, verificationCardPublicKeys::get));

			final VerifyVotingClientProofsContext verifyVotingClientProofsContext = new VerifyVotingClientProofsContext.Builder()
					.setElectionEventId(electionEventId)
					.setNumberOfSelectableVotingOptions(numberOfSelectableVotingOptions)
					.setNumberOfAllowedWriteInsPlusOne(numberOfAllowedWriteInsPlusOne)
					.build();

			final VerifyVotingClientProofsInput verifyVotingClientProofsInput = new VerifyVotingClientProofsInput.Builder()
					.setEncryptedVerifiableVotes(confirmedEncryptedVotes)
					.setVerificationCardPublicKeys(verificationCardPublicKeysMap)
					.setElectionPublicKey(electionPublicKey)
					.setChoiceReturnCodesEncryptionPublicKey(electionEventContext.choiceReturnCodesEncryptionPublicKey())
					.build();

			vcProofsVerif = verifyVotingClientProofsAlgorithm.verifyVotingClientProofs(verifyVotingClientProofsContext,
					verifyVotingClientProofsInput);
		} else {
			vcProofsVerif = true;
		}

		// Verify shuffle proofs.
		final List<VerifiableShuffle> precedingVerifiableShuffledVotes = controlComponentShufflePayloads.stream()
				.map(ControlComponentShufflePayload::getVerifiableShuffle)
				.toList();
		final List<VerifiableDecryptions> precedingVerifiableDecryptedVotes = controlComponentShufflePayloads.stream()
				.map(ControlComponentShufflePayload::getVerifiableDecryptions)
				.toList();

		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> ccmElectionPublicKey = electionEventContext.combinedControlComponentPublicKeys()
				.stream()
				.map(ControlComponentPublicKeys::ccmElectionPublicKey)
				.collect(GroupVector.toGroupVector());
		final ElGamalMultiRecipientPublicKey electoralBoardPublicKey = electionEventContext.electoralBoardPublicKey();

		final Map<String, ElGamalMultiRecipientCiphertext> confirmedEncryptedVotesMap = confirmedEncryptedVotes.stream()
				.collect(Collectors.toMap(encryptedVerifiableVote -> encryptedVerifiableVote.contextIds().verificationCardId(),
						EncryptedVerifiableVote::encryptedVote));

		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> initialCiphertexts = getMixnetInitialCiphertextsAlgorithm.getMixnetInitialCiphertexts(
				numberOfAllowedWriteInsPlusOne, confirmedEncryptedVotesMap, electionPublicKey);

		final VerifyMixDecOfflineContext verifyMixDecOfflineContext = new VerifyMixDecOfflineContext(electionEventId, ballotBoxId,
				numberOfAllowedWriteInsPlusOne);
		final VerifyMixDecInput verifyMixDecInput = new VerifyMixDecInput(initialCiphertexts, precedingVerifiableShuffledVotes,
				precedingVerifiableDecryptedVotes, electionPublicKey, ccmElectionPublicKey, electoralBoardPublicKey);

		final boolean shuffleProofsVerif = verifyMixDecOfflineAlgorithm.verifyMixDecOffline(verifyMixDecOfflineContext, verifyMixDecInput);

		return vcProofsVerif && shuffleProofsVerif;
	}

	private void verifyConsistency(final List<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloads,
			final List<ControlComponentShufflePayload> controlComponentShufflePayloads) {

		checkState(allEqual(controlComponentBallotBoxPayloads.stream(), ControlComponentBallotBoxPayload::getEncryptionGroup),
				"All control component ballot box payloads must have the same group.");
		checkState(allEqual(controlComponentBallotBoxPayloads.stream(), ControlComponentBallotBoxPayload::getElectionEventId),
				"All control component ballot box payloads must have the same election event id.");
		checkState(allEqual(controlComponentBallotBoxPayloads.stream(), ControlComponentBallotBoxPayload::getBallotBoxId),
				"All control component ballot box payloads must have the same ballot box id.");
		checkState(allEqual(controlComponentBallotBoxPayloads.stream(), ControlComponentBallotBoxPayload::getConfirmedEncryptedVotes),
				"All control component ballot box payloads must have the same confirmed encrypted votes.");

		final List<Integer> ballotBoxPayloadsNodeIds = controlComponentBallotBoxPayloads.stream()
				.map(ControlComponentBallotBoxPayload::getNodeId)
				.toList();
		checkState(NODE_IDS.size() == ballotBoxPayloadsNodeIds.size() && NODE_IDS.equals(new HashSet<>(ballotBoxPayloadsNodeIds)),
				"Wrong number of control component ballot box payloads.");

		checkState(allEqual(controlComponentShufflePayloads.stream(), ControlComponentShufflePayload::getEncryptionGroup),
				"All control component shuffle payloads must have the same group.");
		checkState(allEqual(controlComponentShufflePayloads.stream(), ControlComponentShufflePayload::getElectionEventId),
				"All control component shuffle payloads must have the same election event id.");
		checkState(allEqual(controlComponentShufflePayloads.stream(), ControlComponentShufflePayload::getBallotBoxId),
				"All control component shuffle payloads must have the same ballot box id.");

		final List<Integer> shufflePayloadsNodeIds = controlComponentShufflePayloads.stream()
				.map(ControlComponentShufflePayload::getNodeId)
				.toList();
		checkState(NODE_IDS.size() == shufflePayloadsNodeIds.size() && NODE_IDS.equals(new HashSet<>(shufflePayloadsNodeIds)),
				"Wrong number of control component shuffle payloads.");

		// Cross-checks.
		checkState(controlComponentBallotBoxPayloads.get(0).getEncryptionGroup().equals(controlComponentShufflePayloads.get(0).getEncryptionGroup()),
				"The control component ballot box and shuffle payloads must have the same group.");
		checkState(controlComponentBallotBoxPayloads.get(0).getElectionEventId().equals(controlComponentShufflePayloads.get(0).getElectionEventId()),
				"The control component ballot box and shuffle payloads must have the same election event id.");
		checkState(controlComponentBallotBoxPayloads.get(0).getBallotBoxId().equals(controlComponentShufflePayloads.get(0).getBallotBoxId()),
				"The control component ballot box and shuffle payloads must have the same ballot box id.");
	}

}
