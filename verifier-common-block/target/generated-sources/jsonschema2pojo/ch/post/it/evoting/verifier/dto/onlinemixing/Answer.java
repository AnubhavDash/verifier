
package ch.post.it.evoting.verifier.dto.onlinemixing;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "exponentsA",
    "exponentsB",
    "exponentR",
    "exponentS",
    "exponentT"
})
public class Answer {

    @JsonProperty("exponentsA")
    private List<ExponentsA> exponentsA = new ArrayList<ExponentsA>();
    @JsonProperty("exponentsB")
    private List<ExponentsB> exponentsB = new ArrayList<ExponentsB>();
    @JsonProperty("exponentR")
    private ExponentR exponentR;
    @JsonProperty("exponentS")
    private ExponentS exponentS;
    @JsonProperty("exponentT")
    private ExponentT exponentT;

    @JsonProperty("exponentsA")
    public List<ExponentsA> getExponentsA() {
        return exponentsA;
    }

    @JsonProperty("exponentsA")
    public void setExponentsA(List<ExponentsA> exponentsA) {
        this.exponentsA = exponentsA;
    }

    @JsonProperty("exponentsB")
    public List<ExponentsB> getExponentsB() {
        return exponentsB;
    }

    @JsonProperty("exponentsB")
    public void setExponentsB(List<ExponentsB> exponentsB) {
        this.exponentsB = exponentsB;
    }

    @JsonProperty("exponentR")
    public ExponentR getExponentR() {
        return exponentR;
    }

    @JsonProperty("exponentR")
    public void setExponentR(ExponentR exponentR) {
        this.exponentR = exponentR;
    }

    @JsonProperty("exponentS")
    public ExponentS getExponentS() {
        return exponentS;
    }

    @JsonProperty("exponentS")
    public void setExponentS(ExponentS exponentS) {
        this.exponentS = exponentS;
    }

    @JsonProperty("exponentT")
    public ExponentT getExponentT() {
        return exponentT;
    }

    @JsonProperty("exponentT")
    public void setExponentT(ExponentT exponentT) {
        this.exponentT = exponentT;
    }

}
