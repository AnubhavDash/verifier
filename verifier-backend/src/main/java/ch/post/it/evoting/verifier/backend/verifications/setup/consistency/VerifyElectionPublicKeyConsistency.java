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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.evotinglibraries.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.evotinglibraries.domain.election.ControlComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.election.SetupComponentPublicKeys;
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
public class VerifyElectionPublicKeyConsistency extends AbstractVerification {

	private final ElGamal elGamal;
	private final ElectionDataExtractionService extractionService;

	protected VerifyElectionPublicKeyConsistency(
			final ElGamal elGamal,
			final ElectionDataExtractionService extractionService,
			final ResultPublisherService resultPublisherService) {
		super(resultPublisherService);
		this.elGamal = elGamal;
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification312.description"));
		definition.setId("03.12");
		definition.setName("VerifyElectionPublicKeyConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		if (verifyElectionPublicKeyConsistency(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification312.nok.message"));
		}
	}

	@SuppressWarnings("java:S117")
	private boolean verifyElectionPublicKeyConsistency(final Path inputDirectoryPath) {

		// Input.
		final Stream<ElGamalMultiRecipientPublicKey> EL_pk_j = extractionService.getControlComponentPublicKeysPayloads(inputDirectoryPath)
				.parallel()
				.map(ControlComponentPublicKeysPayload::getControlComponentPublicKeys)
				.map(ControlComponentPublicKeys::ccmjElectionPublicKey);
		final SetupComponentPublicKeys setupComponentPublicKeys = extractionService.getSetupComponentPublicKeysPayload(inputDirectoryPath)
				.getSetupComponentPublicKeys();
		final ElGamalMultiRecipientPublicKey EL_pk = setupComponentPublicKeys.electionPublicKey();
		final ElGamalMultiRecipientPublicKey EB_pk = setupComponentPublicKeys.electoralBoardPublicKey();

		// Operation.
		final ElGamalMultiRecipientPublicKey combined_EB_pk_EL_pk_j = elGamal.combinePublicKeys(Stream.concat(Stream.of(EB_pk), EL_pk_j)
				.collect(GroupVector.toGroupVector())
		);

		return EL_pk.equals(combined_EB_pk_EL_pk_j);
	}
}
