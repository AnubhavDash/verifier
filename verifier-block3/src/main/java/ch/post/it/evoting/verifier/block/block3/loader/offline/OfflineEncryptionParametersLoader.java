/**
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
package ch.post.it.evoting.verifier.block.block3.loader.offline;

import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.block.block3.scytl.loader.EncryptionParametersLoader;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.EncryptionParameters;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;

public class OfflineEncryptionParametersLoader implements EncryptionParametersLoader {

    private final Path path;

    public OfflineEncryptionParametersLoader(Path path) {
        this.path = path;
    }

    @Override
    public ZpGroup getZpGroup() throws IOException {
        EncryptionParameters ep = Deserializer.fromJson(path.resolve(Block3VerificationSuite.PATH_CRYPTO_SETUP).toFile(), "encryptionParameters\\.json", EncryptionParameters.class);
        BigInteger p = TypeConverter.stringToBigInteger(ep.getP());
        BigInteger q = TypeConverter.stringToBigInteger(ep.getQ());
        ZpGroupParams zpGroupParams = new ZpGroupParams(p, q);
        return new ZpGroup(zpGroupParams, new ZpElement(TypeConverter.stringToBigInteger(ep.getG()), zpGroupParams));
    }
}
