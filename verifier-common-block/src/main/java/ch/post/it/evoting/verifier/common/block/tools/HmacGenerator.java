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
package ch.post.it.evoting.verifier.common.block.tools;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

public class HmacGenerator {
    private HmacGenerator() {
        //private ctor, use static methods
    }

    public static byte[] hash(byte[] text, byte[] key) {
        HMac hmac = new HMac(new SHA256Digest());

        hmac.init(new KeyParameter(key));
        byte[] result = new byte[hmac.getMacSize()];
        hmac.update(text, 0, text.length);
        hmac.doFinal(result, 0);

        return result;
    }
}
