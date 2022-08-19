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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence.algorithms;

import static ch.post.it.evoting.cryptoprimitives.domain.ControlComponentConstants.NODE_IDS;
import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import ch.post.it.evoting.cryptoprimitives.math.GqGroup;

/**
 * Regroups the context values needed by the VerifyEncryptedPCCExponentiationProofsVerificationCardSet and the
 * VerifyEncryptedCKExponentiationProofsVerificationCardSet algorithm.
 *
 * <ul>
 * <li>group, the encryption group. Not null.</li>
 * <li>j, the CCR's index. In the range [1, 4].</li>
 * <li>ee, the election event ID. Not null and a valid UUID.</li>
 * <li>vcs, the verification card set ID. Not null and a valid UUID.</li>
 * <li>N_E, the number of voters. Strictly positive.</li>
 * <li>n, number of voting options. Only needed for VerifyEncryptedPCCExponentiationProofsVerificationCardSet.</li>
 * </ul>
 */
public class VerifyEncryptedExponentiationProofsVerificationCardSetContext {

	private final GqGroup encryptionGroup;
	private final int j;
	private final String electionEventId;
	private final String verificationCardSetId;
	private final int numberOfVoters;
	private final int numberOfVotingOptions;

	private VerifyEncryptedExponentiationProofsVerificationCardSetContext(final GqGroup encryptionGroup, final int j, final String electionEventId,
			final String verificationCardSetId, final int numberOfVoters, final int numberOfVotingOptions) {
		this.encryptionGroup = encryptionGroup;
		this.j = j;
		this.electionEventId = electionEventId;
		this.verificationCardSetId = verificationCardSetId;
		this.numberOfVoters = numberOfVoters;
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

	public String getVerificationCardSetId() {
		return verificationCardSetId;
	}

	public int getNumberOfVoters() {
		return numberOfVoters;
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
		private String verificationCardSetId;
		private int numberOfVoters;
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

		public Builder setVerificationCardSetId(final String verificationCardSetId) {
			this.verificationCardSetId = verificationCardSetId;
			return this;
		}

		public Builder setNumberOfVoters(final int numberOfVoters) {
			this.numberOfVoters = numberOfVoters;
			return this;
		}

		public Builder setNumberOfVotingOptions(final int numberOfVotingOptions) {
			this.numberOfVotingOptions = numberOfVotingOptions;
			return this;
		}

		public VerifyEncryptedExponentiationProofsVerificationCardSetContext build() {
			checkNotNull(encryptionGroup);
			validateUUID(electionEventId);
			validateUUID(verificationCardSetId);

			checkArgument(NODE_IDS.contains(j), "The CCR's index must be in the range [1, 4]. [j: %s]", j);
			checkArgument(numberOfVoters > 0, "The number of voters must be strictly positive. [N_E: %s]", numberOfVoters);
			checkArgument(numberOfVotingOptions >= 0, "The number of voting options must be positive. [n: %s]", numberOfVotingOptions);

			return new VerifyEncryptedExponentiationProofsVerificationCardSetContext(encryptionGroup, j, electionEventId, verificationCardSetId,
					numberOfVoters, numberOfVotingOptions);
		}
	}
}
