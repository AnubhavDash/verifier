package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Ballot {
    public final Vote vote;
    public final Receipt receipt;
    public final AuthenticationToken authenticationToken;

    @JsonCreator
    public Ballot(@JsonProperty("vote") Vote vote, @JsonProperty("receipt") Receipt receipt,
                  @JsonProperty("authenticationToken") AuthenticationToken authenticationToken) {
        this.vote = vote;
        this.receipt = receipt;
        this.authenticationToken = authenticationToken;
    }
}
