package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BallotBoxId {

    private final String tenantId;
    private final String electionEventId;
    private final String id;

    public BallotBoxId(@JsonProperty("tenantId") String tenantId,
                       @JsonProperty("electionEventId") String electionEventId,
                       @JsonProperty("id") String id) {
        this.tenantId = tenantId;
        this.electionEventId = electionEventId;
        this.id = id;
    }
}
