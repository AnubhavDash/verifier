package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class MultiExponentiationArgumentAnswer {

    private final List<Exponent> exponentsA;
    private final Exponent exponentR;
    private final Exponent exponentsB;
    private final Exponent exponentS;
    private final RandomnessTau randomnessTau;

    public MultiExponentiationArgumentAnswer(@JsonProperty("exponentsA") Exponent[] exponentsA,
                                             @JsonProperty("exponentR") Exponent exponentR,
                                             @JsonProperty("exponentsB") Exponent exponentsB,
                                             @JsonProperty("exponentS") Exponent exponentS,
                                             @JsonProperty("randomnessTau") RandomnessTau randomnessTau) {
        this.exponentsA = ImmutableList.copyOf(exponentsA);
        this.exponentR = exponentR;
        this.exponentsB = exponentsB;
        this.exponentS = exponentS;
        this.randomnessTau = randomnessTau;
    }
}
