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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.exceptions.JsonMissingNodeException;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class CheckSigSuccessfulVotes extends AbstractVerification {

	static final String BALLOT_BOX_CERT = "ballotBoxCert";
	static final String SERVICES_CA = "servicesCA";
	static final String ELECTION_ROOT_CA = "electionRootCA";

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(2);
		def.setCategory(Category.AUTHENTICITY);
		def.setId(71);
		def.setName("checkSigDownloadedBallotBox");
		def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification71.description"));
		def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		// Mapper to parse json files containing the certificates.
		ObjectMapper mapper = new ObjectMapper();

		// Get the intermediate certificates.
		final PathNode electionInfoPathNode = pathService.buildFromRootPath(StructureKey.ELECTION_INFORMATION_CONTENTS, inputDirectoryPath);
		final JsonNode electionInfoNode = mapper.readTree(Files.readAllBytes(electionInfoPathNode.getPath()));
		final JsonNode servicesCANode = electionInfoNode.path(SERVICES_CA);
		if (servicesCANode.isMissingNode()) {
			throw new JsonMissingNodeException(String.format("%s certificate is missing!", SERVICES_CA));
		}
		final byte[][] intermediateCertificates = new byte[][] { servicesCANode.asText().getBytes(StandardCharsets.UTF_8) };

		// Get the root certificate.
		final JsonNode electionRootCANode = electionInfoNode.path(ELECTION_ROOT_CA);
		if (electionRootCANode.isMissingNode()) {
			throw new JsonMissingNodeException(String.format("%s certificate is missing!", ELECTION_ROOT_CA));
		}
		final byte[] rootCertificate = electionRootCANode.asText().getBytes(StandardCharsets.UTF_8);

		// Get all the ballot box id directories and iterate over them.
		final PathNode ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
		for (Path ballotBoxIdDirectoryPath : ballotIdsPathNode.getRegexPaths()) {

			// Get the certificate used for signing.
			final PathNode ballotPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX, ballotBoxIdDirectoryPath);
			final JsonNode ballotBoxNode = mapper.readTree(Files.readAllBytes(ballotPathNode.getPath()));
			final JsonNode ballotBoxCertNode = ballotBoxNode.path(BALLOT_BOX_CERT);
			if (ballotBoxCertNode.isMissingNode()) {
				throw new JsonMissingNodeException(String.format("%s certificate is missing!", BALLOT_BOX_CERT));
			}
			final byte[] signingCertificate = ballotBoxCertNode.asText().getBytes(StandardCharsets.UTF_8);

			final PathNode successVotesPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.SUCCESSFUL_VOTES, ballotBoxIdDirectoryPath);

			// Extract and decode the signature.
			final List<String> lines = Files.readAllLines(successVotesPathNode.getPath());
			byte[] signatureBase64 = lines.remove(lines.size() - 1).getBytes(StandardCharsets.UTF_8);
			byte[] signature = Base64.getDecoder().decode(signatureBase64);

			// Convert back the content without the signature.
			final String sourceString = String.join("\n", lines);
			final byte[] source = sourceString.getBytes(StandardCharsets.UTF_8);

			if (!SignatureChecker.verifySignature(source, signature, signingCertificate, intermediateCertificates, rootCertificate)) {
				throw buildVerificationFailureException(
						"The signature verification of the file failed",
						Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification71.nok.message",
						ballotBoxIdDirectoryPath.toString()
				);
			}
		}

		result.setStatus(Status.OK);
		return result;
	}
}
