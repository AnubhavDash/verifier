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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import java.nio.file.Path;
import java.util.function.BiFunction;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
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

@Component("verifyTallyElectionEventIdConsistency")
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
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification804.description"));
		definition.setId("08.04");
		definition.setName("VerifyElectionEventIdConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		if (verifyElectionEventIdConsistency(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification804.nok.message"));
		}
	}

	private boolean verifyElectionEventIdConsistency(final Path inputDirectoryPath) {
		// Input.
		final String electionEventContextElectionEventId = electionDataExtractionService.getElectionEventContext(inputDirectoryPath)
				.electionEventId();

		// Operation.
		final ImmutableList<BiFunction<Path, String, Boolean>> validations = ImmutableList.of(
				this::validateControlComponentBallotBox,
				this::validateOnlineControlComponentShuffle,
				this::validateTallyControlComponentShuffle,
				this::validateTallyControlComponentVotes);

		return validations.stream()
				.parallel()
				.map(f -> f.apply(inputDirectoryPath, electionEventContextElectionEventId))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	private boolean validateControlComponentBallotBox(final Path inputDirectoryPath, final String electionEventContextElectionEventId) {
		return electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(inputDirectoryPath)
				.parallel()
				.map(ControlComponentBallotBoxPayload::getElectionEventId)
				.allMatch(electionEventContextElectionEventId::equals);
	}

	private boolean validateOnlineControlComponentShuffle(final Path inputDirectoryPath, final String electionEventContextElectionEventId) {
		return electionDataExtractionService.getAllControlComponentShufflePayloadsOrderedByNodeId(inputDirectoryPath)
				.parallel()
				.map(ControlComponentShufflePayload::getElectionEventId)
				.allMatch(electionEventContextElectionEventId::equals);
	}

	private boolean validateTallyControlComponentShuffle(final Path inputDirectoryPath, final String electionEventContextElectionEventId) {
		return electionDataExtractionService.getTallyComponentShufflePayloads(inputDirectoryPath)
				.parallel()
				.map(TallyComponentShufflePayload::getElectionEventId)
				.allMatch(electionEventContextElectionEventId::equals);
	}

	private boolean validateTallyControlComponentVotes(final Path inputDirectoryPath, final String electionEventContextElectionEventId) {
		return electionDataExtractionService.getTallyComponentVotesPayloads(inputDirectoryPath)
				.parallel()
				.map(TallyComponentVotesPayload::getElectionEventId)
				.allMatch(electionEventContextElectionEventId::equals);
	}

}

