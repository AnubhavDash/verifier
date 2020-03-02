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
import java.util.Base64;

public class CheckSigCodesMappingTablesContextData extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                "verification77.description"));
        def.setId(77);
        def.setName("checkSigCodesMappingTablesContextData");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        // Get the certificate used for signing.
        final PathNode adminBoardCertPathNode = pathService.buildFromRootPath(StructureKey.ADMIN_BOARD_CERT, inputDirectoryPath);
        byte[] signingCertificate = Files.readAllBytes(adminBoardCertPathNode.getPath());

        // Get the intermediate certificates.
        final PathNode tenantPathNode = pathService.buildFromRootPath(StructureKey.TENANT_100, inputDirectoryPath);
        byte[][] intermediateCertificates = new byte[][]{Files.readAllBytes(tenantPathNode.getPath())};

        // Get the root certificate.
        final PathNode platformRootPathNode = pathService.buildFromRootPath(StructureKey.PLATFORM_ROOT_CA, inputDirectoryPath);
        byte[] rootCertificate = Files.readAllBytes(platformRootPathNode.getPath());

        // Get directory where files to check signature are located.
        final PathNode verifCardSetIdPathNode = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

        for (Path regexPath : verifCardSetIdPathNode.getRegexPaths()) {
            final PathNode pathNode = pathService.buildFromDynamicAncestorPath(StructureKey.CODES_MAPPING_TABLES_CONTEXT_DATA, regexPath);

            // Get and decode the signature.
            byte[] signatureBase64 = Files.readAllBytes(pathNode.getRelation(RelationType.SIGN));
            byte[] signature = Base64.getDecoder().decode(signatureBase64);

            byte[] source = Files.readAllBytes(pathNode.getPath());

            if (!SignatureChecker.verifySignature(source, signature, signingCertificate, intermediateCertificates,
                    rootCertificate)) {
                throw buildVerificationFailureException(
                        "The signature verification of the file failed",
                        Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification77.nok.message",
                        regexPath.toString()
                );
            }
        }

        result.setStatus(Status.OK);
        return result;
    }
}
