package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "signatureContents",
    "certificateChain"
})
public class Signature {

    @JsonProperty("signatureContents")
    private String signatureContents;
    @JsonProperty("certificateChain")
    private List<String> certificateChain = new ArrayList<String>();

    @JsonProperty("signatureContents")
    public String getSignatureContents() {
        return signatureContents;
    }

    @JsonProperty("signatureContents")
    public void setSignatureContents(String signatureContents) {
        this.signatureContents = signatureContents;
    }

    @JsonProperty("certificateChain")
    public List<String> getCertificateChain() {
        return certificateChain;
    }

    @JsonProperty("certificateChain")
    public void setCertificateChain(List<String> certificateChain) {
        this.certificateChain = certificateChain;
    }

}
