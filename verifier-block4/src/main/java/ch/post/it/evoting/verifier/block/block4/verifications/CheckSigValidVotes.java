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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
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
public class CheckSigValidVotes extends AbstractVerification {

	private final PathService pathService;
	private final CertificateLoader certificateLoader;

	public CheckSigValidVotes(final PathService pathService, final CertificateLoader certificateLoader,
			final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
		this.certificateLoader = certificateLoader;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(4);
		definition.setCategory(Category.AUTHENTICITY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification77.description"));
		definition.setId(77);
		definition.setName("checkSigValidVotes");
		definition.addVerificationTrait(VerificationTrait.BLOCK_4);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		// Get the certificate used for signing.
		final byte[] signingCertificate = certificateLoader.loadBytes(StructureKey.ADMIN_BOARD_CERT, inputDirectoryPath);

		// Get root certificate.
		final byte[] rootCertificate = certificateLoader.loadBytes(StructureKey.TENANT_100, inputDirectoryPath);

		// Get all valid votes results.
		final var validVotesPathNode = pathService.buildFromRootPath(StructureKey.VALID_VOTES_RESULT, inputDirectoryPath);

		// Verify signature of each file
		for (final Path validVotesPath : validVotesPathNode.getRegexPaths()) {
			final byte[] content;
			final byte[] signature;
			try {
				content = Files.readAllBytes(validVotesPath);
				signature = Files.readAllBytes(validVotesPathNode.getRelation(RelationType.METADATA));
			} catch (IOException e) {
				throw new UncheckedIOException("Failed to read valid vote results or its metadata file.", e);
			}

			if (!SignatureChecker.verifyMetadata(content, signature, signingCertificate, rootCertificate)) {
				return VerificationResultEvent.failure(this, getVerificationDefinition(),
						TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification77.nok.message"));
			}
		}

		return VerificationResultEvent.success(this, getVerificationDefinition());
	}

}
