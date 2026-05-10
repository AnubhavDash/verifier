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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import java.nio.file.Path;
import java.util.function.BiFunction;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.evotinglibraries.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
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
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification304.description"));
		definition.setId("03.04");
		definition.setName("VerifyElectionEventIdConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		if (verifyElectionEventIdConsistency(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification304.nok.message"));
		}
	}

	private boolean verifyElectionEventIdConsistency(final Path inputDirectoryPath) {
		// Input.
		final String electionEventContextElectionEventId = electionDataExtractionService.getElectionEventContext(inputDirectoryPath)
				.electionEventId();

		// Operation.
		final ImmutableList<BiFunction<Path, String, Boolean>> validations = ImmutableList.of(
				this::validateControlComponentPublicKeys,
				this::validateSetupComponentTallyData);

		return validations.stream()
				.parallel()
				.map(f -> f.apply(inputDirectoryPath, electionEventContextElectionEventId))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	private boolean validateControlComponentPublicKeys(final Path inputDirectoryPath, final String electionEventContextElectionEventId) {
		return electionDataExtractionService.getControlComponentPublicKeysPayloads(inputDirectoryPath)
				.map(ControlComponentPublicKeysPayload::getElectionEventId)
				.distinct()
				.allMatch(electionEventContextElectionEventId::equals);
	}

	private boolean validateSetupComponentTallyData(final Path inputDirectoryPath, final String electionEventContextElectionEventId) {
		return electionDataExtractionService.getSetupComponentTallyDataPayloads(inputDirectoryPath)
				.map(SetupComponentTallyDataPayload::getElectionEventId)
				.distinct()
				.allMatch(electionEventContextElectionEventId::equals);
	}
}

