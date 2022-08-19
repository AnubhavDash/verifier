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

import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

public record VerifyTallyControlComponentBallotBoxContext(GqGroup encryptionGroup, String electionEventId, String ballotBoxId,
														  ElGamalMultiRecipientPublicKey electoralBoardPublicKey,
														  GroupVector<PrimeGqElement, GqGroup> encodedVotingOptions,
														  int numberOfSelectableVotingOptions, int numberOfAllowedWriteInsPlusOne) {

	public VerifyTallyControlComponentBallotBoxContext(final GqGroup encryptionGroup, final String electionEventId, final String ballotBoxId,
			final ElGamalMultiRecipientPublicKey electoralBoardPublicKey, final GroupVector<PrimeGqElement, GqGroup> encodedVotingOptions,
			final int numberOfSelectableVotingOptions,
			final int numberOfAllowedWriteInsPlusOne) {
		this.encryptionGroup = checkNotNull(encryptionGroup);
		this.electionEventId = validateUUID(electionEventId);
		this.ballotBoxId = validateUUID(ballotBoxId);
		this.electoralBoardPublicKey = checkNotNull(electoralBoardPublicKey);
		this.encodedVotingOptions = checkNotNull(encodedVotingOptions);
		this.numberOfSelectableVotingOptions = numberOfSelectableVotingOptions;
		this.numberOfAllowedWriteInsPlusOne = numberOfAllowedWriteInsPlusOne;

		checkArgument(electoralBoardPublicKey.getGroup().equals(encryptionGroup));
		checkArgument(encodedVotingOptions.getGroup().equals(encryptionGroup));

		checkArgument(1 <= numberOfSelectableVotingOptions && numberOfSelectableVotingOptions <= 120,
				"The number of selectable voting options must be within the allowed bounds.");
		checkArgument(numberOfAllowedWriteInsPlusOne > 0, "The number of allowed write-ins plus one must be at least 1.");
	}

}
