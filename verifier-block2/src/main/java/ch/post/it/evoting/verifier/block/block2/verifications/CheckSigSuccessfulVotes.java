/*
 * Copyright 2022 Post CH Ltd
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
package ch.post.it.evoting.verifier.block.block2.verifications;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.plugin.contract.AbstractVerification;
import ch.post.it.evoting.verifier.plugin.contract.Category;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.plugin.contract.VerificationTrait;
import ch.post.it.evoting.verifier.core.internal.exceptions.VerificationPreconditionException;
import ch.post.it.evoting.verifier.core.internal.tools.SignatureChecker;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathNode;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathService;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerifierEvent;

@Component
public class CheckSigSuccessfulVotes extends AbstractVerification {

	static final String BALLOT_BOX_CERT = "ballotBoxCert";
	static final String SERVICES_CA = "servicesCA";
	static final String ELECTION_ROOT_CA = "electionRootCA";

	private static final String CERTIFICATE_IS_MISSING = "%s certificate is missing!";

	private final PathService pathService;

	public CheckSigSuccessfulVotes(final PathService pathService, final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(2);
		definition.setCategory(Category.AUTHENTICITY);
		definition.setId(71);
		definition.setName("checkSigDownloadedBallotBox");
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "verification71.description"));
		definition.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		// Mapper to parse json files containing the certificates.
		final var mapper = new ObjectMapper();

		// Get the intermediate certificates.
		final var electionInfoPathNode = pathService.buildFromRootPath(StructureKey.ELECTION_INFORMATION_CONTENTS, inputDirectoryPath);
		final JsonNode electionInfoNode;
		try {
			electionInfoNode = mapper.readTree(Files.readAllBytes(electionInfoPathNode.getPath()));
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize election information contents.", e);
		}
		final JsonNode servicesCANode = electionInfoNode.path(SERVICES_CA);
		if (servicesCANode.isMissingNode()) {
			throw new VerificationPreconditionException(String.format(CERTIFICATE_IS_MISSING, SERVICES_CA));
		}
		final var intermediateCertificates = new byte[][] { servicesCANode.asText().getBytes(StandardCharsets.UTF_8) };

		// Get the root certificate.
		final JsonNode electionRootCANode = electionInfoNode.path(ELECTION_ROOT_CA);
		if (electionRootCANode.isMissingNode()) {
			throw new VerificationPreconditionException(String.format(CERTIFICATE_IS_MISSING, ELECTION_ROOT_CA));
		}
		final byte[] rootCertificate = electionRootCANode.asText().getBytes(StandardCharsets.UTF_8);

		// Get all the ballot box id directories and iterate over them.
		final var ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
		for (Path ballotBoxIdDirectoryPath : ballotIdsPathNode.getRegexPaths()) {
			// Get the certificate used for signing.
			final PathNode ballotPathNode;
			final JsonNode ballotBoxNode;
			try {
				ballotPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX, ballotBoxIdDirectoryPath);
				ballotBoxNode = mapper.readTree(Files.readAllBytes(ballotPathNode.getPath()));
			} catch (IOException e) {
				throw new UncheckedIOException("Failed to read ballot box.", e);
			}
			final JsonNode ballotBoxCertNode = ballotBoxNode.path(BALLOT_BOX_CERT);
			if (ballotBoxCertNode.isMissingNode()) {
				throw new VerificationPreconditionException(String.format(CERTIFICATE_IS_MISSING, BALLOT_BOX_CERT));
			}
			final byte[] signingCertificate = ballotBoxCertNode.asText().getBytes(StandardCharsets.UTF_8);

			// Extract and decode the signature.
			final PathNode successVotesPathNode;
			final List<String> lines;
			try {
				successVotesPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.SUCCESSFUL_VOTES, ballotBoxIdDirectoryPath);
				lines = Files.readAllLines(successVotesPathNode.getPath());
			} catch (IOException e) {
				throw new UncheckedIOException("Failed to read signature.", e);
			}
			byte[] signatureBase64 = lines.remove(lines.size() - 1).getBytes(StandardCharsets.UTF_8);
			byte[] signature = Base64.getDecoder().decode(signatureBase64);

			// Convert back the content without the signature.
			final var sourceString = String.join("\n", lines);
			final byte[] source = sourceString.getBytes(StandardCharsets.UTF_8);

			if (!SignatureChecker.verifySignature(source, signature, signingCertificate, intermediateCertificates, rootCertificate)) {
				return VerificationResultEvent.failure(this, getVerificationDefinition(),
						TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "verification71.nok.message"));
			}
		}

		return VerificationResultEvent.success(this, getVerificationDefinition());
	}
}
