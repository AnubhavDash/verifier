
package ch.post.it.evoting.verifier.dto.onlinemixing;

import java.math.BigInteger;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "q",
    "value"
})
public class ExponentTildeR {

    @JsonProperty("q")
    private BigInteger q;
    @JsonProperty("value")
    private BigInteger value;

    @JsonProperty("q")
    public BigInteger getQ() {
        return q;
    }

    @JsonProperty("q")
    public void setQ(BigInteger q) {
        this.q = q;
    }

    @JsonProperty("value")
    public BigInteger getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(BigInteger value) {
        this.value = value;
    }

}
