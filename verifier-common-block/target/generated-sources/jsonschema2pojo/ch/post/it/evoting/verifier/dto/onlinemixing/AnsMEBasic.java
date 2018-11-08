
package ch.post.it.evoting.verifier.dto.onlinemixing;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "exponentsA",
    "exponentR",
    "exponentsB",
    "exponentS",
    "randomnessTau"
})
public class AnsMEBasic {

    @JsonProperty("exponentsA")
    private List<ExponentsA__1> exponentsA = new ArrayList<ExponentsA__1>();
    @JsonProperty("exponentR")
    private ExponentR__1 exponentR;
    @JsonProperty("exponentsB")
    private ExponentsB__1 exponentsB;
    @JsonProperty("exponentS")
    private ExponentS__1 exponentS;
    @JsonProperty("randomnessTau")
    private RandomnessTau randomnessTau;

    @JsonProperty("exponentsA")
    public List<ExponentsA__1> getExponentsA() {
        return exponentsA;
    }

    @JsonProperty("exponentsA")
    public void setExponentsA(List<ExponentsA__1> exponentsA) {
        this.exponentsA = exponentsA;
    }

    @JsonProperty("exponentR")
    public ExponentR__1 getExponentR() {
        return exponentR;
    }

    @JsonProperty("exponentR")
    public void setExponentR(ExponentR__1 exponentR) {
        this.exponentR = exponentR;
    }

    @JsonProperty("exponentsB")
    public ExponentsB__1 getExponentsB() {
        return exponentsB;
    }

    @JsonProperty("exponentsB")
    public void setExponentsB(ExponentsB__1 exponentsB) {
        this.exponentsB = exponentsB;
    }

    @JsonProperty("exponentS")
    public ExponentS__1 getExponentS() {
        return exponentS;
    }

    @JsonProperty("exponentS")
    public void setExponentS(ExponentS__1 exponentS) {
        this.exponentS = exponentS;
    }

    @JsonProperty("randomnessTau")
    public RandomnessTau getRandomnessTau() {
        return randomnessTau;
    }

    @JsonProperty("randomnessTau")
    public void setRandomnessTau(RandomnessTau randomnessTau) {
        this.randomnessTau = randomnessTau;
    }

}
