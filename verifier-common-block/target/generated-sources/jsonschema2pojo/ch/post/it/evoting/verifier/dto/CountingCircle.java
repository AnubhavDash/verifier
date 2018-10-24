
package ch.post.it.evoting.verifier.dto;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "domainOfInfluence"
})
public class CountingCircle {

    @JsonProperty("id")
    private String id;
    @JsonProperty("domainOfInfluence")
    private List<DomainOfInfluence> domainOfInfluence = new ArrayList<DomainOfInfluence>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("domainOfInfluence")
    public List<DomainOfInfluence> getDomainOfInfluence() {
        return domainOfInfluence;
    }

    @JsonProperty("domainOfInfluence")
    public void setDomainOfInfluence(List<DomainOfInfluence> domainOfInfluence) {
        this.domainOfInfluence = domainOfInfluence;
    }

}
