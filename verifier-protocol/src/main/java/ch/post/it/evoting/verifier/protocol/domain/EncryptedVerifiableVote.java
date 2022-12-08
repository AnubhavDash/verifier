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
package ch.post.it.evoting.verifier.protocol.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ExponentiationProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.PlaintextEqualityProof;

public record EncryptedVerifiableVote(ContextIds contextIds,
									  ElGamalMultiRecipientCiphertext encryptedVote,
									  ElGamalMultiRecipientCiphertext exponentiatedEncryptedVote,
									  ElGamalMultiRecipientCiphertext encryptedPartialChoiceReturnCodes,
									  ExponentiationProof exponentiationProof,
									  PlaintextEqualityProof plaintextEqualityProof) implements HashableList {

	public EncryptedVerifiableVote {
		checkNotNull(contextIds);
		checkNotNull(encryptedVote);
		checkNotNull(exponentiatedEncryptedVote);
		checkNotNull(encryptedPartialChoiceReturnCodes);
		checkNotNull(exponentiationProof);
		checkNotNull(plaintextEqualityProof);

		checkArgument(exponentiatedEncryptedVote.size() == 1, "The exponentiated encrypted vote size must be one.");
		checkArgument(encryptedVote.size() <= encryptedPartialChoiceReturnCodes.size() + 1,
				"The encrypted vote size must be smaller or equal to the encrypted partial choice return codes size + 1.");

		checkArgument(encryptedVote.getGroup().equals(exponentiatedEncryptedVote.getGroup()),
				"The groups of the encrypted vote and the exponentiated encrypted vote must be the same.");
		checkArgument(encryptedVote.getGroup().equals(encryptedPartialChoiceReturnCodes.getGroup()),
				"The groups of the encrypted vote and the encrypted partial choice return codes must be the same.");
		checkArgument(encryptedVote.getGroup().hasSameOrderAs(exponentiationProof.getGroup()),
				"The groups of the encrypted vote and the exponentiation proof must be of same order.");
		checkArgument(encryptedVote.getGroup().hasSameOrderAs(plaintextEqualityProof.getGroup()),
				"The groups of the encrypted vote and the plaintext equality proof must be of same order.");
	}

	@Override
	public List<Hashable> toHashableForm() {

		return List.of(contextIds,
				encryptedVote,
				exponentiatedEncryptedVote,
				encryptedPartialChoiceReturnCodes,
				exponentiationProof,
				plaintextEqualityProof);

	}
}
