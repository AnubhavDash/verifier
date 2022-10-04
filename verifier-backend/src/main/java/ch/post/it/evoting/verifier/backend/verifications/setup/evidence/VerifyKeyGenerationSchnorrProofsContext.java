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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static ch.post.it.evoting.cryptoprimitives.domain.VotingOptionsConstants.MAXIMUM_NUMBER_OF_SELECTABLE_VOTING_OPTIONS;
import static ch.post.it.evoting.cryptoprimitives.domain.VotingOptionsConstants.MAXIMUM_NUMBER_OF_WRITE_IN_OPTIONS;
import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import ch.post.it.evoting.cryptoprimitives.math.GqGroup;

/**
 * Regroups the context values needed by the VerifyKeyGenerationSchnorrProofs algorithm.
 *
 * <ul>
 * <li>encryptionGroup, the encryption group. Not null.</li>
 * <li>phi, the maximum number of selections. Strictly positive.</li>
 * <li>mu, the maximum supported number of write-in options + 1. Strictly positive.</li>
 * <li>delta, the number of write-ins for this election + 1. Strictly positive.</li>
 * <li>ee, the election event ID. Not null and a valid UUID.</li>
 * </ul>
 */
public class VerifyKeyGenerationSchnorrProofsContext {

	private final GqGroup encryptionGroup;
	private final int maximumNumberOfSelections;
	private final int maximumNumberOfWriteInOptionsPlusOne;
	private final int numberOfWriteInsPlusOne;
	private final String electionEventId;

	public VerifyKeyGenerationSchnorrProofsContext(final GqGroup encryptionGroup, final String electionEventId, final int numberOfWriteInsPlusOne) {
		checkNotNull(encryptionGroup);
		validateUUID(electionEventId);

		checkArgument(numberOfWriteInsPlusOne > 0, "The number of write-ins for this election + 1 must be strictly positive.");

		this.encryptionGroup = encryptionGroup;
		this.maximumNumberOfSelections = MAXIMUM_NUMBER_OF_SELECTABLE_VOTING_OPTIONS;
		this.maximumNumberOfWriteInOptionsPlusOne = MAXIMUM_NUMBER_OF_WRITE_IN_OPTIONS + 1;
		this.numberOfWriteInsPlusOne = numberOfWriteInsPlusOne;
		this.electionEventId = electionEventId;
	}

	public GqGroup getEncryptionGroup() {
		return encryptionGroup;
	}

	public int getMaximumNumberOfSelections() {
		return maximumNumberOfSelections;
	}

	public int getMaximumNumberOfWriteInOptionsPlusOne() {
		return maximumNumberOfWriteInOptionsPlusOne;
	}

	public int getNumberOfWriteInsPlusOne() {
		return numberOfWriteInsPlusOne;
	}

	public String getElectionEventId() {
		return electionEventId;
	}
}
