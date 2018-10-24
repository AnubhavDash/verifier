
package ch.post.it.evoting.verifier.dto;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "alias",
    "numberOfSeats",
    "lists",
    "changedBallotsWithPartyAffiliation",
    "changedBallotsWithoutPartyAffiliation",
    "emptyVotesOfChangedBallotsWithoutPartyAffiliation",
    "writeIns",
    "candidates"
})
public class Election {

    @JsonProperty("id")
    private String id;
    @JsonProperty("alias")
    private String alias;
    @JsonProperty("numberOfSeats")
    private Integer numberOfSeats;
    @JsonProperty("lists")
    private java.util.List<ch.post.it.evoting.verifier.dto.List> lists = new ArrayList<ch.post.it.evoting.verifier.dto.List>();
    @JsonProperty("changedBallotsWithPartyAffiliation")
    private Integer changedBallotsWithPartyAffiliation;
    @JsonProperty("changedBallotsWithoutPartyAffiliation")
    private Integer changedBallotsWithoutPartyAffiliation;
    @JsonProperty("emptyVotesOfChangedBallotsWithoutPartyAffiliation")
    private Integer emptyVotesOfChangedBallotsWithoutPartyAffiliation;
    @JsonProperty("writeIns")
    private java.util.List<String> writeIns = new ArrayList<String>();
    @JsonProperty("candidates")
    private java.util.List<Candidate> candidates = new ArrayList<Candidate>();

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

    @JsonProperty("numberOfSeats")
    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    @JsonProperty("numberOfSeats")
    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    @JsonProperty("lists")
    public java.util.List<ch.post.it.evoting.verifier.dto.List> getLists() {
        return lists;
    }

    @JsonProperty("lists")
    public void setLists(java.util.List<ch.post.it.evoting.verifier.dto.List> lists) {
        this.lists = lists;
    }

    @JsonProperty("changedBallotsWithPartyAffiliation")
    public Integer getChangedBallotsWithPartyAffiliation() {
        return changedBallotsWithPartyAffiliation;
    }

    @JsonProperty("changedBallotsWithPartyAffiliation")
    public void setChangedBallotsWithPartyAffiliation(Integer changedBallotsWithPartyAffiliation) {
        this.changedBallotsWithPartyAffiliation = changedBallotsWithPartyAffiliation;
    }

    @JsonProperty("changedBallotsWithoutPartyAffiliation")
    public Integer getChangedBallotsWithoutPartyAffiliation() {
        return changedBallotsWithoutPartyAffiliation;
    }

    @JsonProperty("changedBallotsWithoutPartyAffiliation")
    public void setChangedBallotsWithoutPartyAffiliation(Integer changedBallotsWithoutPartyAffiliation) {
        this.changedBallotsWithoutPartyAffiliation = changedBallotsWithoutPartyAffiliation;
    }

    @JsonProperty("emptyVotesOfChangedBallotsWithoutPartyAffiliation")
    public Integer getEmptyVotesOfChangedBallotsWithoutPartyAffiliation() {
        return emptyVotesOfChangedBallotsWithoutPartyAffiliation;
    }

    @JsonProperty("emptyVotesOfChangedBallotsWithoutPartyAffiliation")
    public void setEmptyVotesOfChangedBallotsWithoutPartyAffiliation(Integer emptyVotesOfChangedBallotsWithoutPartyAffiliation) {
        this.emptyVotesOfChangedBallotsWithoutPartyAffiliation = emptyVotesOfChangedBallotsWithoutPartyAffiliation;
    }

    @JsonProperty("writeIns")
    public java.util.List<String> getWriteIns() {
        return writeIns;
    }

    @JsonProperty("writeIns")
    public void setWriteIns(java.util.List<String> writeIns) {
        this.writeIns = writeIns;
    }

    @JsonProperty("candidates")
    public java.util.List<Candidate> getCandidates() {
        return candidates;
    }

    @JsonProperty("candidates")
    public void setCandidates(java.util.List<Candidate> candidates) {
        this.candidates = candidates;
    }

}
