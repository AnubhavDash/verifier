
package ch.post.it.evoting.verifier.dto.onlinemixing;

import java.math.BigInteger;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "value",
    "p",
    "q"
})
public class Element__10 {

    @JsonProperty("value")
    private BigInteger value;
    @JsonProperty("p")
    private BigInteger p;
    @JsonProperty("q")
    private BigInteger q;

    @JsonProperty("value")
    public BigInteger getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(BigInteger value) {
        this.value = value;
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
