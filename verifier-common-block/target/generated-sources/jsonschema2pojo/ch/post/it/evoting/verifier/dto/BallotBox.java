
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
    "authId",
    "vcsId",
    "vcsAlias",
    "votingCardsGenerated",
    "countingCircles"
})
public class BallotBox {

    @JsonProperty("id")
    private String id;
    @JsonProperty("alias")
    private String alias;
    @JsonProperty("authId")
    private String authId;
    @JsonProperty("vcsId")
    private String vcsId;
    @JsonProperty("vcsAlias")
    private String vcsAlias;
    @JsonProperty("votingCardsGenerated")
    private Integer votingCardsGenerated;
    @JsonProperty("countingCircles")
    private List<CountingCircle> countingCircles = new ArrayList<CountingCircle>();

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

    @JsonProperty("authId")
    public String getAuthId() {
        return authId;
    }

    @JsonProperty("authId")
    public void setAuthId(String authId) {
        this.authId = authId;
    }

    @JsonProperty("vcsId")
    public String getVcsId() {
        return vcsId;
    }

    @JsonProperty("vcsId")
    public void setVcsId(String vcsId) {
        this.vcsId = vcsId;
    }

    @JsonProperty("vcsAlias")
    public String getVcsAlias() {
        return vcsAlias;
    }

    @JsonProperty("vcsAlias")
    public void setVcsAlias(String vcsAlias) {
        this.vcsAlias = vcsAlias;
    }

    @JsonProperty("votingCardsGenerated")
    public Integer getVotingCardsGenerated() {
        return votingCardsGenerated;
    }

    @JsonProperty("votingCardsGenerated")
    public void setVotingCardsGenerated(Integer votingCardsGenerated) {
        this.votingCardsGenerated = votingCardsGenerated;
    }

    @JsonProperty("countingCircles")
    public List<CountingCircle> getCountingCircles() {
        return countingCircles;
    }

    @JsonProperty("countingCircles")
    public void setCountingCircles(List<CountingCircle> countingCircles) {
        this.countingCircles = countingCircles;
    }

}
