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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import java.nio.file.Path;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.mixnet.EncryptionParametersPayload;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component
public class VerifySmallPrimeGroupMembers extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;
	private final VerifySmallPrimeGroupMembersAlgorithm verifySmallPrimeGroupMembersAlgorithm;

	VerifySmallPrimeGroupMembers(final ElectionDataExtractionService extractionService,
			final VerifySmallPrimeGroupMembersAlgorithm verifySmallPrimeGroupMembersAlgorithm,
			final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.extractionService = extractionService;
		this.verifySmallPrimeGroupMembersAlgorithm = verifySmallPrimeGroupMembersAlgorithm;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.EVIDENCE);
		definition.setDescription(TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
				"setup.verification501.description"));
		definition.setId(501);
		definition.setName("VerifySmallPrimeGroupMembers");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		// Get the encryption parameters.
		final EncryptionParametersPayload encryptionParametersPayload = extractionService.getEncryptionParametersPayload(inputDirectoryPath);

		// Get the primes from the file.
		final GroupVector<PrimeGqElement, GqGroup> smallPrimeGroupMembers = encryptionParametersPayload.getSmallPrimes();

		if (verifySmallPrimeGroupMembersAlgorithm.verifySmallPrimeGroupMembers(smallPrimeGroupMembers)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification501.nok.message"));
		}
	}
}

