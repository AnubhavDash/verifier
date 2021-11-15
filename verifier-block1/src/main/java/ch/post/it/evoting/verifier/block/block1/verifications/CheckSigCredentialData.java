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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.AbstractVerification;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.tools.CertificateLoader;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;

@Component
public class CheckSigCredentialData extends AbstractVerification {

	private final PathService pathService;
	private final CertificateLoader certificateLoader;

	public CheckSigCredentialData(final PathService pathService, final CertificateLoader certificateLoader,
			final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
		this.certificateLoader = certificateLoader;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(1);
		definition.setCategory(Category.AUTHENTICITY);
		definition.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification78.description"));
		definition.setId(78);
		definition.setName("checkSigCredentialData");
		definition.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		definition.addVerificationTrait(VerificationTrait.BLOCK_1);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		// Get the certificate used for signing.
		final byte[] signingCertificate = certificateLoader.loadBytes(StructureKey.ADMIN_BOARD_CERT, inputDirectoryPath);

		// Get the intermediate certificates.
		final var intermediateCertificates = new byte[][] { certificateLoader.loadBytes(StructureKey.TENANT_100, inputDirectoryPath) };

		// Get the root certificate.
		final byte[] rootCertificate = certificateLoader.loadBytes(StructureKey.PLATFORM_ROOT_CA, inputDirectoryPath);

		final var votingCardIdPathNode = pathService.buildFromRootPath(StructureKey.VOTING_CARD_SETS_ID_DIR, inputDirectoryPath);

		// Iterate over all directories and do the verification for each credentialData.
		for (final Path regexPath : votingCardIdPathNode.getRegexPaths()) {
			final var credentialDataPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.CREDENTIAL_DATA, regexPath);

			for (final Path credentialDataPath : credentialDataPathNode.getRegexPaths()) {

				// Get source and signature.
				final byte[] source;
				final byte[] signatureBase64;
				try {
					source = Files.readAllBytes(credentialDataPath);
					signatureBase64 = Files.readAllBytes(credentialDataPathNode.getRelation(RelationType.SIGN, credentialDataPath));
				} catch (IOException e) {
					throw new UncheckedIOException("Failed to read credential data or its signature file.", e);
				}

				// Decode the signature.
				final byte[] signature = Base64.getDecoder().decode(signatureBase64);

				// Check signatures.
				if (!SignatureChecker.verifySignature(source, signature, signingCertificate, intermediateCertificates, rootCertificate)) {
					return VerificationResultEvent.failure(this, getVerificationDefinition(),
							TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification78.nok.message",
									credentialDataPath.toString()));
				}
			}
		}

		return VerificationResultEvent.success(this, getVerificationDefinition());
	}

}
