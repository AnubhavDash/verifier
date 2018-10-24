
package ch.post.it.evoting.verifier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "publicKey"
})
public class PublicKey {

    @JsonProperty("publicKey")
    private PublicKey__1 publicKey;

    @JsonProperty("publicKey")
    public PublicKey__1 getPublicKey() {
        return publicKey;
    }

    @JsonProperty("publicKey")
    public void setPublicKey(PublicKey__1 publicKey) {
        this.publicKey = publicKey;
    }

}
