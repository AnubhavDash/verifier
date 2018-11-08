
package ch.post.it.evoting.verifier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "zkProof"
})
public class OnlineDecryptionProof {

    @JsonProperty("zkProof")
    private ZkProof zkProof;

    @JsonProperty("zkProof")
    public ZkProof getZkProof() {
        return zkProof;
    }

    @JsonProperty("zkProof")
    public void setZkProof(ZkProof zkProof) {
        this.zkProof = zkProof;
    }

}
