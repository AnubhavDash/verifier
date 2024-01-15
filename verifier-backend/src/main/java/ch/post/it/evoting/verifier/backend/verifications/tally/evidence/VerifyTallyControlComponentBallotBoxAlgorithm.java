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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientMessage;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.cryptoprimitives.mixnet.Mixnet;
import ch.post.it.evoting.cryptoprimitives.mixnet.ShuffleArgument;
import ch.post.it.evoting.cryptoprimitives.utils.VerificationResult;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.VerifiableDecryptions;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProof;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.votingoptions.PrimesMappingTableAlgorithms;

@Service
public class VerifyTallyControlComponentBallotBoxAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyTallyControlComponentBallotBoxAlgorithm.class);

	private final Mixnet mixnet;
	private final ZeroKnowledgeProof zeroKnowledgeProof;
	private final PrimesMappingTableAlgorithms primesMappingTableAlgorithms;
	private final VerifyProcessPlaintextsAlgorithm verifyProcessPlaintextsAlgorithm;

	public VerifyTallyControlComponentBallotBoxAlgorithm(final Mixnet mixnet, final ZeroKnowledgeProof zeroKnowledgeProof,
			final PrimesMappingTableAlgorithms primesMappingTableAlgorithms1,
			final VerifyProcessPlaintextsAlgorithm verifyProcessPlaintextsAlgorithm) {
		this.mixnet = mixnet;
		this.zeroKnowledgeProof = zeroKnowledgeProof;
		this.primesMappingTableAlgorithms = primesMappingTableAlgorithms1;
		this.verifyProcessPlaintextsAlgorithm = verifyProcessPlaintextsAlgorithm;
	}

	/**
	 * Verifies the proofs of the TallyControlComponentBallotBox.
	 * <p>
	 * The following proofs are verified:
	 *     <ul>
	 *         <li>shuffle argument</li>
	 *         <li>decryption proofs</li>
	 *         <li>correct processing of plaintexts</li>
	 *     </ul>
	 * </p>
	 *
	 * @param context the context as a {@link VerifyTallyControlComponentBallotBoxContext}. Non-null.
	 * @param input   the input as a {@link VerifyTallyControlComponentBallotBoxInput}. Non-null.
	 * @return {@code true} if all proofs verify, {@code false} otherwise
	 * @throws NullPointerException if the context or the input is null.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyTallyControlComponentBallotBox(final VerifyTallyControlComponentBallotBoxContext context,
			final VerifyTallyControlComponentBallotBoxInput input) {
		checkNotNull(context);
		checkNotNull(input);

		// Cross-group check.
		checkArgument(context.getEncryptionGroup().equals(input.getPreviousPartiallyDecryptedVotes().getGroup()),
				"The context and input should have the same encryption group.");

		// Context.
		final GqGroup encryptionGroup = context.getEncryptionGroup();
		final String ee = context.getElectionEventId();
		final String bb = context.getBallotBoxId();
		final PrimesMappingTable pTable = context.getPrimesMappingTable();
		final int psi = primesMappingTableAlgorithms.getPsi(pTable);
		final int delta_hat = primesMappingTableAlgorithms.getDeltaHat(pTable);
		final ElGamalMultiRecipientPublicKey EB_pk = context.getElectoralBoardPublicKey();

		// Input.
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_dec_4 = input.getPreviousPartiallyDecryptedVotes();
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_mix_5 = input.getShuffledVotes();
		final ShuffleArgument pi_mix_5 = input.getShuffleProofs();
		final GroupVector<ElGamalMultiRecipientMessage, GqGroup> m = input.getVerifiablePlaintextDecryption().getDecryptedVotes();
		final VerifiableDecryptions pi_dec_5 = input.getVerifiableDecryptions();
		final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> L_votes = input.getSelectedEncodedVotingOptions();
		final List<List<String>> L_decodedVotes = input.getSelectedDecodedVotingOptions();
		final List<List<String>> L_writeIns = input.getSelectedDecodedWriteInVotes();

		// Cross-checks.
		if (!L_votes.isEmpty()) {
			checkArgument(L_votes.getElementSize() == psi,
					"The size of the p_i_hat elements and v_i_hat elements should be equal to the number of selectable encoded voting options.");
			// It is ensured by the GroupVector class that all elements in L_votes have the same size.
		}
		checkArgument(c_dec_4.getElementSize() == delta_hat,
				"All shuffled, partially decrypted and decrypted votes must be of size delta_hat. [l: %s, delta_hat: %s]", c_dec_4.getElementSize(),
				delta_hat);

		// Require.
		final int N_C_hat = c_dec_4.size();
		final int N_C = L_votes.size();
		checkArgument(N_C_hat >= 2, "The number of mixed votes must be greater than or equal to 2.");
		checkArgument((N_C_hat == N_C) || (N_C_hat == N_C + 2 && N_C < 2),
				"The number of mixed votes must be equal to the number of processed votes, if the number of confirmed votes is 2 or greater. "
						+ "Otherwise, there must be two more mixed votes than confirmed votes (for N_C = 0 or 1).");
		final GroupVector<PrimeGqElement, GqGroup> p_tilde = primesMappingTableAlgorithms.getEncodedVotingOptions(pTable, List.of());
		checkArgument(L_votes.stream().parallel().allMatch(p_tilde::containsAll),
				"All selected voting options must be a subset of the total voting options.");
		L_votes.forEach(p_i_hat -> checkArgument(p_i_hat.stream().parallel().distinct().count() == p_i_hat.size(),
				"All selected encoded voting options in a vote must be distinct."));

		// Operation.
		final List<String> i_aux = List.of(ee, bb, "MixDecOffline");

		final ElGamalMultiRecipientPublicKey EB_pk_cut = new ElGamalMultiRecipientPublicKey(
				GroupVector.from(EB_pk.getKeyElements().subList(0, delta_hat)));

		final VerificationResult shuffleVerif = mixnet.verifyShuffle(c_dec_4, c_mix_5, pi_mix_5, EB_pk_cut);
		if (!shuffleVerif.isVerified()) {
			LOGGER.error("The shuffle proofs are invalid. [ee: {}, bb: {}, errorMessage: {}]", ee, bb, shuffleVerif.getErrorMessages().getFirst());
		} else {
			LOGGER.info("The shuffle proofs are valid. [ee: {}, bb: {}]", ee, bb);
		}

		final VerificationResult decryptVerif = zeroKnowledgeProof.verifyDecryptions(c_mix_5, EB_pk_cut, pi_dec_5, i_aux);
		if (!decryptVerif.isVerified()) {
			LOGGER.error("The decryption proofs are invalid. [ee: {}, bb: {}, errorMessage: {}]", ee, bb, decryptVerif.getErrorMessages().getFirst());
		} else {
			LOGGER.info("The decryption proofs are valid. [ee: {}, bb: {}]", ee, bb);
		}

		final VerifyProcessPlaintextsContext verifyProcessPlaintextsContext = new VerifyProcessPlaintextsContext(encryptionGroup, ee, bb, pTable);
		final boolean processVerif = verifyProcessPlaintextsAlgorithm.verifyProcessPlaintexts(verifyProcessPlaintextsContext,
				new VerifyProcessPlaintextsInput.Builder()
						.setPlaintextVotes(m)
						.setSelectedEncodedVotingOptions(L_votes)
						.setSelectedDecodedVotingOptions(L_decodedVotes)
						.setSelectedDecodedWriteInVotes(L_writeIns)
						.build());
		if (!processVerif) {
			LOGGER.error("The process plaintexts verification failed. [ee: {}, bb: {}]", ee, bb);
		} else {
			LOGGER.info("The process plaintexts verification succeeded. [ee: {}, bb: {}]", ee, bb);
		}

		return shuffleVerif.isVerified() && decryptVerif.isVerified() && processVerif;
	}
}
