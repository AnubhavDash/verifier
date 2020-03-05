package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BallotBoxId {

    private String tenantId;
    private String electionEventId;
    private String id;

    public BallotBoxId(@JsonProperty("tenantId") String tenantId,
                       @JsonProperty("electionEventId") String electionEventId,
                       @JsonProperty("id") String id) {
        this.tenantId = tenantId;
        this.electionEventId = electionEventId;
        this.id = id;
    }
}
