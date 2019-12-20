package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class DomainOfInfluence {
    public final UUID id;
    public final List<Vote> votes;
    public final List<Election> elections;


    @JsonCreator
    public DomainOfInfluence(@JsonProperty("id") UUID id,
                             @JsonProperty("votes") List<Vote> votes,
                             @JsonProperty("elections") List<Election> elections) {
        this.id = id;
        this.votes = ImmutableList.copyOf(votes);
        this.elections = ImmutableList.copyOf(elections);
    }
}
