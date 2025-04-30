/*
 * (c) Copyright 2025 Swiss Post Ltd.
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

import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.security.SignatureException;

import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
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
public class VerifySignatureElectionEventContext extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;

	private final SignatureVerification signatureVerification;

	protected VerifySignatureElectionEventContext(
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
						"verification.direct.trust.authenticity.description", "ElectionEventContext"));
		definition.setId("02.05");
		definition.setName("VerifySignatureElectionEventContext");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(
				inputDirectoryPath);

		final boolean verified = verifySignature(electionEventContextPayload);

		if (verified) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification.direct.trust.signature.fail", "SetupComponentPublicKeys"));
		}

	}

	@VisibleForTesting
	boolean verifySignature(final ElectionEventContextPayload electionEventContextPayload) {
		final String electionEventId = electionEventContextPayload.getElectionEventContext().electionEventId();
		final CryptoPrimitivesSignature signature = electionEventContextPayload.getSignature();

		checkState(signature != null, "The signature of the election event context payload is null. [electionEventId: %s]", electionEventId);

		final Hashable additionalContextData = ChannelSecurityContextData.electionEventContext(electionEventId);

		try {
			return signatureVerification.verifySignature(Alias.SDM_CONFIG.toString(), electionEventContextPayload,
					additionalContextData, signature.signatureContents());
		} catch (final SignatureException e) {
			throw new IllegalStateException(
					String.format("Could not verify the signature of the election event context payload. [electionEventId: %s]", electionEventId));
		}
	}
}
