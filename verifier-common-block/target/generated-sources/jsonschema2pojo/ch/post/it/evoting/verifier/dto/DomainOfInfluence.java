
package ch.post.it.evoting.verifier.dto;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "votes",
    "elections"
})
public class DomainOfInfluence {

    @JsonProperty("id")
    private String id;
    @JsonProperty("votes")
    private List<Vote> votes = new ArrayList<Vote>();
    @JsonProperty("elections")
    private List<Election> elections = new ArrayList<Election>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("votes")
    public List<Vote> getVotes() {
        return votes;
    }

    @JsonProperty("votes")
    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    @JsonProperty("elections")
    public List<Election> getElections() {
        return elections;
    }

    @JsonProperty("elections")
    public void setElections(List<Election> elections) {
        this.elections = elections;
    }

}
