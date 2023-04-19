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

import static ch.post.it.evoting.cryptoprimitives.domain.ControlComponentConstants.NODE_IDS;
import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.security.SignatureException;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.domain.signature.Alias;
import ch.post.it.evoting.cryptoprimitives.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.configuration.ControlComponentPublicKeysPayload;
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
public class VerifySignatureControlComponentPublicKeys extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;

	private final SignatureVerification signatureVerification;

	protected VerifySignatureControlComponentPublicKeys(
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
						"verification.direct.trust.authenticity.description", "ControlComponentPublicKeys"));
		definition.setId("02.04");
		definition.setName("VerifySignatureControlComponentPublicKeys");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final List<ControlComponentPublicKeysPayload> controlComponentPublicKeysPayloads = electionDataExtractionService.getControlComponentPublicKeysPayloads(
				inputDirectoryPath);

		checkState(NODE_IDS.size() == controlComponentPublicKeysPayloads.size(),
				"The number of control component public keys payload should correspond to the number of control components.");

		controlComponentPublicKeysPayloads.stream().parallel()
				.forEach(payload -> checkState(NODE_IDS.contains(payload.getControlComponentPublicKeys().nodeId()),
						"Invalid node id. [nodeId: %s]", payload.getControlComponentPublicKeys().nodeId()));

		final boolean verified = controlComponentPublicKeysPayloads
				.stream()
				.parallel()
				.map(this::verifySignature)
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (verified) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification.direct.trust.signature.fail", "ControlComponentPublicKeys"));
		}

	}

	@VisibleForTesting
	boolean verifySignature(final ControlComponentPublicKeysPayload controlComponentPublicKeysPayload) {
		final int nodeId = controlComponentPublicKeysPayload.getControlComponentPublicKeys().nodeId();
		final String electionEventId = controlComponentPublicKeysPayload.getElectionEventId();
		final CryptoPrimitivesSignature signature = controlComponentPublicKeysPayload.getSignature();

		checkState(signature != null,
				"The signature of the control component public keys payload is null. [nodeId: %s, electionEventId: %s]", nodeId, electionEventId);

		final Hashable additionalContextData = ChannelSecurityContextData.controlComponentPublicKeys(nodeId, electionEventId);

		try {
			return signatureVerification.verifySignature(Alias.getControlComponentByNodeId(nodeId).toString(), controlComponentPublicKeysPayload,
					additionalContextData, signature.signatureContents());
		} catch (final SignatureException e) {
			throw new IllegalStateException(
					String.format("Could not verify the signature of the control component public keys payload. [nodeId: %s, electionEventId: %s]",
							nodeId, electionEventId));
		}
	}
}

