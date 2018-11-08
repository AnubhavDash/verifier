
package ch.post.it.evoting.verifier.dto.onlinemixing;

import java.math.BigInteger;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "g",
    "p",
    "q"
})
public class ZpSubgroup__1 {

    @JsonProperty("g")
    private BigInteger g;
    @JsonProperty("p")
    private BigInteger p;
    @JsonProperty("q")
    private BigInteger q;

    @JsonProperty("g")
    public BigInteger getG() {
        return g;
    }

    @JsonProperty("g")
    public void setG(BigInteger g) {
        this.g = g;
    }

    @JsonProperty("p")
    public BigInteger getP() {
        return p;
    }

    @JsonProperty("p")
    public void setP(BigInteger p) {
        this.p = p;
    }

    @JsonProperty("q")
    public BigInteger getQ() {
        return q;
    }

    @JsonProperty("q")
    public void setQ(BigInteger q) {
        this.q = q;
    }

}
