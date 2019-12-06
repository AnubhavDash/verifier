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
package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.ElectoralAuthority;
import ch.post.it.evoting.verifier.dto.PublicKey;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class IsMemberOfGroupPKEA extends AbstractVerification {

    private static final Logger LOGGER = Logger.getLogger(IsMemberOfGroupPKEA.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification07.description"));
        def.setId(7);
        def.setName("isMemberOfGroup(pk_ea)");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        try {
            Path path = inputDirectory.toPath().resolve(Block1VerificationSuite.PATH_CRYPTO_SETUP);
            ElectoralAuthority electoralAuthority = Deserializer.fromJson(path.toFile(), "electoralAuthority\\.json", ElectoralAuthority.class);
            String publicKeyB64 = electoralAuthority.getPublicKey();
            byte[] decoded = TypeConverter.base64ToByte(publicKeyB64);
            String publicKey = TypeConverter.byteToString(decoded);

            BigInteger p = extractPFromPublicKey(publicKey);
            List<String> elements = extractElementsFromPublicKey(publicKey);
            if (elements.isEmpty()) {
                throw new VerificationFailureException("No such Elements was found in the publicKey");
            } else {
                List<String> errors = elements.stream()
                        .map(element -> TypeConverter.byteToBigInteger(TypeConverter.base64ToByte(element)))
                        .filter(bigInteger -> !MathHelper.isEulerCriterionValid(bigInteger, p))
                        .map(bi -> TypeConverter.byteToBase64String(TypeConverter.bigIntegerToByte(bi)))
                        .collect(Collectors.toList());
                if (errors.isEmpty()) {
                    result.setStatus(Status.OK);
                } else {
                    result.setStatus(Status.NOK);
                    result.setMessage(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification07.nok.message", errors.toString()));
                }
            }
        } catch (VerificationFailureException e) {
            LOGGER.error("Error while extracting elements from publicKey", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification07.file.not.found.message"));
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }

    private BigInteger extractPFromPublicKey(String publicKey) throws IOException {

        PublicKey pk = Deserializer.fromJson(TypeConverter.stringToByte(publicKey), PublicKey.class);
        return TypeConverter.base64ToBigInteger(pk.getPublicKey().getZpSubgroup().getP());
    }

    private List<String> extractElementsFromPublicKey(String publicKey) throws IOException {
        PublicKey pk = Deserializer.fromJson(TypeConverter.stringToByte(publicKey), PublicKey.class);
        return (pk.getPublicKey().getElements());
    }
}
