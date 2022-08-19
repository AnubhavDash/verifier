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

import static ch.post.it.evoting.cryptoprimitives.utils.Validations.allEqual;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.domain.validations.Validations;
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
	 * @param numberOfAllowedWriteInsPlusOne delta_hat, the number of allowed write-ins plus one. Must be strictly positive.
	 * @param confirmedEncryptedVotes        vcMap<sub>j</sub>, map of verification card ids to confirmed, encrypted votes. Must be non-null.
	 * @param electionPublicKey              EL<sub>pk</sub>, the election public key. Must be non-null.
	 * @return the mix net initial ciphertexts.
	 */
	@SuppressWarnings("java:S117")
	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getMixnetInitialCiphertexts(final int numberOfAllowedWriteInsPlusOne,
			final Map<String, ElGamalMultiRecipientCiphertext> confirmedEncryptedVotes, final ElGamalMultiRecipientPublicKey electionPublicKey) {

		checkNotNull(confirmedEncryptedVotes);
		checkNotNull(electionPublicKey);

		// Size checks.
		checkArgument(numberOfAllowedWriteInsPlusOne >= 1, "The number of allowed write-ins + 1 must be at least 1.");
		final int delta_hat = numberOfAllowedWriteInsPlusOne;
		final int delta = electionPublicKey.size();
		checkArgument(delta_hat <= delta, "The election public key must have at least as many elements as the number of allowed write-ins + 1.");

		final Map<String, ElGamalMultiRecipientCiphertext> vcMap_j = Map.copyOf(confirmedEncryptedVotes);
		checkArgument(allEqual(vcMap_j.values().stream(), ElGamalMultiRecipientCiphertext::size),
				"All ciphertexts must have the same size.");
		vcMap_j.values().stream()
				.findAny()
				.map(ElGamalMultiRecipientCiphertext::size)
				.ifPresent(l -> checkArgument(l == delta_hat, "The ciphertexts must be of size number of allowed write-ins + 1."));

		// Groups check.
		checkArgument(allEqual(vcMap_j.values().stream(), ElGamalMultiRecipientCiphertext::getGroup),
				"All ciphertexts must have the same group.");

		// Verification card id validation.
		vcMap_j.keySet().forEach(Validations::validateUUID);

		// Cross-group check.
		vcMap_j.values().stream()
				.findAny()
				.ifPresent(elGamalMultiRecipientCiphertext -> checkArgument(
						elGamalMultiRecipientCiphertext.getGroup().equals(electionPublicKey.getGroup()),
						"The ciphertexts must have the same group as the election public key."));

		// Variables.
		final int N_C = vcMap_j.size();
		final ElGamalMultiRecipientPublicKey EL_pk = electionPublicKey;
		final GqGroup gqGroup = EL_pk.getGroup();

		// Operation
		final Map<String, ElGamalMultiRecipientCiphertext> vcMap_j_ordered = order(vcMap_j);

		final List<ElGamalMultiRecipientCiphertext> c_init_j = new ArrayList<>(vcMap_j_ordered.values());

		if (N_C < 2) {
			final ElGamalMultiRecipientMessage oneMessage = elGamal.ones(gqGroup, delta_hat);

			final ZqElement oneExponent = ZqElement.create(1, ZqGroup.sameOrderAs(gqGroup));
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
