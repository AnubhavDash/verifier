
package ch.post.it.evoting.verifier.dto.onlinemixing;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "gamma",
    "phis"
})
public class Vote {

    @JsonProperty("gamma")
    private BigInteger gamma;
    @JsonProperty("phis")
    private List<BigInteger> phis = new ArrayList<BigInteger>();

    @JsonProperty("gamma")
    public BigInteger getGamma() {
        return gamma;
    }

    @JsonProperty("gamma")
    public void setGamma(BigInteger gamma) {
        this.gamma = gamma;
    }

    @JsonProperty("phis")
    public List<BigInteger> getPhis() {
        return phis;
    }

    @JsonProperty("phis")
    public void setPhis(List<BigInteger> phis) {
        this.phis = phis;
    }

}
