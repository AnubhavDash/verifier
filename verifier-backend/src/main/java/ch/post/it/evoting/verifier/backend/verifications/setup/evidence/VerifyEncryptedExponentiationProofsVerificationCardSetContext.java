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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static ch.post.it.evoting.evotinglibraries.domain.VotingOptionsConstants.MAXIMUM_SUPPORTED_NUMBER_OF_VOTING_OPTIONS;
import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.evotinglibraries.domain.ControlComponentNode;
import ch.post.it.evoting.evotinglibraries.domain.validations.Validations;

/**
 * Regroups the context values needed by the VerifyEncryptedPCCExponentiationProofsVerificationCardSet and the
 * VerifyEncryptedCKExponentiationProofsVerificationCardSet algorithm.
 *
 * <ul>
 *     <li>(p_q_g), the encryption group. Not null.</li>
 *     <li>j, the CCR's index. In the range [1, 4].</li>
 *     <li>ee, the election event ID. Not null and a valid UUID.</li>
 *     <li>vc, the vector of verification card IDs. Not null and contains valid UUIDs.</li>
 *     <li>n, the number of voting options. In range [1, n<sub>sup</sub>]. Only needed for VerifyEncryptedPCCExponentiationProofsVerificationCardSet.</li>
 * </ul>
 */
public class VerifyEncryptedExponentiationProofsVerificationCardSetContext {

	private final GqGroup encryptionGroup;
	private final int j;
	private final String electionEventId;
	private final ImmutableList<String> verificationCardIds;
	private final int numberOfVotingOptions;

	private VerifyEncryptedExponentiationProofsVerificationCardSetContext(final GqGroup encryptionGroup, final int j, final String electionEventId,
			final ImmutableList<String> verificationCardIds, final int numberOfVotingOptions) {
		this.encryptionGroup = encryptionGroup;
		this.j = j;
		this.electionEventId = electionEventId;
		this.verificationCardIds = verificationCardIds;
		this.numberOfVotingOptions = numberOfVotingOptions;
	}

	public GqGroup getEncryptionGroup() {
		return encryptionGroup;
	}

	public int getJ() {
		return j;
	}

	public String getElectionEventId() {
		return electionEventId;
	}

	public ImmutableList<String> getVerificationCardIds() {
		return verificationCardIds;
	}

	public int getNumberOfVotingOptions() {
		return numberOfVotingOptions;
	}

	/**
	 * Builder performing context validations and cross-validations before constructing a
	 * {@link VerifyEncryptedExponentiationProofsVerificationCardSetContext}.
	 */
	public static class Builder {
		private GqGroup encryptionGroup;
		private int j;
		private String electionEventId;
		private ImmutableList<String> verificationCardIds;
		private int numberOfVotingOptions;

		public Builder setEncryptionGroup(final GqGroup encryptionGroup) {
			this.encryptionGroup = encryptionGroup;
			return this;
		}

		public Builder setJ(final int j) {
			this.j = j;
			return this;
		}

		public Builder setElectionEventId(final String electionEventId) {
			this.electionEventId = electionEventId;
			return this;
		}

		public Builder setVerificationCardIds(final ImmutableList<String> verificationCardIds) {
			this.verificationCardIds = verificationCardIds;
			return this;
		}

		public Builder setNumberOfVotingOptions(final int numberOfVotingOptions) {
			this.numberOfVotingOptions = numberOfVotingOptions;
			return this;
		}

		public VerifyEncryptedExponentiationProofsVerificationCardSetContext build() {
			checkNotNull(encryptionGroup);
			validateUUID(electionEventId);
			checkNotNull(verificationCardIds).forEach(Validations::validateUUID);

			checkArgument(ControlComponentNode.ids().contains(j), "The CCR's index must be in the range [1, 4]. [j: %s]", j);
			checkArgument(numberOfVotingOptions > 0, "The number of voting options must be strictly positive. [n: %s]", numberOfVotingOptions);
			checkArgument(numberOfVotingOptions <= MAXIMUM_SUPPORTED_NUMBER_OF_VOTING_OPTIONS,
					"The number of voting options must be smaller or equal to the maximum supported number of voting options. [n: %s, n_sup: %s]",
					numberOfVotingOptions, MAXIMUM_SUPPORTED_NUMBER_OF_VOTING_OPTIONS);

			return new VerifyEncryptedExponentiationProofsVerificationCardSetContext(encryptionGroup, j, electionEventId, verificationCardIds,
					numberOfVotingOptions);
		}
	}
}
