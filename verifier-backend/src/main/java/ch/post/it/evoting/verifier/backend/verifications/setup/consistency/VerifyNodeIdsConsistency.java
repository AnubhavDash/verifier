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

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.ControlComponentNode;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.dataextractors.ControlComponentCodeSharesPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.dataextractors.ControlComponentPublicKeysPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component("verifySetupNodeIdsConsistency")
public class VerifyNodeIdsConsistency extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;

	protected VerifyNodeIdsConsistency(final ResultPublisherService resultPublisherService,
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
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification314.description"));
		definition.setId("03.14");
		definition.setName("VerifyNodeIdsConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		// Check controlComponentPublicKeysPayloads
		final ImmutableList<Integer> publicKeysNodeIds = extractionService.getControlComponentPublicKeysPayloadsDataExtractions(inputDirectoryPath)
				.map(ControlComponentPublicKeysPayloadDataExtractor.DataExtraction::nodeId)
				.collect(toImmutableList());

		// Check controlComponentCodeSharesPayloads
		final Stream<ImmutableList<Integer>> codeSharesNodeIds = extractionService.getAllControlComponentCodeSharesPayloadsDataExtractions(
						inputDirectoryPath)
				.map(ControlComponentCodeSharesPayloadDataExtractor.DataExtraction::nodeIds);

		if (verifyNodeIdsConsistency(publicKeysNodeIds, codeSharesNodeIds)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification314.nok.message"));
		}
	}

	private boolean verifyNodeIdsConsistency(final ImmutableList<Integer> publicKeysNodeIds, final Stream<ImmutableList<Integer>> codeSharesNodeIds) {
		final boolean verifPublicKeysNodeIdsComplete = publicKeysNodeIds.toImmutableSet().equals(ControlComponentNode.ids());
		final boolean verifPublicKeyNodeIdsUnique = publicKeysNodeIds.size() == ControlComponentNode.ids().size();

		final boolean verifCodeSharesNodeIds = codeSharesNodeIds
				.parallel()
				.allMatch(nodeIds -> ControlComponentNode.ids().equals(nodeIds.toImmutableSet())
						&& ControlComponentNode.ids().size() == nodeIds.size());

		return verifPublicKeysNodeIdsComplete && verifPublicKeyNodeIdsUnique && verifCodeSharesNodeIds;
	}
}
