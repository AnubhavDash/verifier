/*
 * (c) Copyright 2025 Swiss Post Ltd.
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

import static ch.post.it.evoting.cryptoprimitives.utils.Validations.allEqual;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientMessage;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

/**
 * Regroups the input values needed by the VerifyProcessPlaintexts algorithm.
 *
 * <ul>
 *     <li>m, the list of plaintext votes. Non-null.</li>
 *     <li>L<sub>votes</sub>, the list of decrypted votes. Non-null.</li>
 *     <li>L<sub>decodedVotes</sub>, the list of decoded votes. Non-null.</li>
 *     <li>L<sub>writeIns</sub>, the list of decoded write-ins. Non-null.</li>
 * </ul>
 */
public class VerifyProcessPlaintextsInput {

	private final GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes;
	private final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> listOfDecryptedVotes;
	private final ImmutableList<ImmutableList<String>> listOfDecodedVotes;
	private final ImmutableList<ImmutableList<String>> listOfDecodedWriteIns;

	private VerifyProcessPlaintextsInput(final GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes,
			final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> listOfDecryptedVotes,
			final ImmutableList<ImmutableList<String>> listOfDecodedVotes,
			final ImmutableList<ImmutableList<String>> listOfDecodedWriteIns) {
		this.plaintextVotes = plaintextVotes;
		this.listOfDecryptedVotes = listOfDecryptedVotes;
		this.listOfDecodedVotes = listOfDecodedVotes;
		this.listOfDecodedWriteIns = listOfDecodedWriteIns;
	}

	public GroupVector<ElGamalMultiRecipientMessage, GqGroup> getPlaintextVotes() {
		return plaintextVotes;
	}

	public GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> getListOfDecryptedVotes() {
		return listOfDecryptedVotes;
	}

	public ImmutableList<ImmutableList<String>> getListOfDecodedVotes() {
		return listOfDecodedVotes;
	}

	public ImmutableList<ImmutableList<String>> getListOfDecodedWriteIns() {
		return listOfDecodedWriteIns;
	}

	public static class Builder {

		private GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes;
		private GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> listOfDecryptedVotes;
		private ImmutableList<ImmutableList<String>> listOfDecodedVotes;
		private ImmutableList<ImmutableList<String>> listOfDecodedWriteIns;

		public Builder setPlaintextVotes(
				final GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes) {
			this.plaintextVotes = plaintextVotes;
			return this;
		}

		public Builder setListOfDecryptedVotes(
				final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> listOfDecryptedVotes) {
			this.listOfDecryptedVotes = listOfDecryptedVotes;
			return this;
		}

		public Builder setListOfDecodedVotes(final ImmutableList<ImmutableList<String>> listOfDecodedVotes) {
			this.listOfDecodedVotes = checkNotNull(listOfDecodedVotes);
			return this;
		}

		public Builder setListOfDecodedWriteIns(final ImmutableList<ImmutableList<String>> listOfDecodedWriteIns) {
			this.listOfDecodedWriteIns = checkNotNull(listOfDecodedWriteIns);
			return this;
		}

		/**
		 * @throws NullPointerException     if any parameter is null.
		 * @throws IllegalArgumentException if
		 *                                  <ul>
		 *                                      <li>the plaintext votes and the decrypted votes do not have the same group.</li>
		 *                                      <li>there is a different number of decrypted votes and decoded votes.</li>
		 *                                      <li>there is a different number of write-ins and decrypted votes.</li>
		 *                                  </ul>
		 */
		public VerifyProcessPlaintextsInput build() {
			checkNotNull(plaintextVotes);
			checkNotNull(listOfDecryptedVotes);

			// Cross-group checks.
			checkArgument(listOfDecryptedVotes.isEmpty() || listOfDecryptedVotes.getGroup().equals(plaintextVotes.getGroup()),
					"The decrypted votes and plaintexts votes must have the same group.");

			// Cross-size checks.
			checkArgument(listOfDecryptedVotes.size() == listOfDecodedVotes.size(),
					"There must be as many decrypted votes as decoded votes.");
			checkArgument(listOfDecodedWriteIns.size() == listOfDecryptedVotes.size(),
					"There must be as many write-ins as decrypted votes.");
			checkArgument(allEqual(listOfDecodedVotes.stream(), ImmutableList::size),
					"All decoded votes must have the same size.");
			checkArgument(listOfDecryptedVotes.isEmpty()
							|| listOfDecryptedVotes.getElementSize() == listOfDecodedVotes.get(0).size(),
					"All decrypted votes and decoded votes must have the same size.");

			return new VerifyProcessPlaintextsInput(plaintextVotes, listOfDecryptedVotes, listOfDecodedVotes, listOfDecodedWriteIns);
		}
	}
}
