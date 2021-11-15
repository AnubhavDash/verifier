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
package ch.post.it.evoting.verifier.block.block4.verifications;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.hashing.HashService;
import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.AbstractVerification;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.dto.revised.BallotBox;
import ch.post.it.evoting.verifier.common.block.tools.CertificateLoader;
import ch.post.it.evoting.verifier.common.block.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.common.block.tools.SignatureService;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;

@Component
public class CheckSigOfflineProofs extends AbstractVerification {

	private final PathService pathService;
	private final ElectionDataExtractionService extractionService;
	private final HashService hashService;
	private final SignatureService signatureService;
	private final CertificateLoader certificateLoader;

	public CheckSigOfflineProofs(final PathService pathService, final ElectionDataExtractionService extractionService, final HashService hashService,
			final SignatureService signatureService, final CertificateLoader certificateLoader,
			final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
		this.extractionService = extractionService;
		this.hashService = hashService;
		this.signatureService = signatureService;
		this.certificateLoader = certificateLoader;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		var verificationDefinition = new VerificationDefinition();
		verificationDefinition.setBlockId(4);
		verificationDefinition.setCategory(Category.AUTHENTICITY);
		verificationDefinition.setId(61);
		verificationDefinition.addVerificationTrait(VerificationTrait.BLOCK_4);
		verificationDefinition.setName("checkSigOfflineProofs");
		verificationDefinition.setDescription(
				TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification61.description"));
		return verificationDefinition;
	}

	@Override
	@SuppressWarnings("java:S117")
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();
		final boolean bbDecryptVerif = deserializeVerificationInput(inputDirectoryPath)
				.parallel()
				.map(this::checkSigOfflineProofsBallotBox)
				.reduce(Boolean::logicalAnd).orElse(Boolean.FALSE);

		if (bbDecryptVerif) {
			return VerificationResultEvent.success(this, getVerificationDefinition());
		} else {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification61.nok.message"));
		}
	}

	/**
	 * Get all inputs for the verification from the relevant files.
	 *
	 * @param inputDirectoryPath the root directory of files
	 */
	private Stream<CheckSigOfflineProofsInput> deserializeVerificationInput(final Path inputDirectoryPath) {

		final var electionEvent = extractionService.getElectionEvent(inputDirectoryPath);
		final List<BallotBox> ballotBoxList = electionEvent.getBallotBoxes();
		final var ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		// Get the certificate used for signing.
		final X509Certificate signingCertificate = certificateLoader.loadCertificate(StructureKey.ADMIN_BOARD_CERT, inputDirectoryPath);

		// Get the intermediate certificates.
		final List<X509Certificate> intermediateCertificates = Collections
				.singletonList(certificateLoader.loadCertificate(StructureKey.TENANT_100, inputDirectoryPath));

		// Get the root certificate.
		final X509Certificate rootCertificate = certificateLoader.loadCertificate(StructureKey.PLATFORM_ROOT_CA, inputDirectoryPath);

		return ballotBoxList.stream()
				.parallel()
				.map(ballotBox -> ballotIdsPathNode.getRegexPath(TypeConverter.UUIDToStringWithoutDash(ballotBox.getId())))
				.filter(ballotBoxDirectoryPath -> Files
						.exists(ballotBoxDirectoryPath.resolve(pathService.getStructureNode(StructureKey.BALLOT_BOX_OFFLINE_MIXING).getQualifier())))
				.map(extractionService::getMixnetFinalPayload)
				.map(mixnetFinalPayload -> {
					final byte[] payloadHash = hashService.recursiveHash(mixnetFinalPayload);
					final byte[] payloadSignature = mixnetFinalPayload.getSignature().getSignatureContents();
					return CheckSigOfflineProofsInput.builder()
							.withSignature(payloadSignature)
							.withPayloadHash(payloadHash)
							.withSigningCertificate(signingCertificate)
							.withIntermediateCertificates(intermediateCertificates)
							.withRootCertificate(rootCertificate)
							.build();
				});
	}

	/**
	 * Verifies the signature of offline mixnet final proof for a ballot box.
	 *
	 * @param input for the verification of the signature of the mixnetFinalPayload
	 * @return true if the signature argument verifies correctly, false otherwise
	 */
	@SuppressWarnings("java:S117")
	boolean checkSigOfflineProofsBallotBox(final CheckSigOfflineProofsInput input) {
		return signatureService
				.verify(input.signature, input.payloadHash, input.signingCertificate, input.intermediateCertificates, input.rootCertificate);
	}

	// Data class for the input to check offline proofs
	static class CheckSigOfflineProofsInput {
		private final byte[] signature;
		private final byte[] payloadHash;
		private final X509Certificate signingCertificate;
		private final List<X509Certificate> intermediateCertificates;
		private final X509Certificate rootCertificate;

		private CheckSigOfflineProofsInput(final byte[] signature, final byte[] payloadHash, final X509Certificate signingCertificate,
				final List<X509Certificate> intermediateCertificates, final X509Certificate rootCertificate) {
			this.signature = checkNotNull(signature);
			this.payloadHash = checkNotNull(payloadHash);
			this.signingCertificate = checkNotNull(signingCertificate);
			this.intermediateCertificates = checkNotNull(intermediateCertificates);
			this.rootCertificate = checkNotNull(rootCertificate);
		}

		static CheckSigOfflineProofsInputBuilder builder() {
			return new CheckSigOfflineProofsInputBuilder();
		}

		static class CheckSigOfflineProofsInputBuilder {
			private byte[] signature;
			private byte[] payloadHash;
			private X509Certificate signingCertificate;
			private List<X509Certificate> intermediateCertificates;
			private X509Certificate rootCertificate;

			CheckSigOfflineProofsInputBuilder withSignature(final byte[] signature) {
				this.signature = checkNotNull(signature);
				return this;
			}

			CheckSigOfflineProofsInputBuilder withPayloadHash(final byte[] payloadHash) {
				this.payloadHash = checkNotNull(payloadHash);
				return this;
			}

			CheckSigOfflineProofsInputBuilder withSigningCertificate(final X509Certificate signingCertificate) {
				this.signingCertificate = checkNotNull(signingCertificate);
				return this;
			}

			CheckSigOfflineProofsInputBuilder withIntermediateCertificates(final List<X509Certificate> intermediateCertificates) {
				this.intermediateCertificates = checkNotNull(intermediateCertificates);
				return this;
			}

			CheckSigOfflineProofsInputBuilder withRootCertificate(final X509Certificate rootCertificate) {
				this.rootCertificate = checkNotNull(rootCertificate);
				return this;
			}

			public CheckSigOfflineProofsInput build() {
				return new CheckSigOfflineProofsInput(signature, payloadHash, signingCertificate, intermediateCertificates, rootCertificate);
			}
		}

	}

}