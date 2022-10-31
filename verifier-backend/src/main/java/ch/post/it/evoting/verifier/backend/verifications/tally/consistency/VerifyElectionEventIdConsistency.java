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

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.TallyComponentShufflePayload;
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
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.verifier.protocol.domain.tally.TallyComponentVotesPayload;

@Component("VerifyTallyElectionEventIdConsistency")
public class VerifyElectionEventIdConsistency extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;

	protected VerifyElectionEventIdConsistency(
			final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService electionDataExtractionService) {
		super(resultPublisherService);
		this.electionDataExtractionService = electionDataExtractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification307.description"));
		definition.setId(307);
		definition.setName("VerifyElectionEventIdConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final String electionEventId = electionDataExtractionService.getElectionEventContextPayload(inputDirectoryPath).getElectionEventContext()
				.electionEventId();

		final boolean areControlComponentBallotBoxPayloadVerified = electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
						inputDirectoryPath).stream()
				.map(ControlComponentBallotBoxPayload::getElectionEventId)
				.allMatch(id -> id.equals(electionEventId));

		final boolean areControlComponentShufflePayloadsVerified = electionDataExtractionService.getAllControlComponentShufflePayloadsOrderedByNodeId(
						inputDirectoryPath).stream()
				.map(ControlComponentShufflePayload::getElectionEventId)
				.allMatch(id -> id.equals(electionEventId));

		final boolean areTallyComponentShufflePayloadsVerified = electionDataExtractionService.getTallyComponentShufflePayloads(inputDirectoryPath)
				.stream()
				.map(TallyComponentShufflePayload::getElectionEventId)
				.allMatch(id -> id.equals(electionEventId));

		final boolean areTallyComponentVotesPayloadsVerified = electionDataExtractionService.getTallyComponentVotesPayloads(
						inputDirectoryPath).stream()
				.map(TallyComponentVotesPayload::getElectionEventId)
				.allMatch(id -> id.equals(electionEventId));

		if (areControlComponentBallotBoxPayloadVerified && areControlComponentShufflePayloadsVerified && areTallyComponentShufflePayloadsVerified
				&& areTallyComponentVotesPayloadsVerified) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification307.nok.message"));
		}
	}

}

