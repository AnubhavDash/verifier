package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Vote {
    public final UUID id;
    public final String alias;
    public final List<VoteQuestion> questions;

    @JsonCreator
    public Vote(@JsonProperty("id") UUID id,
                @JsonProperty("alias") String alias,
                @JsonProperty("questions") List<VoteQuestion> questions) {
        this.id = id;
        this.alias = alias;
        this.questions = questions;
    }
}
