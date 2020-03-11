package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class SingleValueArgumentAnswer {

    private List<Exponent> exponentsTildeA;
    private List<Exponent> exponentsTildeB;
    private Exponent exponentTildeR;
    private Exponent exponentTildeS;

    public SingleValueArgumentAnswer(@JsonProperty("exponentsTildeA") Exponent[] exponentsTildeA,
                                     @JsonProperty("exponentsTildeB") Exponent[] exponentsTildeB,
                                     @JsonProperty("exponentTildeR") Exponent exponentTildeR,
                                     @JsonProperty("exponentTildeS") Exponent exponentTildeS) {
        this.exponentsTildeA = ImmutableList.copyOf(exponentsTildeA);
        this.exponentsTildeB = ImmutableList.copyOf(exponentsTildeB);
        this.exponentTildeR = exponentTildeR;
        this.exponentTildeS = exponentTildeS;
    }
}
