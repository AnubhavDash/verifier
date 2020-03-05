package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class AnsSVA {

    private List<ExponentValue> exponentsTildeA;
    private List<ExponentValue> exponentsTildeB;
    private ExponentValue exponentTildeR;
    private ExponentValue exponentTildeS;

    public AnsSVA(@JsonProperty("exponentsTildeA") ExponentValue[] exponentsTildeA,
                  @JsonProperty("exponentsTildeB") ExponentValue[] exponentsTildeB,
                  @JsonProperty("exponentTildeR") ExponentValue exponentTildeR,
                  @JsonProperty("exponentTildeS") ExponentValue exponentTildeS) {
        this.exponentsTildeA = ImmutableList.copyOf(exponentsTildeA);
        this.exponentsTildeB = ImmutableList.copyOf(exponentsTildeB);
        this.exponentTildeR = exponentTildeR;
        this.exponentTildeS = exponentTildeS;
    }
}
