package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

public class Election {
    public final UUID id;
    public final String alias;
    public final int numberOfSeats;
    public final List<CandidateList> lists;
    public final List<Candidate> candidates;
    public final List<BigInteger> writeIns;
    // TODO: why are the following properties given as ints, shouldn't they be booleans?
    public final int changedBallotsWithPartyAffiliation;
    public final int changedBallotsWithoutPartyAffiliation;
    public final int emptyVotesOfChangedBallotsWithoutPartyAffiliation;

    @JsonCreator
    public Election(@JsonProperty("id") UUID id,
                    @JsonProperty("alias") String alias,
                    @JsonProperty("numberOfSeats") int numberOfSeats,
                    @JsonProperty("lists") List<CandidateList> lists,
                    @JsonProperty("candidates") List<Candidate> candidates,
                    @JsonProperty("writeIns") List<BigInteger> writeIns,
                    @JsonProperty("changedBallotsWithPartyAffiliation") int changedBallotsWithPartyAffiliation,
                    @JsonProperty("changedBallotsWithoutPartyAffiliation") int changedBallotsWithoutPartyAffiliation,
                    @JsonProperty("emptyVotesOfChangedBallotsWithoutPartyAffiliation") int emptyVotesOfChangedBallotsWithoutPartyAffiliation) {
        this.id = id;
        this.alias = alias;
        this.numberOfSeats = numberOfSeats;
        this.lists = lists != null ? ImmutableList.copyOf(lists) : ImmutableList.of();
        this.candidates = candidates != null ? ImmutableList.copyOf(candidates) : ImmutableList.of();
        this.writeIns = writeIns != null ? ImmutableList.copyOf(writeIns) : ImmutableList.of();
        this.changedBallotsWithPartyAffiliation = changedBallotsWithPartyAffiliation;
        this.changedBallotsWithoutPartyAffiliation = changedBallotsWithoutPartyAffiliation;
        this.emptyVotesOfChangedBallotsWithoutPartyAffiliation = emptyVotesOfChangedBallotsWithoutPartyAffiliation;
    }
}
