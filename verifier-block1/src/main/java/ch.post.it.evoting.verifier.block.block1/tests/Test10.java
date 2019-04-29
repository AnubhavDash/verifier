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
package ch.post.it.evoting.verifier.block.block1.tests;

import ch.post.it.evoting.verifier.block.block1.Block1TestSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.Test;
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

public class Test10 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test10.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test10.description"));
        def.setId(10);
        def.setName("checkGenerator(q)");
        def.addTestTrait(TestTrait.PreDecryption);
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            Path path = inputDirectory.toPath().resolve(Block1TestSuite.PATH_CRYPTO_SETUP);
            EncryptionParameters encryptionParameters = Deserializer.fromJson(path.toFile(), "encryptionParameters\\.json", EncryptionParameters.class);
            BigInteger g = TypeConverter.stringToBigInteger(encryptionParameters.getG());
            BigInteger p = TypeConverter.stringToBigInteger(encryptionParameters.getP());

            if (BigInteger.valueOf(2).equals(g)) {
                result.setStatus(Status.OK);
            } else {
                throw new Test10Exception("two", TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test10.nok.message"));
            }

            if (MathHelper.isEulerCriterionValid(g, p)) {
                result.setStatus(Status.OK);
            } else {
                throw new Test10Exception("euler", TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test10.euler.nok.message"));
            }

        } catch (Test10Exception e) {
            result.setStatus(Status.NOK);
            result.setMessage(e.getMsg());
            String detail = (e.getType().equals("two")) ? "g does not equal 2" : "g is not part of the subgroup q";
            LOGGER.debug(String.format("Block1 Test10 failed, because %s", detail));

        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test10.file.not.found.message"));
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }

    class Test10Exception extends RuntimeException {
        String type;
        Map<Language, String> msg;

        Test10Exception(String type, Map<Language, String> msg) {
            this.type = type;
            this.msg = msg;
        }

        public String getType() {
            return type;
        }

        public Map<Language, String> getMsg() {
            return msg;
        }

    }
}
