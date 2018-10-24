
package ch.post.it.evoting.verifier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "g",
    "p",
    "q"
})
public class ZpSubgroup {

    @JsonProperty("g")
    private String g;
    @JsonProperty("p")
    private String p;
    @JsonProperty("q")
    private String q;

    @JsonProperty("g")
    public String getG() {
        return g;
    }

    @JsonProperty("g")
    public void setG(String g) {
        this.g = g;
    }

    @JsonProperty("p")
    public String getP() {
        return p;
    }

    @JsonProperty("p")
    public void setP(String p) {
        this.p = p;
    }

    @JsonProperty("q")
    public String getQ() {
        return q;
    }

    @JsonProperty("q")
    public void setQ(String q) {
        this.q = q;
    }

}
