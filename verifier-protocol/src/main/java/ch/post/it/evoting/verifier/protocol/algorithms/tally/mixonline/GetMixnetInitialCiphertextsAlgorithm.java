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
package ch.post.it.evoting.verifier.protocol.algorithms.tally.mixonline;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientMessage;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.ZqElement;
import ch.post.it.evoting.cryptoprimitives.math.ZqGroup;

/**
 * Implements the GetMixnetInitialCiphertexts<sub>j</sub> algorithm.
 */
public class GetMixnetInitialCiphertextsAlgorithm {

	private final ElGamal elGamal;

	public GetMixnetInitialCiphertextsAlgorithm(final ElGamal elGamal) {
		this.elGamal = elGamal;
	}

	/**
	 * Retrieves the mix net's initial ciphertexts from the confirmed, encrypted votes. The algorithm adds two trivial encryptions if there are less
	 * than two confirmed votes in the ballot box.
	 *
	 * @param context the {@link GetMixnetInitialCiphertextsContext}. Must be non-null.
	 * @param input   the {@link GetMixnetInitialCiphertextsInput}. Must be non-null.
	 * @return the mix net initial ciphertexts.
	 * @throws NullPointerException     if any parameter is null.
	 * @throws IllegalArgumentException if the context and input do not have the same encryption group.
	 */
	@SuppressWarnings("java:S117")
	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getMixnetInitialCiphertexts(final GetMixnetInitialCiphertextsContext context,
			final GetMixnetInitialCiphertextsInput input) {
		checkNotNull(context);
		checkNotNull(input);

		// Cross-group checks.
		checkArgument(context.encryptionGroup().equals(input.electionPublicKey().getGroup()),
				"The context and input must have the same encryption group.");

		// Context.
		final GqGroup encryptionGroup = context.encryptionGroup();

		// Input.
		final int delta_hat = input.numberOfAllowedWriteInsPlusOne();
		final Map<String, ElGamalMultiRecipientCiphertext> vcMap_j = input.encryptedConfirmedVotes();
		final ElGamalMultiRecipientPublicKey EL_pk = input.electionPublicKey();
		final int N_C = vcMap_j.size();

		// Require.
		// l = delta_hat <= delta is ensured by GetMixnetInitialCiphertextsInput.

		// Operation.
		final Map<String, ElGamalMultiRecipientCiphertext> vcMap_j_ordered = order(vcMap_j);

		final List<ElGamalMultiRecipientCiphertext> c_init_j = new ArrayList<>(vcMap_j_ordered.values());

		if (N_C < 2) {
			final ElGamalMultiRecipientMessage oneMessage = elGamal.ones(encryptionGroup, delta_hat);

			final ZqElement oneExponent = ZqElement.create(1, ZqGroup.sameOrderAs(encryptionGroup));
			final ElGamalMultiRecipientCiphertext E_trivial = elGamal.getCiphertext(oneMessage, oneExponent, EL_pk);

			c_init_j.add(E_trivial);
			c_init_j.add(E_trivial);
		}

		// Output.
		return GroupVector.from(c_init_j);
	}

	/**
	 * The TreeMap orders the votes by natural order its keys. Natural order corresponds to a lexicographic order in case of strings.
	 */
	@VisibleForTesting
	protected Map<String, ElGamalMultiRecipientCiphertext> order(final Map<String, ElGamalMultiRecipientCiphertext> listOfConfirmedVotes) {
		return new TreeMap<>(listOfConfirmedVotes);
	}

}