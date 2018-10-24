
package ch.post.it.evoting.verifier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "electionEvent"
})
public class DataConfigEE {

    @JsonProperty("electionEvent")
    private ElectionEvent electionEvent;

    @JsonProperty("electionEvent")
    public ElectionEvent getElectionEvent() {
        return electionEvent;
    }

    @JsonProperty("electionEvent")
    public void setElectionEvent(ElectionEvent electionEvent) {
        this.electionEvent = electionEvent;
    }

}
