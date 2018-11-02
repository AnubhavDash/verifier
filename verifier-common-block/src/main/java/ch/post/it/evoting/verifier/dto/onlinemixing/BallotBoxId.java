package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "tenantId",
    "electionEventId",
    "id"
})
public class BallotBoxId {

    @JsonProperty("tenantId")
    private String tenantId;
    @JsonProperty("electionEventId")
    private String electionEventId;
    @JsonProperty("id")
    private String id;

    @JsonProperty("tenantId")
    public String getTenantId() {
        return tenantId;
    }

    @JsonProperty("tenantId")
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @JsonProperty("electionEventId")
    public String getElectionEventId() {
        return electionEventId;
    }

    @JsonProperty("electionEventId")
    public void setElectionEventId(String electionEventId) {
        this.electionEventId = electionEventId;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

}
