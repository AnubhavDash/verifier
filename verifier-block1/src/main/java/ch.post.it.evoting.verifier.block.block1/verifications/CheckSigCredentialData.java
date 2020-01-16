package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class CheckSigCredentialData extends AbstractVerification {

    private static final String CREDENTIAL_DATA_CSV_SIGN = "credentialData.csv.sign";
    private static final String CREDENTIAL_DATA_CSV = "credentialData.csv";
    private static final String PLATFORM_ROOT_CA_PEM = "platformRootCA.pem";
    private static final String TENANT_100_PEM = "tenant_100.pem";

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                "verification78.description"));
        def.setId(78);
        def.setName("checkSigCredentialData");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    protected VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        // Top level directories.
        Path pathElection = inputDirectoryPath.resolve(Block1VerificationSuite.PATH_ELECTION_SETUP);
        Path pathCertificates = inputDirectoryPath.resolve(Block1VerificationSuite.PATH_CERTIFICATES);

        // Sub-level directories.
        Path pathVotingCardSets = pathElection.resolve(Block1VerificationSuite.PATH_VOTING_CARD_SETS);
        Path pathAdminboard = pathCertificates.resolve(Block1VerificationSuite.PATH_ADMINBOARD);

        // Get the certificate used for signing.
        byte[] signingCertificate = Files.readAllBytes(PathHelper.getPath(pathAdminboard, 1, ".*\\.pem"));

        // Get the intermediate certificates.
        byte[][] intermediateCertificates = new byte[][]{Files.readAllBytes(PathHelper.getPath(pathCertificates, 1, TENANT_100_PEM))};

        // Get the root certificate.
        byte[] rootCertificate = Files.readAllBytes(PathHelper.getPath(pathCertificates, 1, PLATFORM_ROOT_CA_PEM));

        // Iterate over all directories and do the verification for credentialData in each.
        try (Stream<Path> stream = Files.walk(pathVotingCardSets, 1)) {
            stream.filter(p -> Files.isDirectory(p) && !p.equals(pathVotingCardSets))
                    .forEach(d -> {
                        try {
                            byte[] signature = Files.readAllBytes(d.resolve(CREDENTIAL_DATA_CSV_SIGN));
                            byte[] source = Files.readAllBytes(d.resolve(CREDENTIAL_DATA_CSV));
                            if (!SignatureChecker.verifySignSignature(source, signature, signingCertificate, intermediateCertificates,
                                    rootCertificate)) {
                                throw buildVerificationFailureException(
                                        "The signature verification of the file failed",
                                        Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                                        "verification78.nok.message",
                                        d.getFileName().toString() + "/" + CREDENTIAL_DATA_CSV
                                );
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        result.setStatus(Status.OK);
        return result;
    }
}
