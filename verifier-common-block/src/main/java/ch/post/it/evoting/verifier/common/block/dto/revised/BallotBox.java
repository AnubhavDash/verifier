package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class BallotBox {
    public final UUID id;
    public final String alias;
    public final UUID authId;
    public final UUID vcsId;
    public final String vcsAlias;
    public final int votingCardsGenerated;
    public final List<CountingCircle> countingCircles;

    @JsonCreator
    public BallotBox(@JsonProperty("id") UUID id,
                     @JsonProperty("alias") String alias,
                     @JsonProperty("authId") UUID authId,
                     @JsonProperty("vcsId") UUID vcsId,
                     @JsonProperty("vcsAlias") String vcsAlias,
                     @JsonProperty("votingCardsGenerated") int votingCardsGenerated,
                     @JsonProperty("countingCircles") List<CountingCircle> countingCircles) {
        this.id = id;
        this.alias = alias;
        this.authId = authId;
        this.vcsId = vcsId;
        this.vcsAlias = vcsAlias;
        this.votingCardsGenerated = votingCardsGenerated;
        this.countingCircles = ImmutableList.copyOf(countingCircles);
    }
}
