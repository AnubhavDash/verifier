/*
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
import ch.post.it.evoting.verifier.common.block.VerificationFailureConsumer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
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
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
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
                    .forEach((VerificationFailureConsumer<Path>) d -> {
                        byte[] signatureBase64 = Files.readAllBytes(d.resolve(CREDENTIAL_DATA_CSV_SIGN));
                        // Decode the signature.
                        byte[] signature = Base64.getDecoder().decode(signatureBase64);
                        byte[] source = Files.readAllBytes(d.resolve(CREDENTIAL_DATA_CSV));

                        if (!SignatureChecker.verifySignature(source, signature, signingCertificate, intermediateCertificates,
                                rootCertificate)) {
                            throw buildVerificationFailureException(
                                    "The signature verification of the file failed",
                                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                                    "verification78.nok.message",
                                    d.getFileName().toString() + "/" + CREDENTIAL_DATA_CSV
                            );
                        }
                    });
        }

        result.setStatus(Status.OK);
        return result;
    }

}
