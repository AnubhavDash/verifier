
package ch.post.it.evoting.verifier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "zpSubgroup"
})
public class EncryptionParametersZpSubGroup {

    @JsonProperty("zpSubgroup")
    private ZpSubgroup zpSubgroup;

    @JsonProperty("zpSubgroup")
    public ZpSubgroup getZpSubgroup() {
        return zpSubgroup;
    }

    @JsonProperty("zpSubgroup")
    public void setZpSubgroup(ZpSubgroup zpSubgroup) {
        this.zpSubgroup = zpSubgroup;
    }

}
