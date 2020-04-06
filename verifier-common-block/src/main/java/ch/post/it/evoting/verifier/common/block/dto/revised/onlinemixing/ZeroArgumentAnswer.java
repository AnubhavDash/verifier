package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class ZeroArgumentAnswer {

    private final List<Exponent> exponentsA;
    private final List<Exponent> exponentsB;
    private final Exponent exponentR;
    private final Exponent exponentS;
    private final Exponent exponentT;

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
