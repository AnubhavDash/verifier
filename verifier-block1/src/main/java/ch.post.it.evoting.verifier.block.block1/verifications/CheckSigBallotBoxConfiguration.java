package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.JsonMissingNodeException;
import ch.post.it.evoting.verifier.common.block.tools.path.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;

public class CheckSigBallotBoxConfiguration extends AbstractVerification {

    static final String PLATFORM_ROOT_CA_PEM = "platformRootCA.pem";
    static final String TENANT_100_PEM = "tenant_100.pem";
    static final String BALLOT_BOX_JSON = "ballotBox.json";
    static final String BALLOT_BOX_JSON_SIGN = "ballotBox.json.sign";

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

        // Top level directories.
        Path pathElection = inputDirectoryPath.resolve(Block1VerificationSuite.PATH_ELECTION_SETUP);
        Path pathCertificates = inputDirectoryPath.resolve(Block1VerificationSuite.PATH_CERTIFICATES);

        // Sub-level directories.
        Path pathAdminboard = pathCertificates.resolve(Block1VerificationSuite.PATH_ADMINBOARD);

        // Get the certificate used for signing.
        byte[] signingCertificate = Files.readAllBytes(PathHelper.getPath(pathAdminboard, 1, ".*\\.pem"));

        // Get the intermediate certificates.
        byte[][] intermediateCertificates = new byte[][]{Files.readAllBytes(PathHelper.getPath(pathCertificates, 1, TENANT_100_PEM))};

        // Get the root certificate.
        byte[] rootCertificate = Files.readAllBytes(PathHelper.getPath(pathCertificates, 1, PLATFORM_ROOT_CA_PEM));

        // Get the file and its signature file.
        final Path ballotBoxPath = PathHelper.getPath(pathElection, 1, BALLOT_BOX_JSON);
        final Path ballotBoxSignPath = PathHelper.getPath(pathElection, 1, BALLOT_BOX_JSON_SIGN);

        // Convert files to json nodes.
        ObjectMapper mapper = new ObjectMapper();
        final JsonNode signatureNode = mapper.readTree(Files.readString(ballotBoxSignPath));
        final JsonNode signedNode = mapper.readTree(Files.readString(ballotBoxPath));

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
                    ballotBoxPath.getFileName().toString() + "/" + BALLOT_BOX_JSON
            );
        }

        result.setStatus(Status.OK);
        return result;
    }
}
