/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.common.block.dto.revised;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Getter
public class Election {

    private final UUID id;
    private final String alias;
    private final int numberOfSeats;
    private final List<CandidateList> lists;
    private final List<Candidate> candidates;
    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private final List<BigInteger> writeIns;
    // TODO: why are the following properties given as ints, shouldn't they be booleans?
    private final int changedBallotsWithPartyAffiliation;
    private final int changedBallotsWithoutPartyAffiliation;
    private final int emptyVotesOfChangedBallotsWithoutPartyAffiliation;

    @JsonCreator
    public Election(@JsonProperty("id") UUID id,
                    @JsonProperty("alias") String alias,
                    @JsonProperty("numberOfSeats") int numberOfSeats,
                    @JsonProperty("lists") CandidateList[] lists,
                    @JsonProperty("candidates") Candidate[] candidates,
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
