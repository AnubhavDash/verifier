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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence.verifications;

import java.nio.file.Path;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.DecryptionEvent;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.evidence.algorithms.VerifyOnlineControlComponentsAlgorithm;

@Component
public class VerifyOnlineControlComponents extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;
	private final VerifyOnlineControlComponentsAlgorithm verifyOnlineControlComponentsAlgorithm;

	public VerifyOnlineControlComponents(
			final ApplicationEventPublisher applicationEventPublisher,
			final ElectionDataExtractionService electionDataExtractionService,
			final VerifyOnlineControlComponentsAlgorithm verifyOnlineControlComponentsAlgorithm) {
		super(applicationEventPublisher);
		this.electionDataExtractionService = electionDataExtractionService;
		this.verifyOnlineControlComponentsAlgorithm = verifyOnlineControlComponentsAlgorithm;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.EVIDENCE);
		definition.setDescription(TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME,
				"tally.verification01.description"));
		definition.setId(1);
		definition.setName("verifyOnlineControlComponents");
		definition.addVerifierEvent(DecryptionEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(
				inputDirectoryPath);
		final ElectionEventContext electionEventContext = electionEventContextPayload.getElectionEventContext();

		final String electionEventId = electionEventContext.electionEventId();
		final List<String> ballotBoxIds = electionEventContext.verificationCardSetContexts().stream()
				.map(VerificationCardSetContext::ballotBoxId)
				.toList();

		final VerificationResult verificationResult;
		if (verifyOnlineControlComponentsAlgorithm.verifyOnlineControlComponents(inputDirectoryPath, electionEventId, ballotBoxIds)) {
			verificationResult = VerificationResult.success(getVerificationDefinition());
		} else {
			verificationResult = VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification01.nok.message"));
		}

		return verificationResult;
	}
}
