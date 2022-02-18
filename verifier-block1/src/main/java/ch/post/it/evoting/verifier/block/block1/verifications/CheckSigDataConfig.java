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
package ch.post.it.evoting.verifier.block.block1.verifications;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.plugin.contract.AbstractVerification;
import ch.post.it.evoting.verifier.plugin.contract.Category;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.plugin.contract.VerificationTrait;
import ch.post.it.evoting.verifier.core.internal.tools.CertificateLoader;
import ch.post.it.evoting.verifier.core.internal.tools.SignatureChecker;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathService;
import ch.post.it.evoting.verifier.core.internal.tools.path.RelationType;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerifierEvent;

@Component
public class CheckSigDataConfig extends AbstractVerification {

	private final PathService pathService;
	private final CertificateLoader certificateLoader;

	public CheckSigDataConfig(final PathService pathService, final CertificateLoader certificateLoader,
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
				"verification74.description"));
		definition.setId(74);
		definition.setName("checkSigDataConfig");
		definition.addVerificationTrait(VerificationTrait.CONFIGURATION);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		// Get the signing certificate.
		final byte[] signCertificate = certificateLoader.loadBytes(StructureKey.ADMIN_BOARD_CERT, inputDirectoryPath);

		// Get the root certificate.
		final byte[] rootCA = certificateLoader.loadBytes(StructureKey.TENANT_100, inputDirectoryPath);

		// Verify signatures.
		final var dataConfigPathNode = pathService.buildFromRootPath(StructureKey.DATA_CONFIG_UPDATED, inputDirectoryPath);
		for (Path regexPath : dataConfigPathNode.getRegexPaths()) {
			final byte[] content;
			final byte[] signature;
			try {
				content = Files.readAllBytes(regexPath);
				signature = Files.readAllBytes(dataConfigPathNode.getRelation(RelationType.METADATA, regexPath));
			} catch (IOException e) {
				throw new UncheckedIOException("Failed to read data config updated or its signature file.", e);
			}

			if (!SignatureChecker.verifyMetadata(content, signature, signCertificate, rootCA)) {
				return VerificationResultEvent.failure(this, getVerificationDefinition(),
						TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
								"verification74.nok.message",
								regexPath.toString()));
			}
		}

		return VerificationResultEvent.success(this, getVerificationDefinition());
	}
}
