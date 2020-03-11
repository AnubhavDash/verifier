package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class ZeroArgumentAnswer {

    private List<Exponent> exponentsA;
    private List<Exponent> exponentsB;
    private Exponent exponentR;
    private Exponent exponentS;
    private Exponent exponentT;

    public ZeroArgumentAnswer(@JsonProperty("exponentsA") Exponent[] exponentsA,
                              @JsonProperty("exponentsB") Exponent[] exponentsB,
                              @JsonProperty("exponentR") Exponent exponentR,
                              @JsonProperty("exponentS") Exponent exponentS,
                              @JsonProperty("exponentT") Exponent exponentT) {
        this.exponentsA = ImmutableList.copyOf(exponentsA);
        this.exponentsB = ImmutableList.copyOf(exponentsB);
        this.exponentR = exponentR;
        this.exponentS = exponentS;
        this.exponentT = exponentT;
    }
}
