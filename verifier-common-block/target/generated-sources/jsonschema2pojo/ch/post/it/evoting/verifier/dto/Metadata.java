
package ch.post.it.evoting.verifier.dto;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "version",
    "signed",
    "alg",
    "signature"
})
public class Metadata {

    @JsonProperty("version")
    private String version;
    @JsonProperty("signed")
    private List<Signed> signed = new ArrayList<Signed>();
    @JsonProperty("alg")
    private String alg;
    @JsonProperty("signature")
    private String signature;

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("signed")
    public List<Signed> getSigned() {
        return signed;
    }

    @JsonProperty("signed")
    public void setSigned(List<Signed> signed) {
        this.signed = signed;
    }

    @JsonProperty("alg")
    public String getAlg() {
        return alg;
    }

    @JsonProperty("alg")
    public void setAlg(String alg) {
        this.alg = alg;
    }

    @JsonProperty("signature")
    public String getSignature() {
        return signature;
    }

    @JsonProperty("signature")
    public void setSignature(String signature) {
        this.signature = signature;
    }

}
