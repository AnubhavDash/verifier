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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class DomainOfInfluence {

    private final String id;
    private final List<Vote> votes;
    private final List<Election> elections;

    public DomainOfInfluence(@JsonProperty("id") String id,
                             @JsonProperty("votes") Vote[] votes,
                             @JsonProperty("elections") Election[] elections) {
        this.id = id;
        this.votes = ImmutableList.copyOf(votes);
        this.elections = ImmutableList.copyOf(elections);
    }
}
