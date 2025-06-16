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

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap.toImmutableMap;
import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet.toImmutableSet;

import java.nio.file.Path;
import java.util.Objects;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet;
import ch.post.it.evoting.evotinglibraries.domain.common.EncryptedVerifiableVote;
import ch.post.it.evoting.evotinglibraries.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
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

@Component("verifyTallyVerificationCardIdsConsistency")
public class VerifyVerificationCardIdsConsistency extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;

	public VerifyVerificationCardIdsConsistency(final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService extractionService) {
		super(resultPublisherService);
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification807.description"));
		definition.setId("08.07");
		definition.setName("VerifyVerificationCardIdsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		if (verifyVerificationCardIdsConsistency(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification807.nok.message"));
		}
	}

	private boolean verifyVerificationCardIdsConsistency(final Path inputDirectoryPath) {
		// Input.
		final ImmutableMap<String, ImmutableSet<String>> setupComponentTallyDataVerificationCardIdsMap = extractionService.getSetupComponentTallyDataPayloads(
						inputDirectoryPath)
				.collect(toImmutableMap(
						SetupComponentTallyDataPayload::getVerificationCardSetId,
						setupComponentTallyDataPayload -> setupComponentTallyDataPayload.getVerificationCardIds().stream().collect(toImmutableSet())
				));
		final ImmutableMap<String, String> electionEventContextBallotBoxIdMap = extractionService.getElectionEventContext(inputDirectoryPath)
				.verificationCardSetContexts()
				.stream()
				.collect(toImmutableMap(
						VerificationCardSetContext::getVerificationCardSetId,
						VerificationCardSetContext::getBallotBoxId
				));

		// Operation.
		return extractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(inputDirectoryPath)
				.parallel()
				.allMatch(controlComponentBallotBox -> controlComponentBallotBox.getConfirmedEncryptedVotes().stream()
						.parallel()
						.map(EncryptedVerifiableVote::contextIds)
						.allMatch(contextIds -> {
							final String electionEventContextBallotBoxId = electionEventContextBallotBoxIdMap.get(contextIds.verificationCardSetId());
							final boolean isVerificationCardSetRelatedToBallotBox = Objects.nonNull(electionEventContextBallotBoxId) &&
									controlComponentBallotBox.getBallotBoxId().equals(electionEventContextBallotBoxId);

							final ImmutableSet<String> setupComponentTallyDataVerificationCardIds = setupComponentTallyDataVerificationCardIdsMap.get(
									contextIds.verificationCardSetId());
							final String controlComponentBallotBoxVerificationCardId = contextIds.verificationCardId();
							final boolean isVerificationCardIdInSetupComponentTallyData =
									Objects.nonNull(setupComponentTallyDataVerificationCardIds) && setupComponentTallyDataVerificationCardIds
											.contains(controlComponentBallotBoxVerificationCardId);

							return isVerificationCardSetRelatedToBallotBox && isVerificationCardIdInSetupComponentTallyData;
						}));
	}
}
