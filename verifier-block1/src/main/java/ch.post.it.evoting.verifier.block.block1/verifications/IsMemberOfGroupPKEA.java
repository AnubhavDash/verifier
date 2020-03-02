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
package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.dto.ElectoralAuthority;
import ch.post.it.evoting.verifier.dto.PublicKey;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class IsMemberOfGroupPKEA extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                "verification07.description"));
        def.setId(7);
        def.setName("isMemberOfGroup(pk_ea)");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        final PathNode electoralAuthPathNode = pathService.buildFromRootPath(StructureKey.ELECTORAL_AUTHORITY, inputDirectoryPath);
        ElectoralAuthority electoralAuthority = Deserializer.fromJson(electoralAuthPathNode.getPath(), ElectoralAuthority.class);

        String publicKeyB64 = electoralAuthority.getPublicKey();
        byte[] decoded = TypeConverter.base64ToByte(publicKeyB64);
        String publicKey = TypeConverter.byteToString(decoded);

        BigInteger p = extractPFromPublicKey(publicKey);
        List<String> elements = extractElementsFromPublicKey(publicKey);
        if (elements.isEmpty()) {
            throw buildVerificationFailureException(
                    "No such Elements was found in the publicKey",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification07.nok.message.no.elements"
            );
        } else {
            List<String> errors = elements.stream()
                    .map(element -> TypeConverter.byteToBigInteger(TypeConverter.base64ToByte(element)))
                    .filter(bigInteger -> !MathHelper.isEulerCriterionValid(bigInteger, p))
                    .map(TypeConverter::bigIntegerToB64String)
                    .collect(Collectors.toList());

            if (!errors.isEmpty()) {
                throw buildVerificationFailureException(
                        "Euler criterion does not equal to 1",
                        Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification07.nok.message",
                        errors.toString()
                );
            }
        }

        result.setStatus(Status.OK);
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
