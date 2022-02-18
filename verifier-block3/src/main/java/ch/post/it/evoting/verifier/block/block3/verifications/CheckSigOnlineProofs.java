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
package ch.post.it.evoting.verifier.block.block3.verifications;

import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.hashing.HashService;
import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.plugin.contract.AbstractVerification;
import ch.post.it.evoting.verifier.plugin.contract.Category;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.plugin.contract.VerificationTrait;
import ch.post.it.evoting.verifier.core.internal.tools.CertificateLoader;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.core.internal.tools.SignatureService;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.core.internal.tools.TypeConverter;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathService;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerifierEvent;

@Component
public class CheckSigOnlineProofs extends AbstractVerification {

	private final HashService hashService;
	private final PathService pathService;
	private final SignatureService signatureService;
	private final CertificateLoader certificateLoader;
	private final ElectionDataExtractionService extractionService;

	public CheckSigOnlineProofs(final ElectionDataExtractionService extractionService, final HashService hashService,
			final SignatureService signatureService, final CertificateLoader certificateLoader, final PathService pathService,
			final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.hashService = hashService;
		this.pathService = pathService;
		this.signatureService = signatureService;
		this.certificateLoader = certificateLoader;
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(3);
		definition.setCategory(Category.AUTHENTICITY);
		definition.setId(61);
		definition.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		definition.setName("checkSigOnlineProofs");
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification61.description"));
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		final boolean shufflePayloadsSignaturesValid = deserializeVerificationInput(inputDirectoryPath)
				.map(this::checkSigShufflePayload)
				.reduce(Boolean::logicalAnd).orElse(Boolean.TRUE); // If there was one or no vote, there was no shuffle and hence no shuffle payload.

		if (shufflePayloadsSignaturesValid) {
			return VerificationResultEvent.success(this, getVerificationDefinition());
		} else {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification61.nok.message"));
		}
	}

	private Stream<CheckSigOnlineProofsInput> deserializeVerificationInput(Path inputDirectoryPath) {

		final var electionEvent = extractionService.getElectionEvent(inputDirectoryPath);
		final var ballotBoxList = electionEvent.getBallotBoxes();
		final var ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		final X509Certificate rootCertificate = certificateLoader.loadCertificate(StructureKey.PLATFORM_ROOT_CA, inputDirectoryPath);

		final var nodeCertificatesPathNode = pathService.buildFromRootPath(StructureKey.CCN_M_CA, inputDirectoryPath);
		final List<X509Certificate> nodesCertificates = nodeCertificatesPathNode.getRegexPaths().stream()
				.map(certificateLoader::loadCertificate)
				.collect(Collectors.toList());

		return ballotBoxList.stream()
				.parallel()
				.map(ballotBox -> TypeConverter.UUIDToStringWithoutDash(ballotBox.getId()))
				.map(ballotIdsPathNode::getRegexPath)
				.filter(ballotBoxDirectoryPath -> extractionService.getNumberOfVotes(ballotBoxDirectoryPath) > 0)
				.map(ballotBoxDirectoryPath -> {
					// Get the shuffle payloads.
					final var shufflePayloads = extractionService.getMixnetShufflePayloads(ballotBoxDirectoryPath);
					return shufflePayloads.stream()
							.map(payload -> {
								final byte[] payloadHash = hashService.recursiveHash(payload);
								final byte[] payloadSignature = payload.getSignature().getSignatureContents();
								final X509Certificate signingCertificate = payload.getSignature().getCertificateChain()[0];
								final List<X509Certificate> intermediateCertificates = Collections.singletonList(
										nodesCertificates.get(payload.getNodeId() - 1));
								return new CheckSigOnlineProofsInput(payloadSignature, payloadHash, signingCertificate, intermediateCertificates,
										rootCertificate);
							})
							.collect(Collectors.toList());
				})
				.flatMap(Collection::stream);
	}

	private boolean checkSigShufflePayload(final CheckSigOnlineProofsInput input) {
		return signatureService.verify(input.signature, input.payloadHash, input.signingCertificate, input.intermediateCertificates,
				input.rootCertificate);
	}

	// Data class for the input to check online proofs.
	private static class CheckSigOnlineProofsInput {

		private final byte[] signature;
		private final byte[] payloadHash;
		private final X509Certificate signingCertificate;
		private final List<X509Certificate> intermediateCertificates;
		private final X509Certificate rootCertificate;

		CheckSigOnlineProofsInput(final byte[] signature, final byte[] payloadHash, final X509Certificate signingCertificate,
				final List<X509Certificate> intermediateCertificates, final X509Certificate rootCertificate) {
			this.signature = signature;
			this.payloadHash = payloadHash;
			this.signingCertificate = signingCertificate;
			this.intermediateCertificates = intermediateCertificates;
			this.rootCertificate = rootCertificate;
		}
	}

}
