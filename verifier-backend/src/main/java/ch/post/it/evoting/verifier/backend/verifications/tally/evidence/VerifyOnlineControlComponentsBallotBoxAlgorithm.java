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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.hasNoDuplicates;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.domain.ControlComponentConstants;
import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTable;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.VerifiableDecryptions;
import ch.post.it.evoting.evotinglibraries.domain.common.ContextIds;
import ch.post.it.evoting.evotinglibraries.domain.common.EncryptedVerifiableVote;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyMixDecInput;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyMixDecOfflineAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyMixDecOfflineContext;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyVotingClientProofsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyVotingClientProofsContext;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyVotingClientProofsInput;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixonline.GetMixnetInitialCiphertextsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixonline.GetMixnetInitialCiphertextsContext;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixonline.GetMixnetInitialCiphertextsInput;

/**
 * Implements the VerifyOnlineControlComponentsBallotBoxAlgorithm algorithm.
 */
@Service
public class VerifyOnlineControlComponentsBallotBoxAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyOnlineControlComponentsBallotBoxAlgorithm.class);

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

	/**
	 * Verifies the proofs of the OnlineControlComponentBallotBoxes.
	 * <p>
	 * Calls the verifyVotingClientProofs and verifyMixDecOffline algorithms.
	 * </p>
	 *
	 * @param context the verification context consisting of
	 *                <ul>
	 *                <li>Encryption group encryptionGroup</li>
	 *                <li>Election event ID ee</li>
	 *                <li>Ballot box ID bb</li>
	 *                <li>Number of selectable voting options ψ ∈ [1, 120]</li>
	 *                <li>Election event context</li>
	 *                </ul>
	 * @param input   the verification input consisting of
	 *                <ul>
	 *                <li>Key-value map of the verification card public keys <b>KMap</b></li>
	 *                <li>First control component ballot box</li>
	 *                <li>Control component shuffles</li>
	 *                </ul>
	 * @return {@code true} if all proofs verify, {@code false} otherwise
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyOnlineControlComponentsBallotBox(
			final VerifyOnlineControlComponentsBallotBoxContext context, final VerifyOnlineControlComponentBallotBoxInput input) {
		checkNotNull(context);
		checkNotNull(input);

		// Context
		final GqGroup encryptionGroup = context.getEncryptionGroup();
		final String ee = context.getElectionEventId();
		final String bb = context.getBallotBoxId();
		final int psi = context.getNumberOfSelectableVotingOptions();
		final PrimesMappingTable pTable = context.getPrimesMappingTable();
		final ElGamalMultiRecipientPublicKey EL_pk = context.getElectionPublicKey();
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> EL_pk_1_to_4 = context.getCcmElectionPublicKeys();
		final ElGamalMultiRecipientPublicKey EB_pk = context.getElectoralBoardPublicKey();
		final ElGamalMultiRecipientPublicKey pk_CCR = context.getChoiceReturnCodesEncryptionPublicKey();
		final int delta_hat = context.getNumberOfAlloweWriteInsPlusOne();

		// Input
		final Map<String, ElGamalMultiRecipientPublicKey> KMap = input.verificationCardPublicKeyMap();
		final ControlComponentBallotBoxPayload firstControlComponentBallotBox = input.firstControlComponentBallotBox();
		// vc_1, E1_1, E1_tilde_1, E2_1, pi_Exp_1, pi_EqEnc_1
		final List<EncryptedVerifiableVote> confirmedEncryptedVotes = firstControlComponentBallotBox.getConfirmedEncryptedVotes();
		final List<ControlComponentShufflePayload> controlComponentShuffles = input.controlComponentShuffles();
		final List<VerifiableShuffle> c_mix_j_pi_mix_j = controlComponentShuffles.stream().parallel()
				.map(ControlComponentShufflePayload::getVerifiableShuffle)
				.toList();
		final List<VerifiableDecryptions> c_dec_j_pi_dec_j = controlComponentShuffles.stream().parallel()
				.map(ControlComponentShufflePayload::getVerifiableDecryptions)
				.toList();

		// Cross-checks
		checkArgument(firstControlComponentBallotBox.getEncryptionGroup().equals(encryptionGroup),
				"The context and input must have the same encryption group.");

		final int N_E = KMap.size();
		final int N_C = confirmedEncryptedVotes.size();
		checkArgument(N_E >= N_C);

		final int nodeIdsSize = ControlComponentConstants.NODE_IDS.size();
		checkArgument(c_mix_j_pi_mix_j.size() == nodeIdsSize, "There must be as many shuffle proofs as there are nodes. [nodeIdsSize: %s]",
				nodeIdsSize);
		checkArgument(c_dec_j_pi_dec_j.size() == nodeIdsSize, "There must be as many decryption proofs as there are nodes. [nodeIdsSize: %s]",
				nodeIdsSize);
		final int N_C_hat = c_mix_j_pi_mix_j.get(0).shuffledCiphertexts().size();
		final int l = c_mix_j_pi_mix_j.get(0).shuffledCiphertexts().getElementSize();

		// Requires
		checkArgument(l == delta_hat,
				"The number element size of the shuffled and the partially decrypted votes must correspond to the number of allowed write ins + 1. [l: %s, delta_hat: %s]",
				l, delta_hat);
		checkArgument(N_C_hat >= 2, "There must be at least 2 shuffled votes.");
		checkArgument(N_C >= 2 ? N_C_hat == N_C : N_C_hat == N_C + 2);
		final List<String> vc_1 = confirmedEncryptedVotes.stream().parallel()
				.map(EncryptedVerifiableVote::contextIds)
				.map(ContextIds::verificationCardId)
				.toList();
		checkArgument(hasNoDuplicates(vc_1), "The verification card IDs must not contain duplicates.");

		// Operation
		final Map<String, ElGamalMultiRecipientCiphertext> vcMap_1 = confirmedEncryptedVotes.stream().parallel()
				.collect(Collectors.toMap(encryptedVerifiableVote -> encryptedVerifiableVote.contextIds().verificationCardId(),
						EncryptedVerifiableVote::encryptedVote));

		final boolean vcProofsVerif;
		if (N_C >= 1) {

			final VerifyVotingClientProofsContext verifyVotingClientProofsContext = new VerifyVotingClientProofsContext.Builder()
					.setEncryptionGroup(encryptionGroup)
					.setElectionEventId(ee)
					.setNumberOfSelectableVotingOptions(psi)
					.setNumberOfAllowedWriteInsPlusOne(delta_hat)
					.setPrimesMappingTable(pTable)
					.build();

			final VerifyVotingClientProofsInput verifyVotingClientProofsInput = new VerifyVotingClientProofsInput.Builder()
					.setEncryptedVerifiableVotes(confirmedEncryptedVotes)
					.setVerificationCardPublicKeys(KMap)
					.setElectionPublicKey(EL_pk)
					.setChoiceReturnCodesEncryptionPublicKey(pk_CCR)
					.build();

			vcProofsVerif = verifyVotingClientProofsAlgorithm.verifyVotingClientProofs(verifyVotingClientProofsContext,
					verifyVotingClientProofsInput);

			if (vcProofsVerif) {
				LOGGER.info("The result of the verifyVotingClientProofs is successful. [ee: {}, bb: {}", ee, bb);
			} else {
				LOGGER.error("The result of the verifyVotingClientProofs is unsuccessful. [ee: {}, bb: {}", ee, bb);
			}
		} else {
			vcProofsVerif = true;
		}

		final GetMixnetInitialCiphertextsContext getMixnetInitialCiphertextsContext = new GetMixnetInitialCiphertextsContext(encryptionGroup, ee, bb);
		final GetMixnetInitialCiphertextsInput getMixnetInitialCiphertextsInput = new GetMixnetInitialCiphertextsInput(delta_hat, vcMap_1, EL_pk);
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_init_1 = getMixnetInitialCiphertextsAlgorithm.getMixnetInitialCiphertexts(
				getMixnetInitialCiphertextsContext, getMixnetInitialCiphertextsInput);

		final VerifyMixDecOfflineContext verifyMixDecOfflineContext = new VerifyMixDecOfflineContext(encryptionGroup, ee, bb, delta_hat);
		final VerifyMixDecInput verifyMixDecInput = new VerifyMixDecInput(c_init_1, c_mix_j_pi_mix_j, c_dec_j_pi_dec_j, EL_pk, EL_pk_1_to_4, EB_pk);

		final boolean shuffleProofsVerif = verifyMixDecOfflineAlgorithm.verifyMixDecOffline(verifyMixDecOfflineContext, verifyMixDecInput);

		if (shuffleProofsVerif) {
			LOGGER.info("The result of the verifyMixDecOffline is successful. [ee: {}, bb: {}", ee, bb);
		} else {
			LOGGER.error("The result of the verifyMixDecOffline is unsuccessful. [ee: {}, bb: {}", ee, bb);
		}

		return vcProofsVerif && shuffleProofsVerif;
	}

}
