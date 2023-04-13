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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTable;
import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTableEntry;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientMessage;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.votingoptions.FactorizeAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.votingoptions.GetActualVotingOptionsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.writeins.DecodeWriteInsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.writeins.DecodeWriteInsAlgorithmInput;

@Service
public final class VerifyProcessPlaintextsAlgorithm {

	private final ElGamal elGamal;
	private final GetActualVotingOptionsAlgorithm getActualVotingOptionsAlgorithm;
	private final DecodeWriteInsAlgorithm decodeWriteInsAlgorithm;
	private final FactorizeAlgorithm factorizeAlgorithm;

	public VerifyProcessPlaintextsAlgorithm(
			final ElGamal elGamal,
			final GetActualVotingOptionsAlgorithm getActualVotingOptionsAlgorithm,
			final DecodeWriteInsAlgorithm decodeWriteInsAlgorithm,
			final FactorizeAlgorithm factorizeAlgorithm) {
		this.elGamal = elGamal;
		this.getActualVotingOptionsAlgorithm = getActualVotingOptionsAlgorithm;
		this.decodeWriteInsAlgorithm = decodeWriteInsAlgorithm;
		this.factorizeAlgorithm = factorizeAlgorithm;
	}

	/**
	 * Verifies that all plaintext votes have been processed correctly.
	 *
	 * @param encryptionGroup the encryption group. Must be non-null.
	 * @param input           the input as a {@link VerifyProcessPlaintextsInput}. Must be non-null.
	 * @return true if L_votes, L_decodedVotes and L_writeIns were generated correctly, false otherwise.
	 * @throws NullPointerException     if any parameter is null.
	 * @throws IllegalArgumentException if
	 *                                  <ul>
	 *                                      <li>the input group is not equal to the context group.</li>
	 *                                      <li>the number of allowed write-ins + 1 is strictly greater than the number of elements in the decrypted votes.</li>
	 *                                      <li>there are less than two plaintext votes.</li>
	 *                                  </ul>
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyProcessPlaintexts(final GqGroup encryptionGroup, final VerifyProcessPlaintextsInput input) {

		checkNotNull(encryptionGroup);
		checkNotNull(input);
		checkArgument(input.getPlaintextVotes().getGroup().equals(encryptionGroup), "The context and input must have the same group.");

		// Input.
		final PrimesMappingTable pTable = input.getPrimesMappingTable();
		final GroupVector<PrimeGqElement, GqGroup> p_tilde = pTable.getPTable().stream()
				.map(PrimesMappingTableEntry::encodedVotingOption)
				.collect(GroupVector.toGroupVector());
		final GroupVector<ElGamalMultiRecipientMessage, GqGroup> m = input.getPlaintextVotes();
		final GroupVector<PrimeGqElement, GqGroup> p_w_tilde = input.getWriteInVotingOptions();
		final int psi = input.getNumberOfSelectableVotingOptions();
		final int delta_hat = input.getNumberOfAllowedWriteInsPlusOne();
		final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> L_votes = input.getSelectedEncodedVotingOptions();
		final List<List<String>> L_decodedVotes = input.getSelectedDecodedVotingOptions();
		final List<List<String>> L_writeIns = input.getSelectedDecodedWriteInVotes();

		// Requires.
		final int l = m.getElementSize();
		checkArgument(delta_hat == l,
				"The number of allowed write-ins + 1 must be equal to the number of elements in the decrypted votes. [delta_hat: %s, l: %s]",
				delta_hat, l);
		final int N_C_hat = m.size();
		checkArgument(N_C_hat >= 2, "There must be at least two plaintext votes.");
		final int N_C = L_votes.size();
		checkArgument(N_C >= 2 ? N_C_hat == N_C : N_C_hat == N_C + 2);

		// Operation.
		final ElGamalMultiRecipientMessage one_vector = elGamal.ones(encryptionGroup, delta_hat);

		record FactorizedDecodedVotes(GroupVector<PrimeGqElement, GqGroup> factorized, List<String> decoded, List<String> decodedWriteInVotes) {
		}

		// Equivalent stream to the for-loop.
		final List<FactorizedDecodedVotes> factorizedDecodedVotes = m.stream()
				.filter(m_i -> !m_i.equals(one_vector))
				.map(m_i -> {
					final GqElement phi_i_0 = m_i.get(0);
					final GroupVector<PrimeGqElement, GqGroup> p_k_hat_prime = factorizeAlgorithm.factorize(phi_i_0, p_tilde, psi);

					final List<String> v_k_hat_prime = getActualVotingOptionsAlgorithm.getActualVotingOptions(pTable, p_k_hat_prime);

					final GroupVector<GqElement, GqGroup> w_k_prime = m_i.getElements().subVector(1, l);

					final List<String> s_k_hat_prime = decodeWriteInsAlgorithm.decodeWriteIns(new DecodeWriteInsAlgorithmInput.Builder()
							.setWriteInVotingOptions(p_w_tilde)
							.setSelectedEncodedVotingOptions(p_k_hat_prime)
							.setEncodedWriteIns(w_k_prime)
							.build());

					return new FactorizedDecodedVotes(p_k_hat_prime, v_k_hat_prime, s_k_hat_prime);
				})
				.toList();

		final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> p_hat_prime = factorizedDecodedVotes.stream()
				.map(FactorizedDecodedVotes::factorized)
				.collect(GroupVector.toGroupVector());
		final List<List<String>> v_hat_prime = factorizedDecodedVotes.stream()
				.map(FactorizedDecodedVotes::decoded)
				.toList();
		final List<List<String>> s_hat_prime = factorizedDecodedVotes.stream()
				.map(FactorizedDecodedVotes::decodedWriteInVotes)
				.toList();

		return p_hat_prime.equals(L_votes) && v_hat_prime.equals(L_decodedVotes) && s_hat_prime.equals(L_writeIns);
	}

}

