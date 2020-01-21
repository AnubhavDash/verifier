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

import java.util.Base64;
import java.util.List;

public class Metadata {
    public final String version;
    public final List<SignedItem> signedItems;
    public final String algorithm;
    public final String base64Signature;

    @JsonCreator
    public Metadata(@JsonProperty("version") String version,
                    @JsonProperty("signed") List<SignedItem> signedItems,
                    @JsonProperty("alg") String algorithm,
                    @JsonProperty("signature") String base64Signature) {
        this.version = version;
        this.signedItems = ImmutableList.copyOf(signedItems);
        this.algorithm = algorithm;
        this.base64Signature = base64Signature;
    }

    public byte[] getSignature() {
        return Base64.getDecoder().decode(base64Signature);
    }
}
