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
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.google.common.collect.Streams;

import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.domain.EncryptionGroupParameters;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.EncryptionGroupParametersExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;

@Component("VerifyTallyEncryptionGroupConsistency")
public class VerifyEncryptionGroupConsistency extends AbstractVerification {

	private final EncryptionGroupParametersExtractionService extractionService;

	public VerifyEncryptionGroupConsistency(final ResultPublisherService resultPublisherService,
			final EncryptionGroupParametersExtractionService extractionService) {
		super(resultPublisherService);
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification310.description"));
		definition.setId("08.11");
		definition.setName("VerifyEncryptionGroupConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final EncryptionGroupParameters encryptionGroupParameters = extractionService.getFromElectionEventContext(inputDirectoryPath);

		final Stream<EncryptionGroupParameters> allGroupParameters = Streams.concat(
				extractionService.getFromControlComponentBallotBoxPayloads(inputDirectoryPath),
				extractionService.getFromControlComponentShufflePayloads(inputDirectoryPath),
				extractionService.getFromTallyComponentShufflePayloads(inputDirectoryPath),
				extractionService.getFromTallyComponentVotesPayloads(inputDirectoryPath)
		);

		if (allGroupParameters.parallel().allMatch(group -> group.equals(encryptionGroupParameters))) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification310.nok.message"));
		}
	}
}
