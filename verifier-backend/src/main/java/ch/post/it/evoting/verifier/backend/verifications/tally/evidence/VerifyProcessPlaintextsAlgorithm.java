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

import static ch.post.it.evoting.cryptoprimitives.utils.Validations.allEqual;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTable;
import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTableEntry;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientMessage;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.DecodeVotingOptionsAlgorithm;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.FactorizeService;

@Service
public final class VerifyProcessPlaintextsAlgorithm {

	private final ElGamal elGamal;
	private final DecodeVotingOptionsAlgorithm decodeVotingOptionsAlgorithm;

	public VerifyProcessPlaintextsAlgorithm(
			final ElGamal elGamal,
			final DecodeVotingOptionsAlgorithm decodeVotingOptionsAlgorithm) {
		this.elGamal = elGamal;
		this.decodeVotingOptionsAlgorithm = decodeVotingOptionsAlgorithm;
	}

	/**
	 * Factorizes each plaintext vote.
	 *
	 * @param primesMappingTable              pTable, the primes mapping table. Must be non-null.
	 * @param plaintextVotes                  m, the list of plaintext votes. Must be non-null.
	 * @param numberOfSelectableVotingOptions psi, the number of selectable voting options. Must be between 1 and 120 inclusive.
	 * @param numberOfAllowedWriteInsPlusOne  delta_hat, the number of write-ins + 1. Must be strictly positive.
	 * @return the list of all selected encoded voting options L_votes.
	 * @throws NullPointerException     if any of the inputs is null.
	 * @throws IllegalArgumentException if
	 *                                  <ul>
	 *                                      <li>the number of elements of each plaintext vote does not correspond to the number of allowed write ins plus one.</li>
	 *                                      <li>not all selected encoded voting options are of size selected number of selectable voting options.</li>
	 *                                      <li>the plaintext votes, encoded voting options and selected encoded voting options do not have the same group.</li>
	 *                                      <li>there is a different number of selected encoded and decoded voting options.</li>
	 *                                      <li>the number of selectable voting options is strictly smaller than 1.</li>
	 *                                      <li>the number of selectable voting options is strictly greater than 120.</li>
	 *                                      <li>the number of allowed write-ins + 1 is smaller or equal to 0.</li>
	 *                                      <li>the number of allowed write-ins + 1 is strictly greater to the number of elements in the decrypted votes.</li>
	 *                                      <li>there are less than two plaintext votes.</li>
	 *                                  </ul>
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyProcessPlaintexts(
			final PrimesMappingTable primesMappingTable, final GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes,
			final int numberOfSelectableVotingOptions, final int numberOfAllowedWriteInsPlusOne,
			final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions,
			final List<List<String>> selectedDecodedVotingOptions) {

		checkNotNull(primesMappingTable);
		checkNotNull(plaintextVotes);
		checkNotNull(selectedEncodedVotingOptions);
		checkNotNull(selectedDecodedVotingOptions);

		// Cross size and cross group checks.
		checkArgument(plaintextVotes.getElementSize() == numberOfAllowedWriteInsPlusOne,
				"The number of elements of each plaintext vote must correspond to the number of allowed write ins plus one. [votes: %s, allowed write-ins plus one: %s]",
				plaintextVotes.getElementSize(), numberOfAllowedWriteInsPlusOne);

		final GroupVector<PrimeGqElement, GqGroup> encodedVotingOptions = primesMappingTable.getPTable().stream()
				.map(PrimesMappingTableEntry::encodedVotingOption)
				.collect(GroupVector.toGroupVector());
		checkArgument(allEqual(Stream.of(plaintextVotes, encodedVotingOptions), GroupVector::getGroup),
				"The plaintext votes and the encoded voting options must have the same group.");

		checkArgument(selectedEncodedVotingOptions.size() == selectedDecodedVotingOptions.size(),
				"There must be as many encoded as decoded voting options.");

		if (!selectedEncodedVotingOptions.isEmpty()) {
			checkArgument(selectedEncodedVotingOptions.getElementSize() == numberOfSelectableVotingOptions,
					"Each selected encoded voting options must be of size selected number of selectable voting options. [expected: %s, actual: %s]",
					numberOfSelectableVotingOptions, selectedEncodedVotingOptions.getElementSize());
			checkArgument(encodedVotingOptions.getGroup().equals(selectedEncodedVotingOptions.getGroup()),
					"The plaintext votes, encoded voting options and selected encoded voting options must have the same group.");
		}

		// Context.
		final GqGroup group = plaintextVotes.getGroup();

		// Input.
		final PrimesMappingTable pTable = primesMappingTable;
		final GroupVector<PrimeGqElement, GqGroup> p_tilde = encodedVotingOptions;
		final GroupVector<ElGamalMultiRecipientMessage, GqGroup> m = plaintextVotes;
		final int psi = numberOfSelectableVotingOptions;
		final int delta_hat = numberOfAllowedWriteInsPlusOne;
		final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> L_votes = selectedEncodedVotingOptions;
		final List<List<String>> L_decodedVotes = selectedDecodedVotingOptions;

		checkArgument(psi >= 1, "The number of selectable voting options must be greater or equal to 1. [psi: %s]", psi);
		checkArgument(psi <= 120, "The number of selectable voting options must be smaller or equal to 120. [psi: %s]", psi);
		checkArgument(delta_hat > 0, "The number of allowed write-ins + 1 must be strictly greater than 0. [delta_hat: %s]", delta_hat);

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
		final ElGamalMultiRecipientMessage one_vector = elGamal.ones(group, delta_hat);

		record FactorizedDecodedVotes(GroupVector<PrimeGqElement, GqGroup> factorized, List<String> decoded) {
		}

		// Equivalent stream to the for-loop.
		final List<FactorizedDecodedVotes> factorizedDecodedVotes = m.stream()
				.filter(m_i -> !m_i.equals(one_vector))
				.map(m_i -> {
					final GqElement phi_i_0 = m_i.get(0);
					final GroupVector<PrimeGqElement, GqGroup> p_hat_prime_k = FactorizeService.factorize(phi_i_0, p_tilde, psi);

					final List<String> v_hat_prime_k = decodeVotingOptionsAlgorithm.decodeVotingOptions(p_hat_prime_k, pTable);

					return new FactorizedDecodedVotes(p_hat_prime_k, v_hat_prime_k);
				})
				.toList();

		final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> p_hat_prime = factorizedDecodedVotes.stream()
				.map(FactorizedDecodedVotes::factorized)
				.collect(GroupVector.toGroupVector());
		final List<List<String>> v_hat_prime = factorizedDecodedVotes.stream()
				.map(FactorizedDecodedVotes::decoded)
				.toList();

		return p_hat_prime.equals(L_votes) && v_hat_prime.equals(L_decodedVotes);
	}

}

