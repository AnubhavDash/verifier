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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static ch.post.it.evoting.verifier.backend.tools.TranslationHelper.getFromResourceBundle;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.verifier.protocol.domain.tally.TallyComponentVotesPayload;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.Results;

@Component
public class VerifyTallyControlComponent extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;
	private final VerifyTallyControlComponentAlgorithm verifyTallyControlComponentAlgorithm;

	protected VerifyTallyControlComponent(final ElectionDataExtractionService extractionService,
			final VerifyTallyControlComponentAlgorithm verifyTallyControlComponentAlgorithm,
			final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.extractionService = extractionService;
		this.verifyTallyControlComponentAlgorithm = verifyTallyControlComponentAlgorithm;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.EVIDENCE);
		definition.setDescription(getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification501.description"));
		definition.setId(501);
		definition.setName("VerifyTallyControlComponent");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	@SuppressWarnings("java:S117")
	public VerificationResult verify(Path inputDirectoryPath) {
		final ElectionEventContextPayload electionEventContextPayload = extractionService.getElectionEventContextPayload(inputDirectoryPath);
		final List<ControlComponentShufflePayload> controlComponentShufflePayloads = extractionService
				.getAllControlComponentShufflePayloadsOrderedByNodeId(inputDirectoryPath);
		final List<TallyComponentShufflePayload> tallyComponentShufflePayloads = extractionService
				.getTallyComponentShufflePayloads(inputDirectoryPath);
		final Map<String, TallyComponentVotesPayload> tallyComponentVotesPayloads = getAuthorizationAliasToTallyComponentVotesPayloadMap(
				inputDirectoryPath, electionEventContextPayload.getElectionEventContext());
		final Configuration electionEventConfiguration = extractionService.getSetupComponentConfig(inputDirectoryPath);
		final Results tallyControlComponentDecryptions = extractionService.getTallyComponentDecrypt(inputDirectoryPath);
		final Delivery tallyControlComponentResults = extractionService.getTallyComponentEch0110(inputDirectoryPath);

		final VerifyTallyControlComponentInput input = new VerifyTallyControlComponentInput(electionEventContextPayload,
				controlComponentShufflePayloads, tallyComponentShufflePayloads, tallyComponentVotesPayloads, electionEventConfiguration,
				tallyControlComponentDecryptions, tallyControlComponentResults);

		final Map<String, Integer> numberOfSelectableVotingOptions = electionEventContextPayload.getElectionEventContext()
				.verificationCardSetContexts().stream()
				.collect(Collectors.toMap(VerificationCardSetContext::ballotBoxId,
						vcContext -> extractionService
								.getCombinedCorrectnessInformation(inputDirectoryPath, vcContext.verificationCardSetId())
								.getTotalNumberOfSelections()));

		final boolean result = verifyTallyControlComponentAlgorithm.verifyTallyControlComponent(input, numberOfSelectableVotingOptions);

		if (result) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification501.nok.message"));
		}
	}

	private Map<String, TallyComponentVotesPayload> getAuthorizationAliasToTallyComponentVotesPayloadMap(final Path inputDirectoryPath,
			final ElectionEventContext electionEventContext) {

		record TallyComponentVotesTuple(String authorizationAlias, TallyComponentVotesPayload payload) {
		}

		return electionEventContext.verificationCardSetContexts().stream()
				.map(verificationCardSetContext -> {
					final String ballotBoxId = verificationCardSetContext.ballotBoxId();
					final String verificationCardSetId = verificationCardSetContext.verificationCardSetId();

					final SetupComponentTallyDataPayload setupComponentTallyDataPayload = extractionService.getSetupComponentTallyDataPayload(
							inputDirectoryPath, verificationCardSetId);
					final String ballotBoxAlias = setupComponentTallyDataPayload.getBallotBoxAlias();

					final TallyComponentVotesPayload tallyComponentVotesPayload = extractionService.getTallyComponentVotesPayload(
							inputDirectoryPath, ballotBoxId);

					return new TallyComponentVotesTuple(ballotBoxAlias, tallyComponentVotesPayload);
				})
				.collect(Collectors.toMap(TallyComponentVotesTuple::authorizationAlias, TallyComponentVotesTuple::payload));
	}
}
