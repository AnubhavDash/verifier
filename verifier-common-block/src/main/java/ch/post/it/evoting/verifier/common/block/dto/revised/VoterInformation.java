/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;

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

    @JsonCreator
    public VoterInformation(@JsonProperty("electionEventId") UUID electionEventId,
                            @JsonProperty("votingCardId") UUID votingCardId,
                            @JsonProperty("ballotId") UUID ballotId,
                            @JsonProperty("credentialId") UUID credentialId,
                            @JsonProperty("verificationCardId") UUID verificationCardId,
                            @JsonProperty("ballotBoxId") UUID ballotBoxId,
                            @JsonProperty("votingCardSetId") UUID votingCardSetId,
                            @JsonProperty("verificationCardSetId") UUID verificationCardSetId) {
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
