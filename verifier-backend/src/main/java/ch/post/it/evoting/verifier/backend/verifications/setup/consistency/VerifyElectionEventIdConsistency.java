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

import java.nio.file.Path;
import java.util.function.BiFunction;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.dataextractors.ControlComponentPublicKeysPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.dataextractors.SetupComponentTallyDataPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

/**
 * This verification ensures that the election event IDs in the dataset are consistent to the election event ID present in the file
 * electionEventContextPayload.json.
 */
@Component("verifySetupElectionEventIdConsistency")
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
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification309.description"));
		definition.setId("03.09");
		definition.setName("VerifyElectionEventIdConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final String electionEventId = electionDataExtractionService.getElectionEventContextPayloadDataExtraction(inputDirectoryPath)
				.electionEventId();

		final ImmutableList<BiFunction<Path, String, Boolean>> validations = ImmutableList.of(
				this::validateSetupComponentTallyDataPayload,
				this::validateControlComponentPublicKeysPayload);

		final boolean sameElectionEventId = validations.stream()
				.parallel()
				.map(f -> f.apply(inputDirectoryPath, electionEventId))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (sameElectionEventId) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification309.nok.message"));
		}
	}

	private boolean validateSetupComponentTallyDataPayload(final Path inputDirectoryPath, final String electionEventId) {
		return electionDataExtractionService.getAllSetupComponentTallyDataPayloadsDataExtractions(inputDirectoryPath)
				.map(SetupComponentTallyDataPayloadDataExtractor.DataExtraction::electionEventId)
				.distinct()
				.allMatch(electionEventId::equals);
	}

	private boolean validateControlComponentPublicKeysPayload(final Path inputDirectoryPath, final String electionEventId) {
		return electionDataExtractionService.getControlComponentPublicKeysPayloadsDataExtractions(inputDirectoryPath)
				.map(ControlComponentPublicKeysPayloadDataExtractor.DataExtraction::electionEventId)
				.distinct()
				.allMatch(electionEventId::equals);
	}
}

