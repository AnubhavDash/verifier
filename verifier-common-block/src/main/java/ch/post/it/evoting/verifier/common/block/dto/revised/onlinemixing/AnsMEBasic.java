package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class AnsMEBasic {

    private List<ExponentValue> exponentsA;
    private ExponentValue exponentR;
    private ExponentValue exponentsB;
    private ExponentValue exponentS;
    private RandomnessTau randomnessTau;

    public AnsMEBasic(@JsonProperty("exponentsA") ExponentValue[] exponentsA,
                      @JsonProperty("exponentR") ExponentValue exponentR,
                      @JsonProperty("exponentsB") ExponentValue exponentsB,
                      @JsonProperty("exponentS") ExponentValue exponentS,
                      @JsonProperty("randomnessTau") RandomnessTau randomnessTau) {
        this.exponentsA = ImmutableList.copyOf(exponentsA);
        this.exponentR = exponentR;
        this.exponentsB = exponentsB;
        this.exponentS = exponentS;
        this.randomnessTau = randomnessTau;
    }
}
