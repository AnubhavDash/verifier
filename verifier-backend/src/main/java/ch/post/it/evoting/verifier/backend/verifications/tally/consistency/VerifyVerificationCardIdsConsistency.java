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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
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
import ch.post.it.evoting.verifier.protocol.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

@Component("VerifyTallyVerificationCardIdsConsistency")
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
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification303.description"));
		definition.setId("8.04");
		definition.setName("VerifyVerificationCardIdsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final List<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloads = extractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
				inputDirectoryPath).toList();

		final Stream<SetupComponentTallyDataPayload> setupComponentTallyDataPayloads = extractionService.getSetupComponentTallyDataPayloads(
				inputDirectoryPath);
		final ElectionEventContextPayload electionEventContextPayload = extractionService.getElectionEventContextPayload(inputDirectoryPath);

		if (verifyVerificationCardSetRelationToBallotBox(controlComponentBallotBoxPayloads, electionEventContextPayload) &&
				verifyVerificationCardIdsInExpectedSet(controlComponentBallotBoxPayloads, setupComponentTallyDataPayloads)) {

			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification303.nok.message"));
		}
	}

	@VisibleForTesting
	boolean verifyVerificationCardSetRelationToBallotBox(final List<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloads,
			final ElectionEventContextPayload electionEventContextPayload) {
		final Map<String, String> verificationCardSetIdToBallotBoxId = electionEventContextPayload
				.getElectionEventContext()
				.verificationCardSetContexts().stream()
				.parallel()
				.collect(Collectors.toConcurrentMap(VerificationCardSetContext::verificationCardSetId, VerificationCardSetContext::ballotBoxId));

		return controlComponentBallotBoxPayloads.stream()
				.parallel()
				.allMatch(payload -> payload.getConfirmedEncryptedVotes().stream()
						.parallel()
						.allMatch(encryptedVerifiableVote -> !payload.getBallotBoxId().isEmpty() && payload.getBallotBoxId()
								.equals(verificationCardSetIdToBallotBoxId.get(encryptedVerifiableVote.contextIds().verificationCardSetId()))));
	}

	@VisibleForTesting
	boolean verifyVerificationCardIdsInExpectedSet(final List<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloadsByBallotBox,
			final Stream<SetupComponentTallyDataPayload> setupComponentTallyDataPayloads) {
		final Map<String, Set<String>> verificationCardSetIdToVerificationCardIds = setupComponentTallyDataPayloads
				.parallel()
				.collect(Collectors.toConcurrentMap(SetupComponentTallyDataPayload::getVerificationCardSetId,
						setupComponentTallyDataPayload -> Set.copyOf(setupComponentTallyDataPayload.getVerificationCardIds())));

		return controlComponentBallotBoxPayloadsByBallotBox.stream()
				.parallel()
				.flatMap(payload -> payload.getConfirmedEncryptedVotes().stream())
				.allMatch(encryptedVerifiableVote -> {
					final Set<String> expectedVerificationCardIds = verificationCardSetIdToVerificationCardIds.get(
							encryptedVerifiableVote.contextIds().verificationCardSetId());
					return expectedVerificationCardIds.contains(encryptedVerifiableVote.contextIds().verificationCardId());
				});
	}
}
