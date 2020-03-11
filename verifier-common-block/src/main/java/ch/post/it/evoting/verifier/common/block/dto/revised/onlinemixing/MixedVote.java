package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
public class MixedVote {

    private BigInteger gamma;
    private List<BigInteger> phis;

    @JsonCreator
    public MixedVote(@JsonProperty("gamma") BigInteger gamma,
                     @JsonProperty("phis") BigInteger[] phis) {
        this.gamma = gamma;
        this.phis = ImmutableList.copyOf(phis);
    }
}
