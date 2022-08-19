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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.domain.mixnet.VerifiablePlaintextDecryption;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientMessage;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.cryptoprimitives.math.ZqGroup;
import ch.post.it.evoting.cryptoprimitives.mixnet.Mixnet;
import ch.post.it.evoting.cryptoprimitives.mixnet.ShuffleArgument;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.cryptoprimitives.utils.VerificationResult;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.DecryptionProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.VerifiableDecryptions;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProof;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.VerifyProcessPlaintextsAlgorithm;

@Service
public class VerifyTallyControlComponentBallotBoxAlgorithm {

	private final Mixnet mixnet;
	private final ZeroKnowledgeProof zeroKnowledgeProof;
	private final VerifyProcessPlaintextsAlgorithm verifyProcessPlaintextsAlgorithm;

	public VerifyTallyControlComponentBallotBoxAlgorithm(final Mixnet mixnet, final ZeroKnowledgeProof zeroKnowledgeProof, final ElGamal elGamal) {
		this.mixnet = mixnet;
		this.zeroKnowledgeProof = zeroKnowledgeProof;
		this.verifyProcessPlaintextsAlgorithm = new VerifyProcessPlaintextsAlgorithm(elGamal);
	}

	/**
	 * Verifies the proofs of the TallyControlComponentBallotBox.
	 * <p>
	 * The following proofs are verified:
	 *     <ul>
	 *         <li>shuffle argument</li>
	 *         <li>decryption proofs</li>
	 *         <li>plaintext equality proofs</li>
	 *     </ul>
	 * </p>
	 *
	 * @param context the context containing the election event ID and the ballot box ID. Non-null.
	 * @param input   the input containing the votes and the proofs to be verified. Non-null.
	 * @return {@code true} if all proofs verify, {@code false} otherwise
	 * @throws NullPointerException if the context or the input is null.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyTallyControlComponentBallotBox(final VerifyTallyControlComponentBallotBoxContext context,
			final VerifyTallyControlComponentBallotBoxInput input) {
		checkNotNull(context);
		checkNotNull(input);

		final String ee = context.electionEventId();
		final String bb = context.ballotBoxId();

		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_dec_4 = input.getPreviousPartiallyDecryptedVotes();
		final VerifiableShuffle c_mix_5_pi_mix_5 = input.getVerifiableShuffle();
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_mix_5 = c_mix_5_pi_mix_5.shuffledCiphertexts();
		final ShuffleArgument pi_mix_5 = c_mix_5_pi_mix_5.shuffleArgument();
		final VerifiableDecryptions m_pi_dec_5 = transformToVerifiableDecryptions(input.getVerifiablePlaintextDecryption());
		final ElGamalMultiRecipientPublicKey EB_pk = input.getElectoralBoardPublicKey();
		final List<String> i_aux = List.of(ee, bb, "MixDecOffline");

		final GroupVector<PrimeGqElement, GqGroup> p_tilde = input.getEncodedVotingOptions();
		final GroupVector<ElGamalMultiRecipientMessage, GqGroup> m = input.getVerifiablePlaintextDecryption().getDecryptedVotes();
		final int Psi = input.getNumberOfSelectableVotingOptions();
		final int delta_hat = input.getNumberOfAllowedWriteIns() + 1;
		final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> L_votes = input.getSelectedEncodedVotingOptions();

		final VerificationResult shuffleVerif = mixnet.verifyShuffle(c_dec_4, c_mix_5, pi_mix_5, EB_pk);
		final VerificationResult decryptVerif = zeroKnowledgeProof.verifyDecryptions(c_mix_5, EB_pk, m_pi_dec_5, i_aux);
		final boolean processVerif = verifyProcessPlaintextsAlgorithm.verifyProcessPlaintexts(p_tilde, m, Psi, delta_hat, L_votes);

		return shuffleVerif.isVerified() && decryptVerif.isVerified() && processVerif;
	}

	private VerifiableDecryptions transformToVerifiableDecryptions(final VerifiablePlaintextDecryption verifiablePlaintextDecryption) {
		checkNotNull(verifiablePlaintextDecryption);
		final GqGroup group = verifiablePlaintextDecryption.getGroup();
		final GroupVector<ElGamalMultiRecipientMessage, GqGroup> decryptedVotes = verifiablePlaintextDecryption.getDecryptedVotes();
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> ciphertexts = decryptedVotes.stream()
				.map(vote -> ElGamalMultiRecipientCiphertext.create(group.getIdentity(), vote.getElements()))
				.collect(GroupVector.toGroupVector());
		final GroupVector<DecryptionProof, ZqGroup> decryptionProofs = verifiablePlaintextDecryption.getDecryptionProofs();

		return new VerifiableDecryptions(ciphertexts, decryptionProofs);
	}
}
