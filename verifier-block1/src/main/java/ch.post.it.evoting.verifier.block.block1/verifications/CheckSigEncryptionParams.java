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
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

import java.nio.file.Files;
import java.nio.file.Path;

public class CheckSigEncryptionParams extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                "verification76.description"));
        def.setId(76);
        def.setName("checkSigEncryptionParams");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        final PathNode integrationPathNode = pathService.buildPathNode(StructureKey.INTEGRATION_CA, inputDirectoryPath);
        byte[] rootCertificate = Files.readAllBytes(integrationPathNode.getPath());

        final PathNode encryptParamsPathNode = pathService.buildPathNode(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);

        byte[] content = Files.readAllBytes(encryptParamsPathNode.getPath());
        byte[] signature = Files.readAllBytes(encryptParamsPathNode.getRelation(RelationType.P7));

        if (!SignatureChecker.verifyPKCS7(content, signature, rootCertificate)) {
            throw buildVerificationFailureException(
                    "The signature verification of the file encryptionParameters.json failed",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification76.nok.message",
                    encryptParamsPathNode.getPath().toString()
            );
        }

        result.setStatus(Status.OK);
        return result;
    }
}
