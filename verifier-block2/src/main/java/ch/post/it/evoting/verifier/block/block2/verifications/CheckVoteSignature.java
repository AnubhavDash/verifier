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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.plugin.contract.AbstractVerification;
import ch.post.it.evoting.verifier.plugin.contract.Category;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.plugin.contract.VerificationTrait;
import ch.post.it.evoting.verifier.core.internal.exceptions.JsonMissingNodeException;
import ch.post.it.evoting.verifier.core.internal.serialization.DownloadedBallotSerialization;
import ch.post.it.evoting.verifier.core.internal.tools.SignatureChecker;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathService;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerifierEvent;
import ch.post.it.evoting.verifier.dto.Vote__1;

@Component
public class CheckVoteSignature extends AbstractVerification {

	static final String CREDENTIALS_CA = "credentialsCA";
	static final String ELECTION_ROOT_CA = "electionRootCA";

	private final PathService pathService;

	public CheckVoteSignature(final PathService pathService, final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(2);
		definition.setCategory(Category.AUTHENTICITY);
		definition.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification73.description"));
		definition.setId(73);
		definition.setName("checkVoteSignature");
		definition.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		// Mapper to parse json files containing the certificates.
		final var mapper = new ObjectMapper();

		// Build election node where certificates are.
		final var electionInfoPathNode = pathService.buildFromRootPath(StructureKey.ELECTION_INFORMATION_CONTENTS, inputDirectoryPath);
		final JsonNode electionInfoNode;
		try {
			electionInfoNode = mapper.readTree(Files.readAllBytes(electionInfoPathNode.getPath()));
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read election information contents.", e);
		}

		// Get the intermediate certificates.
		final byte[][] intermediateCertificates = extractIntermediateCertificates(electionInfoNode);

		// Get the root certificate.
		final byte[] rootCertificate = extractRootCertificate(electionInfoNode);

		// Get all the ballot box id directories and iterate over them.
		final var ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
		for (Path regexPath : ballotIdsPathNode.getRegexPaths()) {
			// Get the downloadedBallotBox file path.
			final var downloadedBallotPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.DOWNLOADED_BALLOT_BOX, regexPath);

			try (final Stream<String> lines = Files.lines(downloadedBallotPathNode.getPath())) {
				final List<Boolean> invalidSignatures = lines.parallel()
						.map(DownloadedBallotSerialization::deserializeDownloadedBallot)
						// Remove empty lines, signature etc...
						.filter(Objects::nonNull)
						.map(ballot -> {
							// Extract information and its signature.
							final byte[] signature = Base64.getDecoder().decode(ballot.getVote().getSignature().getBytes(StandardCharsets.UTF_8));
							final byte[] signedInformation = buildSignedInformation(ballot.getVote());

							// Extract the certificate used to sign.
							final byte[] signingCertificate = ballot.getVote().getCertificate().getBytes(StandardCharsets.UTF_8);

							// Check signature.
							return SignatureChecker.verifySignature(signedInformation, signature, signingCertificate, intermediateCertificates,
									rootCertificate);
						})
						.filter(b -> !b) // Keep only invalid signatures.
						.collect(Collectors.toList());

				if (!invalidSignatures.isEmpty()) {
					return VerificationResultEvent.failure(this, getVerificationDefinition(),
							TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "verification73.nok.message"));
				}
			} catch (IOException e) {
				throw new UncheckedIOException("Failed to read downloaded ballot.", e);
			}
		}

		return VerificationResultEvent.success(this, getVerificationDefinition());
	}

	private byte[][] extractIntermediateCertificates(JsonNode electionInfoNode) {
		final JsonNode servicesCANode = electionInfoNode.path(CREDENTIALS_CA);
		if (servicesCANode.isMissingNode()) {
			throw new JsonMissingNodeException(String.format("%s certificate is missing!", CREDENTIALS_CA));
		}
		return new byte[][] { servicesCANode.asText().getBytes(StandardCharsets.UTF_8) };
	}

	private byte[] extractRootCertificate(JsonNode electionInfoNode) {
		final JsonNode electionRootCANode = electionInfoNode.path(ELECTION_ROOT_CA);
		if (electionRootCANode.isMissingNode()) {
			throw new JsonMissingNodeException(String.format("%s certificate is missing!", ELECTION_ROOT_CA));
		}
		return electionRootCANode.asText().getBytes(StandardCharsets.UTF_8);
	}

	private byte[] buildSignedInformation(Vote__1 vote) {
		String concatenatedInfo = Stream.of(
				vote.getEncryptedOptions(),
				vote.getEncryptedWriteIns(),
				vote.getCorrectnessIds(),
				vote.getVerificationCardPKSignature(),
				vote.getAuthenticationTokenSignature(),
				vote.getSchnorrProof(),
				vote.getVotingCardId(),
				vote.getElectionEventId()
		).filter(s -> Objects.nonNull(s) && !s.isEmpty()).collect(Collectors.joining());

		return concatenatedInfo.getBytes(StandardCharsets.UTF_8);
	}

}
