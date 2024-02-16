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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static ch.post.it.evoting.verifier.backend.tools.TranslationHelper.getFromResourceBundle;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.post.it.evoting.evotinglibraries.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.SetupComponentPublicKeysPayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingdecrypt.Results;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;

@Component
public class VerifyTallyControlComponent extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;
	private final VerifyTallyControlComponentAlgorithm verifyTallyControlComponentAlgorithm;

	protected VerifyTallyControlComponent(final ElectionDataExtractionService extractionService,
			final VerifyTallyControlComponentAlgorithm verifyTallyControlComponentAlgorithm,
			final ResultPublisherService resultPublisherService) {
		super(resultPublisherService);
		this.extractionService = extractionService;
		this.verifyTallyControlComponentAlgorithm = verifyTallyControlComponentAlgorithm;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.EVIDENCE);
		definition.setDescription(getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification1002.description"));
		definition.setId("10.02");
		definition.setName("VerifyTallyControlComponent");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	@SuppressWarnings("java:S117")
	public VerificationResult verify(final Path inputDirectoryPath) {
		final ElectionEventContextPayload electionEventContextPayload = extractionService.getElectionEventContextPayload(inputDirectoryPath);
		final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload = extractionService.getSetupComponentPublicKeysPayload(
				inputDirectoryPath);
		final VerifyTallyControlComponentContext context = new VerifyTallyControlComponentContext(electionEventContextPayload,
				setupComponentPublicKeysPayload);

		final Stream<ControlComponentShufflePayload> controlComponentShufflePayloads = extractionService.getAllControlComponentShufflePayloadsOrderedByNodeId(
				inputDirectoryPath);
		final Stream<TallyComponentShufflePayload> tallyComponentShufflePayloads = extractionService.getTallyComponentShufflePayloads(
				inputDirectoryPath);
		final Map<String, TallyComponentVotesPayload> tallyComponentVotesPayloads = getAuthorizationNameToTallyComponentVotesPayloadMap(
				inputDirectoryPath, electionEventContextPayload);
		final Configuration electionEventConfiguration = extractionService.getCantonConfig(inputDirectoryPath);
		final Results tallyControlComponentDecryptions = extractionService.getTallyComponentDecrypt(inputDirectoryPath);
		final Delivery tallyControlComponentResults = extractionService.getTallyComponentEch0110(inputDirectoryPath);
		final ch.ech.xmlns.ech_0222._1.Delivery tallyControlComponentDetailedResults = extractionService.getTallyComponentEch0222(inputDirectoryPath);
		final VerifyTallyControlComponentInput input = new VerifyTallyControlComponentInput(controlComponentShufflePayloads,
				tallyComponentShufflePayloads, tallyComponentVotesPayloads, electionEventConfiguration,
				tallyControlComponentDecryptions, tallyControlComponentResults, tallyControlComponentDetailedResults);

		final boolean result = verifyTallyControlComponentAlgorithm.verifyTallyControlComponent(context, input);
		if (result) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification1002.nok.message"));
		}
	}

	/**
	 * Extract the {@link SetupComponentTallyDataPayload} and {@link TallyComponentVotesPayload} to create a mapping between the ballot box default
	 * title and the {@link TallyComponentVotesPayload}.
	 */
	private Map<String, TallyComponentVotesPayload> getAuthorizationNameToTallyComponentVotesPayloadMap(final Path inputDirectoryPath,
			final ElectionEventContextPayload electionEventContextPayload) {

		record TallyComponentVotesTuple(String authorizationAlias, TallyComponentVotesPayload payload) {
		}

		return electionEventContextPayload.getElectionEventContext().verificationCardSetContexts().stream()
				.map(verificationCardSetContext -> {
					final String ballotBoxId = verificationCardSetContext.getBallotBoxId();
					final String verificationCardSetId = verificationCardSetContext.getVerificationCardSetId();

					final SetupComponentTallyDataPayload setupComponentTallyDataPayload = extractionService.getSetupComponentTallyDataPayload(
							inputDirectoryPath, verificationCardSetId);
					final String ballotBoxDefaultTitle = setupComponentTallyDataPayload.getBallotBoxDefaultTitle();

					final TallyComponentVotesPayload tallyComponentVotesPayload = extractionService.getTallyComponentVotesPayload(
							inputDirectoryPath, ballotBoxId);

					return new TallyComponentVotesTuple(ballotBoxDefaultTitle, tallyComponentVotesPayload);
				})
				.collect(Collectors.toMap(TallyComponentVotesTuple::authorizationAlias, TallyComponentVotesTuple::payload));
	}
}
