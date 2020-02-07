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

public class CheckSigElectionInformationContents extends AbstractVerification {

    static final String PLATFORM_ROOT_CA_PEM = "platformRootCA.pem";
    static final String TENANT_100_PEM = "tenant_100.pem";
    static final String ELECTION_INFORMATION_CONTENTS_JSON = "electionInformationContents.json";
    static final String ELECTION_INFORMATION_CONTENTS_JSON_SIGN = "electionInformationContents.json.sign";

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                "verification81.description"));
        def.setId(81);
        def.setName("checkSigElectionInformationContents");
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
        final Path electionInformationContentsPath = PathHelper.getPath(pathElection, 1, ELECTION_INFORMATION_CONTENTS_JSON);
        final Path electionInformationContentsSignPath = PathHelper.getPath(pathElection, 1, ELECTION_INFORMATION_CONTENTS_JSON_SIGN);

        // Convert files to json nodes.
        ObjectMapper mapper = new ObjectMapper();
        final JsonNode signatureNode = mapper.readTree(Files.readString(electionInformationContentsSignPath));
        final JsonNode signedNode = mapper.readTree(Files.readString(electionInformationContentsPath));

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
                    "verification81.nok.message",
                    electionInformationContentsPath.getFileName().toString() + "/" + ELECTION_INFORMATION_CONTENTS_JSON
            );
        }

        result.setStatus(Status.OK);
        return result;
    }
}
