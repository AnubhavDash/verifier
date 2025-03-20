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

import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientMessage;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.ProcessPlaintextsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.ProcessPlaintextsContext;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.ProcessPlaintextsOutput;

@Service
public final class VerifyProcessPlaintextsAlgorithm {

	private final ProcessPlaintextsAlgorithm processPlaintextsAlgorithm;

	public VerifyProcessPlaintextsAlgorithm(
			final ProcessPlaintextsAlgorithm processPlaintextsAlgorithm) {
		this.processPlaintextsAlgorithm = processPlaintextsAlgorithm;
	}

	/**
	 * Verifies that all plaintext votes have been processed correctly.
	 *
	 * @param context the context as a {@link VerifyProcessPlaintextsContext}. Must be non-null.
	 * @param input   the input as a {@link VerifyProcessPlaintextsInput}. Must be non-null.
	 * @return true if L_votes, L_decodedVotes and L_writeIns were generated correctly, false otherwise.
	 * @throws NullPointerException     if any parameter is null.
	 * @throws IllegalArgumentException if
	 *                                  <ul>
	 *                                      <li>the input group is not equal to the context group.</li>
	 *                                      <li>there are less than two plaintext votes.</li>
	 *                                  </ul>
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyProcessPlaintexts(final VerifyProcessPlaintextsContext context, final VerifyProcessPlaintextsInput input) {
		checkNotNull(context);
		checkNotNull(input);

		// Cross-group check.
		checkArgument(input.getPlaintextVotes().getGroup().equals(context.encryptionGroup()), "The context and input must have the same group.");

		// Context.
		final GqGroup p_q_g = context.encryptionGroup();
		final PrimesMappingTable pTable = context.primesMappingTable();

		// Input.
		final GroupVector<ElGamalMultiRecipientMessage, GqGroup> m = input.getPlaintextVotes();
		final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> L_votes = input.getListOfDecryptedVotes();
		final ImmutableList<ImmutableList<String>> L_decodedVotes = input.getListOfDecodedVotes();
		final ImmutableList<ImmutableList<String>> L_writeIns = input.getListOfDecodedWriteIns();

		// Require.
		final int N_C_hat = m.size();
		checkArgument(N_C_hat >= 2, "There must be at least two plaintext votes.");
		final int N_C = L_votes.size();
		checkArgument(N_C >= 2 ? N_C_hat == N_C : N_C_hat == N_C + 2);

		// Operation.
		final ProcessPlaintextsContext processPlaintextsContext = new ProcessPlaintextsContext(p_q_g, pTable);
		final ProcessPlaintextsOutput L_votes_prime_L_decodedVotes_prime_L_writeIns_prime = processPlaintextsAlgorithm.processPlaintexts(
				processPlaintextsContext, m);

		final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> L_votes_prime = L_votes_prime_L_decodedVotes_prime_L_writeIns_prime.getSelectedEncodedVotingOptions();
		final ImmutableList<ImmutableList<String>> L_decodedVotes_prime = L_votes_prime_L_decodedVotes_prime_L_writeIns_prime.getSelectedDecodedVotingOptions();
		final ImmutableList<ImmutableList<String>> L_writeIns_prime = L_votes_prime_L_decodedVotes_prime_L_writeIns_prime.getSelectedDecodedWriteInVotes();

		return L_votes_prime.equals(L_votes) && L_decodedVotes_prime.equals(L_decodedVotes) && L_writeIns_prime.equals(L_writeIns);
	}

}

