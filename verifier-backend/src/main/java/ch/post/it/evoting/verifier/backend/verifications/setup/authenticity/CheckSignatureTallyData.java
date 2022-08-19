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

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.domain.signature.Alias;
import ch.post.it.evoting.cryptoprimitives.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.ChannelSecurityContextData;
import ch.post.it.evoting.verifier.protocol.domain.configuration.SetupComponentTallyDataPayload;

@Component
public class CheckSignatureTallyData extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;

	private final SignatureVerification signatureVerification;

	protected CheckSignatureTallyData(
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
						"verification.direct.trust.authenticity.description", "SetupComponentTallyData"));
		definition.setId(205);
		definition.setName("CheckSignatureTallyData");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final var setupComponentTallyDataPayloads = electionDataExtractionService.getSetupComponentTallyDataPayloads(
				inputDirectoryPath);

		boolean verified = setupComponentTallyDataPayloads
				.stream()
				.map(this::verifySignature)
				.reduce(Boolean::logicalAnd)
				.orElseThrow();

		if (verified) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification.direct.trust.signature.fail", "SetupComponentTallyData"));
		}

	}

	@VisibleForTesting
	boolean verifySignature(final SetupComponentTallyDataPayload setupComponentTallyDataPayload) {
		final String electionEventId = setupComponentTallyDataPayload.getElectionEventId();
		final String verificationCardSetId = setupComponentTallyDataPayload.getVerificationCardSetId();
		final CryptoPrimitivesSignature signature = setupComponentTallyDataPayload.getSignature();

		checkState(signature != null,
				"The signature of the setup component tally data payload is null. [electionEventId: %s, verificationCardSetId: %s]", electionEventId,
				verificationCardSetId);

		final Hashable additionalContextData = ChannelSecurityContextData.setupComponentTallyData(electionEventId, verificationCardSetId);

		try {
			return signatureVerification.verifySignature(Alias.SDM_CONFIG.toString(), setupComponentTallyDataPayload, additionalContextData,
					signature.signatureContents());
		} catch (final SignatureException e) {
			throw new IllegalStateException(
					String.format(
							"Could not verify the signature of the setup component tally data payload. [electionEventId: %s, verificationCardSetId: %s]",
							electionEventId, verificationCardSetId));
		}
	}
}

