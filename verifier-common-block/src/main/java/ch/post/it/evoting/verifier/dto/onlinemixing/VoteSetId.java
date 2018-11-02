package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ballotBoxId",
    "index"
})
public class VoteSetId {

    @JsonProperty("ballotBoxId")
    private BallotBoxId ballotBoxId;
    @JsonProperty("index")
    private BigInteger index;

    @JsonProperty("ballotBoxId")
    public BallotBoxId getBallotBoxId() {
        return ballotBoxId;
    }

    @JsonProperty("ballotBoxId")
    public void setBallotBoxId(BallotBoxId ballotBoxId) {
        this.ballotBoxId = ballotBoxId;
    }

    @JsonProperty("index")
    public BigInteger getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(BigInteger index) {
        this.index = index;
    }

}
