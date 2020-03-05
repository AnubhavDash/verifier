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
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Base64;

@Getter
public class AuthenticationToken {

    private final String id;
    private final VoterInformation voterInformation;
    private final LocalDateTime timestamp;
    private final String base64Signature;

    @JsonCreator
    public AuthenticationToken(@JsonProperty("id") String id,
                               @JsonProperty("voterInformation") VoterInformation voterInformation,
                               @JsonProperty("timestamp") LocalDateTime timestamp,
                               @JsonProperty("signature") String base64Signature) {
        this.id = id;
        this.voterInformation = voterInformation;
        this.timestamp = timestamp;
        this.base64Signature = base64Signature;
    }

    public byte[] getSignature() {
        return Base64.getDecoder().decode(base64Signature);
    }
}
