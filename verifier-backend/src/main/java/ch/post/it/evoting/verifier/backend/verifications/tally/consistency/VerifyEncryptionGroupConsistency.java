/*
 * (c) Copyright 2024 Swiss Post Ltd.
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
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
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
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;

@Component("verifyTallyEncryptionGroupConsistency")
public class VerifyEncryptionGroupConsistency extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;

	public VerifyEncryptionGroupConsistency(
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
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification811.description"));
		definition.setId("08.11");
		definition.setName("VerifyEncryptionGroupConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final GqGroup electionEventContextPayloadEncryptionGroup = electionDataExtractionService.getElectionEventContextPayload(inputDirectoryPath)
				.getEncryptionGroup();

		final ImmutableList<BiFunction<Path, GqGroup, Boolean>> validations = ImmutableList.of(
				this::validateControlComponentBallotBoxPayloads,
				this::validateControlComponentShufflePayloads,
				this::validateTallyComponentShufflePayloads,
				this::validateTallyComponentVotesPayloads
		);

		final boolean sameGroupParameters = validations.stream()
				.parallel()
				.map(f -> f.apply(inputDirectoryPath, electionEventContextPayloadEncryptionGroup))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (sameGroupParameters) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification811.nok.message"));
		}
	}

	private boolean validateControlComponentBallotBoxPayloads(final Path inputDirectoryPath,
			final GqGroup electionEventContextPayloadEncryptionGroup) {
		return electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(inputDirectoryPath)
				.map(ControlComponentBallotBoxPayload::getEncryptionGroup)
				.distinct()
				.allMatch(electionEventContextPayloadEncryptionGroup::equals);
	}

	private boolean validateControlComponentShufflePayloads(final Path inputDirectoryPath,
			final GqGroup electionEventContextPayloadEncryptionGroup) {
		return electionDataExtractionService.getAllControlComponentShufflePayloadsOrderedByNodeId(inputDirectoryPath)
				.map(ControlComponentShufflePayload::getEncryptionGroup)
				.distinct()
				.allMatch(electionEventContextPayloadEncryptionGroup::equals);
	}

	private boolean validateTallyComponentShufflePayloads(final Path inputDirectoryPath,
			final GqGroup electionEventContextPayloadEncryptionGroup) {
		return electionDataExtractionService.getTallyComponentShufflePayloads(inputDirectoryPath)
				.map(TallyComponentShufflePayload::getEncryptionGroup)
				.distinct()
				.allMatch(electionEventContextPayloadEncryptionGroup::equals);
	}

	private boolean validateTallyComponentVotesPayloads(final Path inputDirectoryPath,
			final GqGroup electionEventContextPayloadEncryptionGroup) {
		return electionDataExtractionService.getTallyComponentVotesPayloads(inputDirectoryPath)
				.map(TallyComponentVotesPayload::getEncryptionGroup)
				.distinct()
				.allMatch(electionEventContextPayloadEncryptionGroup::equals);
	}
}
