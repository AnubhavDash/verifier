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
package ch.post.it.evoting.verifier.backend.verifications.setup.authenticity;

import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.security.SignatureException;

import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.domain.signature.Alias;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.xml.hashable.HashableCantonConfigFactory;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
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

	private final SignatureVerification signatureVerification;

	protected VerifySignatureCantonConfig(
			final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService electionDataExtractionService,
			final SignatureVerification signatureVerification) {
		super(resultPublisherService);
		this.electionDataExtractionService = electionDataExtractionService;
		this.signatureVerification = signatureVerification;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.AUTHENTICITY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification.direct.trust.authenticity.description", "CantonConfig"));
		definition.setId("02.02");
		definition.setName("VerifySignatureCantonConfig");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final Configuration configuration = electionDataExtractionService.getCantonConfig(inputDirectoryPath);

		final boolean verified = verifySignature(configuration);

		if (verified) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification.direct.trust.signature.fail", "CantonConfig"));
		}

	}

	@VisibleForTesting
	boolean verifySignature(final Configuration configuration) {
		final byte[] signature = configuration.getSignature();

		checkState(signature != null, "The signature of the canton config file is null.");

		final Hashable hash = HashableCantonConfigFactory.fromConfiguration(configuration);
		final Hashable additionalContextData = ChannelSecurityContextData.cantonConfig();

		try {
			return signatureVerification.verifySignature(Alias.CANTON.toString(), hash, additionalContextData, signature);
		} catch (final SignatureException e) {
			throw new IllegalStateException("Could not verify the signature of the canton config file.");
		}
	}
}

