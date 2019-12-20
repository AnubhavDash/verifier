package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Ballot {
    public final CastVote vote;
    public final Receipt receipt;
    public final AuthenticationToken authenticationToken;

    @JsonCreator
    public Ballot(@JsonProperty("vote") CastVote vote, @JsonProperty("receipt") Receipt receipt,
                  @JsonProperty("authenticationToken") AuthenticationToken authenticationToken) {
        this.vote = vote;
        this.receipt = receipt;
        this.authenticationToken = authenticationToken;
    }
}
