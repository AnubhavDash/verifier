package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.JsonMissingNodeException;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;

public class CheckSigBallotBoxConfiguration extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                "verification80.description"));
        def.setId(80);
        def.setName("checkSigBallotBoxConfiguration");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        // Get the certificate used for signing.
        final PathNode adminBoardCertpathNode = pathService.buildFromRootPath(StructureKey.ADMIN_BOARD_CERT, inputDirectoryPath);
        byte[] signingCertificate = Files.readAllBytes(adminBoardCertpathNode.getPath());

        // Get the intermediate certificates.
        final PathNode tenantPathNode = pathService.buildFromRootPath(StructureKey.TENANT_100, inputDirectoryPath);
        byte[][] intermediateCertificates = new byte[][]{Files.readAllBytes(tenantPathNode.getPath())};

        // Get the root certificate.
        final PathNode platformRootPathNode = pathService.buildFromRootPath(StructureKey.PLATFORM_ROOT_CA, inputDirectoryPath);
        byte[] rootCertificate = Files.readAllBytes(platformRootPathNode.getPath());

        // Get all the ballot box id directories and iterate over them.
        final PathNode ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
        for (Path ballotBoxIdDirectoryPath : ballotIdsPathNode.getRegexPaths()) {
            // Get ballotBox.
            final PathNode ballotBoxPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX, ballotBoxIdDirectoryPath);

            // Convert file to json nodes.
            ObjectMapper mapper = new ObjectMapper();
            final JsonNode signedNode = mapper.readTree(Files.readString(ballotBoxPathNode.getPath()));
            final JsonNode signatureNode = mapper.readTree(Files.readString(ballotBoxPathNode.getRelation(RelationType.SIGN)));

            // Extract signature.
            final JsonNode signature = signatureNode.path("signature");
            if (signature.isMissingNode()) {
                throw new JsonMissingNodeException("The signature is missing from the file!");
            }

            // Verify signature.
            if (!SignatureChecker.verifyJsonSignature(signedNode, signature, signingCertificate, intermediateCertificates
                    , rootCertificate)) {
                throw buildVerificationFailureException(
                        "The signature verification of the file failed",
                        Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification80.nok.message",
                        ballotBoxPathNode.getPath().toString()
                );
            }
        }

        result.setStatus(Status.OK);
        return result;
    }
}
