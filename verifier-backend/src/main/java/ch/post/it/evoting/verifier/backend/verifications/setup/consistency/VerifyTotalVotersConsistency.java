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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component
public class VerifyTotalVotersConsistency extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;

	protected VerifyTotalVotersConsistency(final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService extractionService) {
		super(resultPublisherService);
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification312.description"));
		definition.setId(312);
		definition.setName("VerifyTotalVotersConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final Configuration configuration = extractionService.getCantonConfig(inputDirectoryPath);

		final int voterTotal = configuration.getHeader().getVoterTotal().intValueExact();
		final int voterCount = configuration.getRegister().getVoter().size();
		checkState(voterTotal == voterCount,
				"The voter total in the header must be the same as the size of the voter list. [voterTotal: %s, voterCount: %s]", voterTotal,
				voterCount);

		final List<VerificationCardSetContext> verificationCardSetContexts = extractionService.getElectionEventContextPayload(inputDirectoryPath)
				.getElectionEventContext().verificationCardSetContexts();

		boolean areAllNumberOfVotingCardsPositive = verificationCardSetContexts.stream()
				.parallel()
				.map(VerificationCardSetContext::numberOfVotingCards)
				.allMatch(numberOfVotingCards -> numberOfVotingCards >= 0);

		final int totalNumberOfVotingCards = verificationCardSetContexts.stream()
				.parallel()
				.mapToInt(VerificationCardSetContext::numberOfVotingCards)
				.reduce(0, Math::addExact);

		if (areAllNumberOfVotingCardsPositive && voterCount == totalNumberOfVotingCards) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification312.nok.message"));
		}
	}
}
