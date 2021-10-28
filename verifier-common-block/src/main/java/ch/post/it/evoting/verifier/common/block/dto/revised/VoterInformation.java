/*
 * Copyright 2021 Post CH Ltd
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
package ch.post.it.evoting.verifier.common.block.dto.revised;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class VoterInformation {

	private final UUID electionEventId;
	private final UUID votingCardId;
	private final UUID ballotId;
	private final UUID credentialId;
	private final UUID verificationCardId;
	private final UUID ballotBoxId;
	private final UUID votingCardSetId;
	private final UUID verificationCardSetId;

	public VoterInformation(
			@JsonProperty("electionEventId")
					UUID electionEventId,
			@JsonProperty("votingCardId")
					UUID votingCardId,
			@JsonProperty("ballotId")
					UUID ballotId,
			@JsonProperty("credentialId")
					UUID credentialId,
			@JsonProperty("verificationCardId")
					UUID verificationCardId,
			@JsonProperty("ballotBoxId")
					UUID ballotBoxId,
			@JsonProperty("votingCardSetId")
					UUID votingCardSetId,
			@JsonProperty("verificationCardSetId")
					UUID verificationCardSetId) {
		this.electionEventId = electionEventId;
		this.votingCardId = votingCardId;
		this.ballotId = ballotId;
		this.credentialId = credentialId;
		this.verificationCardId = verificationCardId;
		this.ballotBoxId = ballotBoxId;
		this.votingCardSetId = votingCardSetId;
		this.verificationCardSetId = verificationCardSetId;
	}
}
