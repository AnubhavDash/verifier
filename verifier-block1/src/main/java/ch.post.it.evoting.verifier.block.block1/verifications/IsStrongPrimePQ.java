/**
 * This file is part of Verifier Swiss Post.
 * <p>
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;

public class IsStrongPrimePQ extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.description"));
        def.setId(3);
        def.setName("isStrongPrime(p,q)");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult verify(File inputDirectory) throws Exception {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        Path path = inputDirectory.toPath().resolve(Block1VerificationSuite.PATH_CRYPTO_SETUP);
        EncryptionGroup encryptionGroup = Deserializer.fromJson(path.toFile(), "encryptionParameters\\.json", EncryptionGroup.class);

        if (!encryptionGroup.getP().equals((encryptionGroup.getQ().multiply(BigInteger.valueOf(2))).add(BigInteger.ONE))) {
            throw buildVerificationFailureException(
                    "p is not a strong prime",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification03.nok.message"
            );
        }

        result.setStatus(Status.OK);
        return result;
    }
}
