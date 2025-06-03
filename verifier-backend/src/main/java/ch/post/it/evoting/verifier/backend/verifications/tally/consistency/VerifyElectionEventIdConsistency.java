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
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification808.description"));
		definition.setId("08.08");
		definition.setName("VerifyElectionEventIdConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final String electionEventId = electionDataExtractionService.getElectionEventContext(inputDirectoryPath).electionEventId();

		final ImmutableList<BiFunction<Path, String, Boolean>> validations = ImmutableList.of(
				this::areControlComponentBallotBoxPayloadsVerified,
				this::areControlComponentShufflePayloadsVerified,
				this::areTallyComponentShufflePayloadsVerified,
				this::areTallyComponentVotesPayloadsVerified);

		final boolean verified = validations
				.stream()
				.parallel()
				.map(f -> f.apply(inputDirectoryPath, electionEventId))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (verified) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification808.nok.message"));
		}
	}

	private boolean areTallyComponentVotesPayloadsVerified(final Path inputDirectoryPath, final String electionEventId) {
		return electionDataExtractionService.getTallyComponentVotesPayloads(inputDirectoryPath)
				.parallel()
				.map(TallyComponentVotesPayload::getElectionEventId)
				.allMatch(id -> id.equals(electionEventId));
	}

	private boolean areTallyComponentShufflePayloadsVerified(final Path inputDirectoryPath, final String electionEventId) {
		return electionDataExtractionService.getTallyComponentShufflePayloads(inputDirectoryPath)
				.parallel()
				.map(TallyComponentShufflePayload::getElectionEventId)
				.allMatch(id -> id.equals(electionEventId));
	}

	private boolean areControlComponentShufflePayloadsVerified(final Path inputDirectoryPath, final String electionEventId) {
		return electionDataExtractionService.getAllControlComponentShufflePayloadsOrderedByNodeId(
						inputDirectoryPath)
				.parallel()
				.map(ControlComponentShufflePayload::getElectionEventId)
				.allMatch(id -> id.equals(electionEventId));
	}

	private boolean areControlComponentBallotBoxPayloadsVerified(final Path inputDirectoryPath, final String electionEventId) {
		return electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
						inputDirectoryPath)
				.parallel()
				.map(ControlComponentBallotBoxPayload::getElectionEventId)
				.allMatch(id -> id.equals(electionEventId));
	}

}

