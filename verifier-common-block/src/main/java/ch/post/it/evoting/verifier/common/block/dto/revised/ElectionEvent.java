package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class ElectionEvent {
    public final UUID id;
    public final String alias;
    public final List<BallotBox> ballotBoxes;

    @JsonCreator
    public ElectionEvent(@JsonProperty("id") UUID id,
                         @JsonProperty("alias") String alias,
                         @JsonProperty("ballotBoxes") List<BallotBox> ballotBoxes) {
        this.id = id;
        this.alias = alias;
        this.ballotBoxes = ImmutableList.copyOf(ballotBoxes);
    }
}
