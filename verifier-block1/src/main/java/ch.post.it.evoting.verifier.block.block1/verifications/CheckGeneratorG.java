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
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.EncryptionParameters;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Map;

public class CheckGeneratorG extends AbstractVerification {

    private static final Logger LOGGER = Logger.getLogger(CheckGeneratorG.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification10.description"));
        def.setId(10);
        def.setName("checkGenerator(g)");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        try {
            Path path = inputDirectory.toPath().resolve(Block1VerificationSuite.PATH_CRYPTO_SETUP);
            EncryptionParameters encryptionParameters = Deserializer.fromJson(path.toFile(), "encryptionParameters\\.json", EncryptionParameters.class);
            BigInteger g = TypeConverter.stringToBigInteger(encryptionParameters.getG());
            BigInteger p = TypeConverter.stringToBigInteger(encryptionParameters.getP());

            if (!BigInteger.valueOf(2).equals(g)) {
                throw new Test10Exception("g does not equal 2", TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification10.nok.message"));
            }

            if (!MathHelper.isEulerCriterionValid(g, p)) {
                throw new Test10Exception("g is not part of the subgroup q", TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification10.euler.nok.message"));
            }

            result.setStatus(Status.OK);
        } catch (Test10Exception e) {
            LOGGER.info(String.format("Block1 Test10 failed, because %s", e.getDetail()));
            result.setStatus(Status.NOK);
            result.setMessage(e.getMsg());
        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification10.file.not.found.message"));
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }

    class Test10Exception extends RuntimeException {
        String detail;
        Map<Language, String> msg;

        Test10Exception(String detail, Map<Language, String> msg) {
            this.detail = detail;
            this.msg = msg;
        }

        public String getDetail() {
            return detail;
        }

        public Map<Language, String> getMsg() {
            return msg;
        }

    }
}
