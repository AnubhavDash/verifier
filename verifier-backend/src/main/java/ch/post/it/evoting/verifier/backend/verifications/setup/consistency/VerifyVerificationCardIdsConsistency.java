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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.hasNoDuplicates;
import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component("verifySetupVerificationCardIdsConsistency")
public class VerifyVerificationCardIdsConsistency extends AbstractVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyVerificationCardIdsConsistency.class);

	private final ElectionDataExtractionService electionDataExtractionService;

	protected VerifyVerificationCardIdsConsistency(
			final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService electionDataExtractionService) {
		super(resultPublisherService);
		this.electionDataExtractionService = electionDataExtractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification312.description"));
		definition.setId("03.12");
		definition.setName("VerifyVerificationCardIdsConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final boolean verificationCardIdsConsistent = extractVerificationCardIds(inputDirectoryPath).stream()
				.parallel()
				.map(this::verifyConsistency)
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (verificationCardIdsConsistent) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification312.nok.message"));
		}
	}

	private ImmutableList<PayloadsVerificationCardIds> extractVerificationCardIds(final Path inputDirectoryPath) {
		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(
				inputDirectoryPath);
		final ImmutableList<VerificationCardSetContext> verificationCardSetContexts = electionEventContextPayload.getElectionEventContext()
				.verificationCardSetContexts();

		return electionDataExtractionService.getContextVerificationCardSetPaths(inputDirectoryPath).stream()
				.parallel()
				.map(verificationCardSetIdPath -> {

					final String verificationCardSetId = verificationCardSetIdPath.getFileName().toString();

					final ImmutableList<String> tallyDataIds = electionDataExtractionService.getSetupComponentTallyDataPayloadsDataExtractions(
									verificationCardSetIdPath)
							.map(dataExtraction -> {
								final ImmutableList<String> verificationCardIds = Arrays.stream(dataExtraction.verificationCardIds())
										.collect(ImmutableList.toImmutableList());

								checkState(hasNoDuplicates(verificationCardIds));
								return verificationCardIds;
							})
							.flatMap(ImmutableList::stream)
							.collect(toImmutableList());

					final int numberOfEligibleVoters = verificationCardSetContexts.stream()
							.parallel()
							.filter(vcs -> vcs.getVerificationCardSetId().equals(verificationCardSetId))
							.collect(MoreCollectors.onlyElement())
							.getNumberOfEligibleVoters();

					return new PayloadsVerificationCardIds(tallyDataIds, numberOfEligibleVoters);
				})
				.collect(toImmutableList());
	}

	/**
	 * Verifies:
	 * <ul>
	 *     <li>the verification card ids in the setup component tally data payload do not have duplicates.</li>
	 *     <li>the number of verification card ids in the setup component tally data payload is equal to the {@code numberOfEligibleVoters}.</li>
	 * </ul>
	 */
	private boolean verifyConsistency(final PayloadsVerificationCardIds payloadsVerificationCardIds) {
		final ImmutableList<String> tallyDataIds = payloadsVerificationCardIds.tallyDataIds;
		final int numberOfEligibleVoters = payloadsVerificationCardIds.numberOfEligibleVoters;

		// The SetupComponentTallyData payload ensures no verification card id are duplicates.

		return tallyDataIds.size() == numberOfEligibleVoters;
	}

	private record PayloadsVerificationCardIds(ImmutableList<String> tallyDataIds, int numberOfEligibleVoters) {
	}

}
