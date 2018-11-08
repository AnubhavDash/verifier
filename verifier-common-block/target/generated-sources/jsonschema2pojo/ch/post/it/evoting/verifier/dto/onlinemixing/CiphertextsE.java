
package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "gamma",
    "phis"
})
public class CiphertextsE {

    @JsonProperty("gamma")
    private String gamma;
    @JsonProperty("phis")
    private String phis;

    @JsonProperty("gamma")
    public String getGamma() {
        return gamma;
    }

    @JsonProperty("gamma")
    public void setGamma(String gamma) {
        this.gamma = gamma;
    }

    @JsonProperty("phis")
    public String getPhis() {
        return phis;
    }

    @JsonProperty("phis")
    public void setPhis(String phis) {
        this.phis = phis;
    }

}
