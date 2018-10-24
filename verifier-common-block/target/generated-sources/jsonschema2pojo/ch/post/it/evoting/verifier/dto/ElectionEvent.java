
package ch.post.it.evoting.verifier.dto;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "alias",
    "ballotBoxes"
})
public class ElectionEvent {

    @JsonProperty("id")
    private String id;
    @JsonProperty("alias")
    private String alias;
    @JsonProperty("ballotBoxes")
    private List<BallotBox> ballotBoxes = new ArrayList<BallotBox>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("alias")
    public String getAlias() {
        return alias;
    }

    @JsonProperty("alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @JsonProperty("ballotBoxes")
    public List<BallotBox> getBallotBoxes() {
        return ballotBoxes;
    }

    @JsonProperty("ballotBoxes")
    public void setBallotBoxes(List<BallotBox> ballotBoxes) {
        this.ballotBoxes = ballotBoxes;
    }

}
