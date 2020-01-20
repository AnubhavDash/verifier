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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
public class CandidateList {
    public final boolean isBlank;
    public final BigInteger primeNumber;
    public final List<CandidatePosition> candidatePositions;
    public final String alias;

    @JsonCreator
    public CandidateList(@JsonProperty("isBlank") boolean isBlank,
                         @JsonProperty("primeNumber") BigInteger primeNumber,
                         @JsonProperty("candidatePositions") List<CandidatePosition> candidatePositions,
                         @JsonProperty("alias") String alias) {
        this.isBlank = isBlank;
        this.primeNumber = primeNumber;
        this.candidatePositions = ImmutableList.copyOf(candidatePositions);
        this.alias = alias;
    }
}
