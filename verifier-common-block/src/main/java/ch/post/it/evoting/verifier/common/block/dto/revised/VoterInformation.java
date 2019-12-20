package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;

@Getter
public class VoterInformation {
    public final UUID electionEventId;
    public final UUID votingCardId;
    public final UUID ballotId;
    public final UUID credentialId;
    public final UUID verificationCardId;
    public final UUID ballotBoxId;
    public final UUID votingCardSetId;
    public final UUID verificationCardSetId;

    @JsonCreator
    public VoterInformation(@JsonProperty("electionEventId") UUID electionEventId,
                            @JsonProperty("votingCardId") UUID votingCardId, @JsonProperty("ballotId") UUID ballotId,
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
