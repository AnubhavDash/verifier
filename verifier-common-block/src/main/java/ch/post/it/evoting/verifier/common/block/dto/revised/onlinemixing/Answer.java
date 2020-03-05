package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class Answer {

    private List<ExponentValue> exponentsA;
    private List<ExponentValue> exponentsB;
    private ExponentValue exponentR;
    private ExponentValue exponentS;
    private ExponentValue exponentT;

    public Answer(@JsonProperty("exponentsA") ExponentValue[] exponentsA,
                  @JsonProperty("exponentsB") ExponentValue[] exponentsB,
                  @JsonProperty("exponentR") ExponentValue exponentR,
                  @JsonProperty("exponentS") ExponentValue exponentS,
                  @JsonProperty("exponentT") ExponentValue exponentT) {
        this.exponentsA = ImmutableList.copyOf(exponentsA);
        this.exponentsB = ImmutableList.copyOf(exponentsB);
        this.exponentR = exponentR;
        this.exponentS = exponentS;
        this.exponentT = exponentT;
    }
}
