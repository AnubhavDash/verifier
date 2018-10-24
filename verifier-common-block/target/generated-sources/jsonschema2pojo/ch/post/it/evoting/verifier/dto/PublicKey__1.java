
package ch.post.it.evoting.verifier.dto;

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
public class PublicKey__1 {

    @JsonProperty("zpSubgroup")
    private ZpSubgroup__1 zpSubgroup;
    @JsonProperty("elements")
    private List<String> elements = new ArrayList<String>();

    @JsonProperty("zpSubgroup")
    public ZpSubgroup__1 getZpSubgroup() {
        return zpSubgroup;
    }

    @JsonProperty("zpSubgroup")
    public void setZpSubgroup(ZpSubgroup__1 zpSubgroup) {
        this.zpSubgroup = zpSubgroup;
    }

    @JsonProperty("elements")
    public List<String> getElements() {
        return elements;
    }

    @JsonProperty("elements")
    public void setElements(List<String> elements) {
        this.elements = elements;
    }

}
