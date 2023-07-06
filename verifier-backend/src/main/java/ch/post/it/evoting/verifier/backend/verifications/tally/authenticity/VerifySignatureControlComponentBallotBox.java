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
package ch.post.it.evoting.verifier.backend.verifications.tally.authenticity;

import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.security.SignatureException;

import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;

@Component
public class VerifySignatureControlComponentBallotBox extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;

	private final SignatureVerification signatureVerification;

	protected VerifySignatureControlComponentBallotBox(
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
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.AUTHENTICITY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification.direct.trust.authenticity.description", "ControlComponentBallotBox"));
		definition.setId("07.01");
		definition.setName("VerifySignatureControlComponentBallotBox");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final boolean verified = electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(inputDirectoryPath)
				.parallel()
				.map(this::verifySignature)
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (verified) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification.direct.trust.signature.fail", "ControlComponentBallotBox"));
		}

	}

	@VisibleForTesting
	boolean verifySignature(final ControlComponentBallotBoxPayload controlComponentBallotBoxPayload) {
		final int nodeId = controlComponentBallotBoxPayload.getNodeId();
		final String electionEventId = controlComponentBallotBoxPayload.getElectionEventId();
		final String ballotBoxId = controlComponentBallotBoxPayload.getBallotBoxId();
		final CryptoPrimitivesSignature signature = controlComponentBallotBoxPayload.getSignature();

		checkState(signature != null,
				"The signature of the control component ballot box payload is null. [nodeID: %s, electionEventId: %s, ballotBoxId: %s]",
				nodeId, electionEventId, ballotBoxId);

		final Hashable additionalContextData = ChannelSecurityContextData.controlComponentBallotBox(nodeId, electionEventId, ballotBoxId);

		try {
			return signatureVerification.verifySignature(Alias.getControlComponentByNodeId(nodeId).toString(), controlComponentBallotBoxPayload,
					additionalContextData, signature.signatureContents());
		} catch (final SignatureException e) {
			throw new IllegalStateException(String.format(
					"Could not verify the signature of the control component ballot box payload. [nodeId: %s, electionEventId: %s, ballotBoxId: %s]",
					nodeId, electionEventId, ballotBoxId));
		}
	}
}
