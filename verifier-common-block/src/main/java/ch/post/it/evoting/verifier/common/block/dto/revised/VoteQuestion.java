package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class VoteQuestion {
    public final String alias;
    public final List<VoteOption> options;

    @JsonCreator
    public VoteQuestion(@JsonProperty("alias") String alias,
                        @JsonProperty("options") List<VoteOption> options) {
        this.alias = alias;
        this.options = ImmutableList.copyOf(options);
    }
}
