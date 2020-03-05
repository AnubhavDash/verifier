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
public class CandidatePosition {

    private final String alias;
    // TODO: why to candidate positions hold candidate list ids ?
    private final UUID candidateListId;
    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private final List<BigInteger> primeNumbers;
    private final int initialAccumulation;
    private final boolean isBlank;

    @JsonCreator
    public CandidatePosition(@JsonProperty("alias") String alias,
                             @JsonProperty("candidateListId") UUID candidateListId,
                             @JsonProperty("primeNumber") List<BigInteger> primeNumber,
                             @JsonProperty("initialAccumulation") int initialAccumulation,
                             @JsonProperty("isBlank") boolean isBlank) {
        this.alias = alias;
        this.candidateListId = candidateListId;
        this.primeNumbers = ImmutableList.copyOf(primeNumber);
        this.initialAccumulation = initialAccumulation;
        this.isBlank = isBlank;
    }
}
