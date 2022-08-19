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
package ch.post.it.evoting.verifier.backend.verifications.setup.authenticity.verifications;

import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.security.SignatureException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SetupComponentVerificationDataPayload;
import ch.post.it.evoting.cryptoprimitives.domain.signature.Alias;
import ch.post.it.evoting.cryptoprimitives.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.ConfigurationEvent;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.ChannelSecurityContextData;

@Component
public class CheckSignatureVerificationData extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;

	private final SignatureVerification signatureVerification;

	protected CheckSignatureVerificationData(
			final ApplicationEventPublisher applicationEventPublisher,
			final ElectionDataExtractionService electionDataExtractionService,
			final SignatureVerification signatureVerification) {
		super(applicationEventPublisher);
		this.electionDataExtractionService = electionDataExtractionService;
		this.signatureVerification = signatureVerification;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.AUTHENTICITY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification.direct.trust.authenticity.description", "SetupComponentVerificationData"));
		definition.setId(93);
		definition.setName("checkSignatureVerificationData");
		definition.addVerifierEvent(ConfigurationEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final var setupComponentVerificationDataPayloads = electionDataExtractionService.getSetupComponentVerificationDataPayloads(
				inputDirectoryPath);

		final boolean verified = setupComponentVerificationDataPayloads
				.stream()
				.map(this::verifySignature)
				.reduce(Boolean::logicalAnd)
				.orElseThrow();

		if (verified) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification.direct.trust.signature.fail", "SetupComponentVerificationData"));
		}

	}

	@VisibleForTesting
	boolean verifySignature(final SetupComponentVerificationDataPayload setupComponentVerificationDataPayload) {
		final String electionEventId = setupComponentVerificationDataPayload.getElectionEventId();
		final String verificationCardSetId = setupComponentVerificationDataPayload.getVerificationCardSetId();
		final CryptoPrimitivesSignature signature = setupComponentVerificationDataPayload.getSignature();

		checkState(signature != null,
				"The signature of the setup component verification data payload is null. [electionEventId: %s, verificationCardSetId: %s]",
				electionEventId, verificationCardSetId);

		final Hashable additionalContextData = ChannelSecurityContextData.setupComponentVerificationData(electionEventId, verificationCardSetId);

		try {
			return signatureVerification.verifySignature(Alias.SDM_CONFIG.toString(), setupComponentVerificationDataPayload, additionalContextData,
					signature.signatureContents());
		} catch (final SignatureException e) {
			throw new IllegalStateException(
					String.format(
							"Could not verify the signature of the setup component verification data payload. [electionEventId: %s, verificationCardSetId: %s]",
							electionEventId, verificationCardSetId));
		}
	}
}