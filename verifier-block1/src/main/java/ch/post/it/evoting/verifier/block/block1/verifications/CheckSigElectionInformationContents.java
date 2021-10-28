/*
 * Copyright 2021 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.block.block1.verifications;

import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
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
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class CheckSigElectionInformationContents extends AbstractVerification {

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

		// Get the certificate used for signing.
		final PathNode adminCertPathNode = pathService.buildFromRootPath(StructureKey.ADMIN_BOARD_CERT, inputDirectoryPath);
		byte[] signingCertificate = Files.readAllBytes(adminCertPathNode.getPath());

		// Get the intermediate certificates.
		final PathNode tenantPathNode = pathService.buildFromRootPath(StructureKey.TENANT_100, inputDirectoryPath);
		byte[][] intermediateCertificates = new byte[][] { Files.readAllBytes(tenantPathNode.getPath()) };

		// Get the root certificate.
		final PathNode platformRootPathNode = pathService.buildFromRootPath(StructureKey.PLATFORM_ROOT_CA, inputDirectoryPath);
		byte[] rootCertificate = Files.readAllBytes(platformRootPathNode.getPath());

		// Get the file path.
		final PathNode electionInfoPathNode = pathService.buildFromRootPath(StructureKey.ELECTION_INFORMATION_CONTENTS, inputDirectoryPath);

		// Convert files to json nodes.
		ObjectMapper mapper = new ObjectMapper();
		final JsonNode signedNode = mapper.readTree(Files.readString(electionInfoPathNode.getPath()));
		final JsonNode signatureNode = mapper.readTree(Files.readString(electionInfoPathNode.getRelation(RelationType.SIGN)));

		// Extract signature.
		final JsonNode signature = signatureNode.path("signature");
		if (signature.isMissingNode()) {
			throw new JsonMissingNodeException("The signature is missing from the file!");
		}

		// Verify signature.
		if (!SignatureChecker.verifyJsonSignature(signedNode, signature, signingCertificate, intermediateCertificates, rootCertificate)) {
			throw buildVerificationFailureException(
					"The signature verification of the file failed",
					Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification81.nok.message",
					electionInfoPathNode.getPath().toString()
			);
		}

		result.setStatus(Status.OK);
		return result;
	}
}
