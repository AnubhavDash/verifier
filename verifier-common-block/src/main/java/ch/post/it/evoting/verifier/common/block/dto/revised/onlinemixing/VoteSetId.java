package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class VoteSetId {

    private BallotBoxId ballotBoxId;
    private BigInteger index;

    public VoteSetId(@JsonProperty("ballotBoxId") BallotBoxId ballotBoxId,
                     @JsonProperty("index") BigInteger index) {
        this.ballotBoxId = ballotBoxId;
        this.index = index;
    }
}
