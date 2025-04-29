/*
 * (c) Copyright 2024 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.backend.verifications.setup.authenticity;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;

import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.channelsecurity.XMLSignatureService;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component
public class VerifySignatureCantonConfig extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;
	private final XMLSignatureService xmlSignatureService;
	private final KeyStore keyStore;

	protected VerifySignatureCantonConfig(
			final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService electionDataExtractionService,
			final XMLSignatureService xmlSignatureService,
			final KeyStore keyStore) {
		super(resultPublisherService);
		this.electionDataExtractionService = electionDataExtractionService;
		this.xmlSignatureService = xmlSignatureService;
		this.keyStore = keyStore;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.AUTHENTICITY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification.direct.trust.authenticity.description", "CantonConfig"));
		definition.setId("02.01");
		definition.setName("VerifySignatureCantonConfig");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final Path cantonConfigPath = electionDataExtractionService.getCantonConfigPath(inputDirectoryPath);
		final boolean verified = verifySignature(cantonConfigPath);

		if (verified) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification.direct.trust.signature.fail", "CantonConfig"));
		}

	}

	@VisibleForTesting
	boolean verifySignature(final Path configurationPath) {
		checkNotNull(configurationPath);

		final PublicKey signatureVerificationKey;
		try (final InputStream configurationIn = Files.newInputStream(configurationPath)) {
			signatureVerificationKey = keyStore.getCertificate(Alias.CANTON.get()).getPublicKey();
			return xmlSignatureService.verifyXMLSignature(configurationIn, signatureVerificationKey);
		} catch (final KeyStoreException e) {
			throw new IllegalStateException("Unable to open keystore", e);
		} catch (final IOException e) {
			throw new UncheckedIOException("Could not read configuration-anonymized file", e);
		}
	}
}

