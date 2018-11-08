
package ch.post.it.evoting.verifier.dto.onlinemixing;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "zpSubgroup",
    "elements"
})
public class PreviousVoteEncryptionKey {

    @JsonProperty("zpSubgroup")
    private ZpSubgroup__1 zpSubgroup;
    @JsonProperty("elements")
    private List<BigInteger> elements = new ArrayList<BigInteger>();

    @JsonProperty("zpSubgroup")
    public ZpSubgroup__1 getZpSubgroup() {
        return zpSubgroup;
    }

    @JsonProperty("zpSubgroup")
    public void setZpSubgroup(ZpSubgroup__1 zpSubgroup) {
        this.zpSubgroup = zpSubgroup;
    }

    @JsonProperty("elements")
    public List<BigInteger> getElements() {
        return elements;
    }

    @JsonProperty("elements")
    public void setElements(List<BigInteger> elements) {
        this.elements = elements;
    }

}
