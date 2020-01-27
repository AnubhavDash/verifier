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
package ch.post.it.evoting.verifier.block.block2.verifications;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.JsonMissingNodeException;
import ch.post.it.evoting.verifier.common.block.dto.revised.BallotBox;
import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.common.block.tools.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

public class CheckSigFailedVotes extends AbstractVerification {
    static final String FAILED_VOTES_CSV = "failedVotes.csv";
    static final String BALLOT_BOX_JSON = "ballotBox.json";
    static final String ELECTION_INFORMATION_CONTENTS_JSON = "electionInformationContents.json";
    static final String BALLOT_BOX_CERT = "ballotBoxCert";
    static final String SERVICES_CA = "servicesCA";
    static final String ELECTION_ROOT_CA = "electionRootCA";

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(2);
        def.setCategory(Category.AUTHENTICITY);
        def.setId(72);
        def.setName("checkSigFailedVotes");
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
                "verification72.description"));
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        // Top level directories.
        Path pathElection = inputDirectoryPath.resolve(Block2VerificationSuite.PATH_ELECTION_SETUP);

        // Mapper to parse json files containing the certificates.
        ObjectMapper mapper = new ObjectMapper();

        // Get the certificate used for signing.
        final JsonNode ballotBoxNode = mapper.readTree(Files.readAllBytes(PathHelper.getPath(pathElection, 1, BALLOT_BOX_JSON)));
        final JsonNode ballotBoxCertNode = ballotBoxNode.path(BALLOT_BOX_CERT);
        if (ballotBoxCertNode.isMissingNode()) {
            throw new JsonMissingNodeException(String.format("%s certificate is missing!", BALLOT_BOX_CERT));
        }
        final byte[] signingCertificate = ballotBoxCertNode.asText().getBytes(StandardCharsets.UTF_8);

        // Get the intermediate certificates.
        final JsonNode electionInfoNode = mapper.readTree(Files.readAllBytes(PathHelper.getPath(pathElection, 1,
                ELECTION_INFORMATION_CONTENTS_JSON)));
        final JsonNode servicesCANode = electionInfoNode.path(SERVICES_CA);
        if (servicesCANode.isMissingNode()) {
            throw new JsonMissingNodeException(String.format("%s certificate is missing!", SERVICES_CA));
        }
        final byte[][] intermediateCertificates = new byte[][]{servicesCANode.asText().getBytes(StandardCharsets.UTF_8)};

        // Get the root certificate.
        final JsonNode electionRootCANode = electionInfoNode.path(ELECTION_ROOT_CA);
        if (electionRootCANode.isMissingNode()) {
            throw new JsonMissingNodeException(String.format("%s certificate is missing!", ELECTION_ROOT_CA));
        }
        final byte[] rootCertificate = electionRootCANode.asText().getBytes(StandardCharsets.UTF_8);

        // Deserialize the ballot boxes to iterate over the ballot box directory.
        ElectionEvent electionEvent = Deserializer.fromJson(pathElection.toFile(), "dataConfig_updated_.*\\.json", ElectionEvent.class);
        List<BallotBox> ballotBoxes = electionEvent.getBallotBoxes();

        for (BallotBox ballotBox : ballotBoxes) {
            String ballotBoxId = TypeConverter.UUIDToStringWithoutDash(ballotBox.getId());

            // Find the ballot box file.
            final Path ballotDirectoryPath = inputDirectoryPath.resolve(Block2VerificationSuite.PATH_BALLOTBOXES).resolve(ballotBoxId);
            final Path filePath = PathHelper.getPath(ballotDirectoryPath, 1, FAILED_VOTES_CSV);

            // Extract and decode the signature.
            final List<String> lines = Files.readAllLines(filePath);
            byte[] signatureBase64 = lines.remove(lines.size() - 1).getBytes(StandardCharsets.UTF_8);
            byte[] signature = Base64.getDecoder().decode(signatureBase64);

            // Convert back the content without the signature.
            final String sourceString = String.join("\n", lines);
            final byte[] source = sourceString.getBytes(StandardCharsets.UTF_8);

            if (!SignatureChecker.verifySignature(source, signature, signingCertificate, intermediateCertificates, rootCertificate)) {
                throw buildVerificationFailureException(
                        "The signature verification of the file failed",
                        Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification72.nok.message",
                        ballotDirectoryPath.getFileName().toString() + "/" + filePath.getFileName().toString()
                );
            }
        }

        result.setStatus(Status.OK);
        return result;
    }
}
