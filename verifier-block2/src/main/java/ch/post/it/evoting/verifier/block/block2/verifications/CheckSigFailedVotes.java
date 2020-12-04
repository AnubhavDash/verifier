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

public class CheckSigFailedVotes extends AbstractVerification {

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

        // Mapper to parse json files containing the certificates.
        ObjectMapper mapper = new ObjectMapper();

        // Get the certificate used for signing.
        final PathNode ballotPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX, inputDirectoryPath);
        final JsonNode ballotBoxNode = mapper.readTree(Files.readAllBytes(ballotPathNode.getPath()));
        final byte[] signingCertificate = extractSigningCertificate(ballotBoxNode);

        // Build election node where certificates are.
        final PathNode electionInfoPathNode = pathService.buildFromRootPath(StructureKey.ELECTION_INFORMATION_CONTENTS, inputDirectoryPath);
        final JsonNode electionInfoNode = mapper.readTree(Files.readAllBytes(electionInfoPathNode.getPath()));

        // Get the intermediate certificates.
        final byte[][] intermediateCertificates = extractIntermediateCertificates(electionInfoNode);

        // Get the root certificate.
        final byte[] rootCertificate = extractRootCertificate(electionInfoNode);

        // Get all the ballot box id directories and iterate over them. // TODO Need to validate folders against election event file.
        final PathNode ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
        for (Path regexPath : ballotIdsPathNode.getRegexPaths()) {
            final PathNode failedVotesPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.FAILED_VOTES, regexPath);

            // Extract and decode the signature.
            final List<String> lines = Files.readAllLines(failedVotesPathNode.getPath());
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
                        regexPath.toString()
                );
            }
        }

        result.setStatus(Status.OK);
        return result;
    }

    private byte[] extractSigningCertificate(JsonNode ballotBoxNode) {
        final JsonNode ballotBoxCertNode = ballotBoxNode.path(BALLOT_BOX_CERT);
        if (ballotBoxCertNode.isMissingNode()) {
            throw new JsonMissingNodeException(String.format("%s certificate is missing!", BALLOT_BOX_CERT));
        }
        return ballotBoxCertNode.asText().getBytes(StandardCharsets.UTF_8);
    }

    private byte[][] extractIntermediateCertificates(JsonNode electionInfoNode) {
        final JsonNode servicesCANode = electionInfoNode.path(SERVICES_CA);
        if (servicesCANode.isMissingNode()) {
            throw new JsonMissingNodeException(String.format("%s certificate is missing!", SERVICES_CA));
        }
        return new byte[][]{servicesCANode.asText().getBytes(StandardCharsets.UTF_8)};
    }

    private byte[] extractRootCertificate(JsonNode electionInfoNode) {
        final JsonNode electionRootCANode = electionInfoNode.path(ELECTION_ROOT_CA);
        if (electionRootCANode.isMissingNode()) {
            throw new JsonMissingNodeException(String.format("%s certificate is missing!", ELECTION_ROOT_CA));
        }
        return electionRootCANode.asText().getBytes(StandardCharsets.UTF_8);
    }
}
