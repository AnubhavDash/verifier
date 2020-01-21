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
import lombok.Getter;

import java.math.BigInteger;
import java.util.Base64;

@Getter
public class PreImageProof {
    public final BigInteger q; // Check if necessary ?!? why is it repeated here?
    public final String base64hash;
    public final BigInteger z;

    public PreImageProof(@JsonProperty("q") BigInteger q, @JsonProperty("hash") String base64hash,
                         @JsonProperty("z") BigInteger z) {
        this.q = q;
        this.base64hash = base64hash;
        this.z = z;
    }

    public byte[] getH() {
        return Base64.getDecoder().decode(base64hash);
    }
}
