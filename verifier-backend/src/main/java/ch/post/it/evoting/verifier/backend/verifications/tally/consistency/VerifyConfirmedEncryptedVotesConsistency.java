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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import static ch.post.it.evoting.verifier.backend.tools.TranslationHelper.getFromResourceBundle;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.cryptoprimitives.utils.Validations;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

@Component
public class VerifyConfirmedEncryptedVotesConsistency extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;

	public VerifyConfirmedEncryptedVotesConsistency(ApplicationEventPublisher applicationEventPublisher,
			final ElectionDataExtractionService extractionService) {
		super(applicationEventPublisher);
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME,
				"tally.verification300.description"));
		definition.setId(300);
		definition.setName("VerifyConfirmedEncryptedVotesConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) {
		final Stream<List<ControlComponentBallotBoxPayload>> ballotBoxPayloads = extractionService.getElectionEventContextPayload(inputDirectoryPath)
				.getElectionEventContext()
				.verificationCardSetContexts().stream()
				.map(VerificationCardSetContext::ballotBoxId)
				.map(ballotBoxId -> extractionService.getControlComponentBallotBoxPayloadsOrderedByNodeId(inputDirectoryPath, ballotBoxId));
		if (confirmedVotesConsistent(ballotBoxPayloads)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification300.nok.message"));
		}
	}

	private boolean confirmedVotesConsistent(final Stream<List<ControlComponentBallotBoxPayload>> ballotBoxPayloadSupplier) {
		return ballotBoxPayloadSupplier
				.map(ballotBoxPayloads -> Validations.allEqual(ballotBoxPayloads.stream(),
						ControlComponentBallotBoxPayload::getConfirmedEncryptedVotes))
				.reduce(Boolean::logicalAnd).orElse(Boolean.FALSE);
	}
}
