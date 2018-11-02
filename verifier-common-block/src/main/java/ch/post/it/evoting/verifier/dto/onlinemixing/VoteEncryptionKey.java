package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "zpSubgroup",
    "elements"
})
public class VoteEncryptionKey {

    @JsonProperty("zpSubgroup")
    private ZpSubgroup zpSubgroup;
    @JsonProperty("elements")
    private List<BigInteger> elements = new ArrayList<BigInteger>();

    @JsonProperty("zpSubgroup")
    public ZpSubgroup getZpSubgroup() {
        return zpSubgroup;
    }

    @JsonProperty("zpSubgroup")
    public void setZpSubgroup(ZpSubgroup zpSubgroup) {
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
