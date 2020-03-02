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
package ch.post.it.evoting.verifier.block.block3.verifications;

import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

public class CheckSigDownloadedBallotBox extends AbstractVerification {

    static final String BALLOT_BOX_CERT_NODE = "ballotBoxCert";
    static final String SERVICES_CA_NODE = "servicesCA";
    static final String ELECTION_ROOT_CA_NODE = "electionRootCA";

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(3);
        def.setCategory(Category.AUTHENTICITY);
        def.setId(76);
        def.setName("checkSigDownloadedBallotBox");
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                "verification76.description"));
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        // Mapper to parse json files containing the certificates.
        ObjectMapper mapper = new ObjectMapper();

        // Get the certificate used for signing.
        PathNode ballotBoxJsonPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX, inputDirectoryPath);
        final JsonNode ballotBoxJsonNode = mapper.readTree(Files.readAllBytes(ballotBoxJsonPathNode.getPath()));
        final JsonNode ballotBoxCertJsonNode = ballotBoxJsonNode.path(BALLOT_BOX_CERT_NODE);
        if (ballotBoxCertJsonNode.isMissingNode()) {
            throw new RuntimeException(String.format("%s certificate is missing!", BALLOT_BOX_CERT_NODE));
        }
        final byte[] signingCertificate = ballotBoxCertJsonNode.asText().getBytes(StandardCharsets.UTF_8);

        // Get the intermediate certificates.
        PathNode electionInfoPathNode = pathService.buildFromRootPath(StructureKey.ELECTION_INFORMATION_CONTENTS, inputDirectoryPath);
        final JsonNode electionInfoJsonNode = mapper.readTree(Files.readAllBytes(electionInfoPathNode.getPath()));
        final JsonNode servicesCANode = electionInfoJsonNode.path(SERVICES_CA_NODE);
        if (servicesCANode.isMissingNode()) {
            throw new RuntimeException(String.format("%s certificate is missing!", SERVICES_CA_NODE));
        }
        final byte[][] intermediateCertificates = new byte[][]{servicesCANode.asText().getBytes(StandardCharsets.UTF_8)};

        // Get the root certificate.
        final JsonNode electionRootCANode = electionInfoJsonNode.path(ELECTION_ROOT_CA_NODE);
        if (electionRootCANode.isMissingNode()) {
            throw new RuntimeException(String.format("%s certificate is missing!", ELECTION_ROOT_CA_NODE));
        }
        final byte[] rootCertificate = electionRootCANode.asText().getBytes(StandardCharsets.UTF_8);

        // Deserialize the ballot boxes to iterate over the ballot box directory.
        PathNode ballotBoxIdDirectoriesPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
        for (Path ballotBoxIdDirectoryPath : ballotBoxIdDirectoriesPathNode.getRegexPaths()) {

            // Get the downloaded ballot box file.
            PathNode downloadedBallotBoxPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.DOWNLOADED_BALLOT_BOX, ballotBoxIdDirectoryPath);

            // Extract and decode the signature.
            final List<String> lines = Files.readAllLines(downloadedBallotBoxPathNode.getPath());
            byte[] signatureBase64 = lines.remove(lines.size() - 1).getBytes(StandardCharsets.UTF_8);
            byte[] signature = Base64.getDecoder().decode(signatureBase64);

            // Convert back the content without the signature.
            final String sourceString = String.join("\n", lines);
            final byte[] source = sourceString.getBytes(StandardCharsets.UTF_8);

            if (!SignatureChecker.verifySignature(source, signature, signingCertificate, intermediateCertificates, rootCertificate)) {
                throw buildVerificationFailureException(
                        "The signature verification of the file failed",
                        Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification76.nok.message",
                        ballotBoxIdDirectoryPath.getFileName().toString() + "/" + downloadedBallotBoxPathNode.getPath().getFileName().toString()
                );
            }


        }

        result.setStatus(Status.OK);
        return result;
    }

}
