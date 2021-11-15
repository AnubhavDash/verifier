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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ChoiceCodeGenerationDTO;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ReturnCodeGenerationResponsePayload;
import ch.post.it.evoting.cryptoprimitives.hashing.HashService;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.AbstractVerification;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.tools.CertificateLoader;
import ch.post.it.evoting.verifier.common.block.tools.SignatureService;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;

@Component
public class CheckSigNodeContributions extends AbstractVerification {

	private final PathService pathService;
	private final SignatureService signatureService;
	private final CertificateLoader certificateLoader;
	private final HashService hashService;
	private final ObjectMapper objectMapper;

	public CheckSigNodeContributions(final PathService pathService, final SignatureService signatureService,
			final CertificateLoader certificateLoader, final HashService hashService, final ApplicationEventPublisher applicationEventPublisher,
			final ObjectMapper objectMapper) {
		super(applicationEventPublisher);
		this.pathService = pathService;
		this.signatureService = signatureService;
		this.certificateLoader = certificateLoader;
		this.hashService = hashService;
		this.objectMapper = objectMapper;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(1);
		definition.setCategory(Category.AUTHENTICITY);
		definition
				.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification61.description"));
		definition.setId(61);
		definition.setName("checkSigNodeContributions");
		definition.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		definition.addVerificationTrait(VerificationTrait.BLOCK_1);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		final boolean verification = getVerificationInputs(inputDirectoryPath)
				.parallel()
				.map(this::checkSignatureNodeContributions)
				.reduce(Boolean::logicalAnd).orElse(Boolean.FALSE);

		if (verification) {
			return VerificationResultEvent.success(this, this.getVerificationDefinition());
		} else {
			return VerificationResultEvent.failure(this, this.getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification61.nok.message"));
		}
	}

	/**
	 * Gets all inputs for the verification from the relevant files.
	 *
	 * @param inputDirectoryPath the root directory of files
	 * @return a Stream of verification input, one per ballot box
	 */
	private Stream<NodeContributionsChunk> getVerificationInputs(final Path inputDirectoryPath) {
		// Root and node certificates
		final X509Certificate rootCertificate = certificateLoader.loadCertificate(StructureKey.PLATFORM_ROOT_CA, inputDirectoryPath);
		final var nodeCertificatesPathNode = pathService.buildFromRootPath(StructureKey.CC_CA, inputDirectoryPath);
		final var nodeCertificates = new NodeCertificates(nodeCertificatesPathNode.getRegexPaths(), rootCertificate);

		// Iterate over all verification card set ids directories
		final var verificationCardSetsIdPathNode = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		return verificationCardSetsIdPathNode.getRegexPaths().stream()
				.map(vcsIdPath -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_CONTRIBUTION, vcsIdPath))
				.flatMap(nodeContributionsChunkPathNode -> nodeContributionsChunkPathNode.getRegexPaths().stream())
				.map(chunkFilePath -> {
					final List<ChoiceCodeGenerationDTO<ReturnCodeGenerationResponsePayload>> choiceCodeGenerationDTOS;
					try {
						choiceCodeGenerationDTOS = objectMapper.readValue(chunkFilePath.toFile(), new TypeReference<>() {
						});
					} catch (IOException e) {
						throw new UncheckedIOException("Failed to deserialize the node contributions chunk file.", e);
					}

					// Extract signature and hash for each node
					final List<NodeOutputSignature> nodeOutputSignatures = choiceCodeGenerationDTOS.stream()
							.map(output -> new NodeOutputSignature(output.getPayload(), nodeCertificates, hashService, certificateLoader))
							.collect(Collectors.toList());

					return new NodeContributionsChunk(nodeOutputSignatures);
				});
	}

	/**
	 * Check the signature of each node output from a node contributions file.
	 *
	 * @param nodeContributionsChunk node contributions input from file
	 * @return {@code true} if all the signature are valid
	 */
	private boolean checkSignatureNodeContributions(final NodeContributionsChunk nodeContributionsChunk) {
		return nodeContributionsChunk.nodeOutputs.stream()
				.parallel()
				.map(output -> signatureService
						.verify(output.signature, output.payloadHash, output.signingCertificate, output.intermediateCertificates,
								output.rootCertificate))
				.reduce(Boolean::logicalAnd).orElse(Boolean.FALSE);
	}

	static class NodeCertificates {
		private final List<Path> nodeCertificatesPaths;
		private final X509Certificate rootCertificate;

		public NodeCertificates(final List<Path> nodeCertificatesPaths, final X509Certificate rootCertificate) {
			this.nodeCertificatesPaths = checkNotNull(nodeCertificatesPaths);
			this.rootCertificate = checkNotNull(rootCertificate);
		}
	}

	// Data class containing the node contributions by chunk
	static class NodeContributionsChunk {
		private final List<NodeOutputSignature> nodeOutputs;

		public NodeContributionsChunk(final List<NodeOutputSignature> nodeOutputs) {
			this.nodeOutputs = nodeOutputs;
		}
	}

	// Data class that represent node contributions signature to verify
	static class NodeOutputSignature {
		private final byte[] signature;
		private final byte[] payloadHash;
		private final X509Certificate signingCertificate;
		private final List<X509Certificate> intermediateCertificates;
		private final X509Certificate rootCertificate;

		// Massage the data to get it into the expected format for the verification algorithm
		NodeOutputSignature(final ReturnCodeGenerationResponsePayload payload, final NodeCertificates nodeCertificates, HashService hashService,
				CertificateLoader certificateLoader) {
			// Signature
			this.signature = payload.getSignature().getSignatureContents();
			this.payloadHash = hashService.recursiveHash(payload);

			// Certificates chain
			this.signingCertificate = payload.getSignature().getCertificateChain()[0];
			final List<Path> filteredCertificates = nodeCertificates.nodeCertificatesPaths.stream()
					.filter(ccPath -> ccPath.getFileName().toString().equals("cc" + payload.getNodeId() + "_CA.pem"))
					.collect(Collectors.toList());
			this.intermediateCertificates = Collections.singletonList(certificateLoader.loadCertificate(filteredCertificates.get(0)));
			this.rootCertificate = nodeCertificates.rootCertificate;
		}
	}
}
