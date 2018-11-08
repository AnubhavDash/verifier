
package ch.post.it.evoting.verifier.dto.onlinemixing;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "exponentsTildeA",
    "exponentsTildeB",
    "exponentTildeR",
    "exponentTildeS"
})
public class AnsSVA {

    @JsonProperty("exponentsTildeA")
    private List<ExponentsTildeA> exponentsTildeA = new ArrayList<ExponentsTildeA>();
    @JsonProperty("exponentsTildeB")
    private List<ExponentsTildeB> exponentsTildeB = new ArrayList<ExponentsTildeB>();
    @JsonProperty("exponentTildeR")
    private ExponentTildeR exponentTildeR;
    @JsonProperty("exponentTildeS")
    private ExponentTildeS exponentTildeS;

    @JsonProperty("exponentsTildeA")
    public List<ExponentsTildeA> getExponentsTildeA() {
        return exponentsTildeA;
    }

    @JsonProperty("exponentsTildeA")
    public void setExponentsTildeA(List<ExponentsTildeA> exponentsTildeA) {
        this.exponentsTildeA = exponentsTildeA;
    }

    @JsonProperty("exponentsTildeB")
    public List<ExponentsTildeB> getExponentsTildeB() {
        return exponentsTildeB;
    }

    @JsonProperty("exponentsTildeB")
    public void setExponentsTildeB(List<ExponentsTildeB> exponentsTildeB) {
        this.exponentsTildeB = exponentsTildeB;
    }

    @JsonProperty("exponentTildeR")
    public ExponentTildeR getExponentTildeR() {
        return exponentTildeR;
    }

    @JsonProperty("exponentTildeR")
    public void setExponentTildeR(ExponentTildeR exponentTildeR) {
        this.exponentTildeR = exponentTildeR;
    }

    @JsonProperty("exponentTildeS")
    public ExponentTildeS getExponentTildeS() {
        return exponentTildeS;
    }

    @JsonProperty("exponentTildeS")
    public void setExponentTildeS(ExponentTildeS exponentTildeS) {
        this.exponentTildeS = exponentTildeS;
    }

}
