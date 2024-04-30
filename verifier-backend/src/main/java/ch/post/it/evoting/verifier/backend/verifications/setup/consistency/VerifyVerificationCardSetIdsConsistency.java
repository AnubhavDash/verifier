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

import static com.google.common.base.Preconditions.checkArgument;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.dataextractors.ControlComponentCodeSharesPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.dataextractors.SetupComponentTallyDataPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.dataextractors.SetupComponentVerificationDataPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

/**
 * This verification ensures that the verification card set IDs in all files in the same directory and with the name of the parent folder are
 * consistent.
 */
@Component
public class VerifyVerificationCardSetIdsConsistency extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;

	public VerifyVerificationCardSetIdsConsistency(final ResultPublisherService resultPublisherService,
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
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification310.description"));
		definition.setId("03.10");
		definition.setName("VerifyVerificationCardSetIdsConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final boolean sameVerificationCardSetIds = extractVerificationCardSetIds(inputDirectoryPath).stream()
				.parallel()
				.map(payloadsVerificationCardSetIds ->
						payloadsVerificationCardSetIds.verificationCardSetId().equals(payloadsVerificationCardSetIds.verificationDataIds())
								&& payloadsVerificationCardSetIds.verificationCardSetId().equals(payloadsVerificationCardSetIds.codeShareIds())
								&& payloadsVerificationCardSetIds.verificationCardSetId().equals(payloadsVerificationCardSetIds.tallyIds()))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (sameVerificationCardSetIds) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification310.nok.message"));
		}
	}

	private List<PayloadsVerificationCardSetIds> extractVerificationCardSetIds(final Path inputDirectoryPath) {

		final List<Path> contextVerificationCardSetPaths = electionDataExtractionService.getContextVerificationCardSetPaths(inputDirectoryPath);

		return electionDataExtractionService.getSetupVerificationCardSetPaths(inputDirectoryPath).stream()
				.parallel()
				.map(verificationCardSetIdPath -> {

					final String vcsId = verificationCardSetIdPath.getFileName().toString();
					final Set<String> verificationCardSetId = Set.of(vcsId);

					final Set<String> verificationDataIds = electionDataExtractionService.getSetupComponentVerificationDataPayloadsDataExtractionsSortedByChunkId(
									verificationCardSetIdPath)
							.map(SetupComponentVerificationDataPayloadDataExtractor.DataExtraction::verificationCardSetId)
							.collect(Collectors.toUnmodifiableSet());

					checkArgument(verificationDataIds.size() == 1, "The setup component verification card set id size must be one.");

					final Set<String> codeShareIds = electionDataExtractionService.getControlComponentCodeSharesPayloadsDataExtractions(
									verificationCardSetIdPath)
							.map(ControlComponentCodeSharesPayloadDataExtractor.DataExtraction::verificationCardSetIds)
							.flatMap(Collection::stream)
							.collect(Collectors.toUnmodifiableSet());

					checkArgument(codeShareIds.size() == 1, "The control component code shares verification card set id size must be one.");

					final Set<String> tallyIds = contextVerificationCardSetPaths.stream()
							.parallel()
							.filter(vcsPath -> vcsPath.getFileName().toString().equals(vcsId))
							.flatMap(electionDataExtractionService::getSetupComponentTallyDataPayloadsDataExtractions)
							.map(SetupComponentTallyDataPayloadDataExtractor.DataExtraction::verificationCardSetId)
							.collect(Collectors.toUnmodifiableSet());

					checkArgument(tallyIds.size() == 1, "The setup component tally verification card set id size must be one.");

					return new PayloadsVerificationCardSetIds(verificationCardSetId, verificationDataIds, codeShareIds, tallyIds);

				})
				.toList();
	}

	private record PayloadsVerificationCardSetIds(Set<String> verificationCardSetId, Set<String> verificationDataIds, Set<String> codeShareIds,
												  Set<String> tallyIds) {
	}

}

