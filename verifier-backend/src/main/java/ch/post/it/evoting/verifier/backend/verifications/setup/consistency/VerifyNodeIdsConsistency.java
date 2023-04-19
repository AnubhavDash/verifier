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

import static ch.post.it.evoting.cryptoprimitives.domain.ControlComponentConstants.NODE_IDS;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.election.ControlComponentPublicKeys;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ControlComponentCodeSharesPayload;
import ch.post.it.evoting.evotinglibraries.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component("VerifySetupNodeIdsConsistency")
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
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification313.description"));
		definition.setId("03.14");
		definition.setName("VerifyNodeIdsConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		// Check controlComponentPublicKeysPayloads
		final List<Integer> publicKeysNodeIds = extractionService.getControlComponentPublicKeysPayloads(inputDirectoryPath).stream()
				.parallel()
				.map(ControlComponentPublicKeysPayload::getControlComponentPublicKeys)
				.map(ControlComponentPublicKeys::nodeId)
				.toList();

		// Check controlComponentCodeSharesPayloads
		final Stream<List<Integer>> codeSharesNodeIds = extractionService.getControlComponentCodeSharesPayloadsByChunkAndVcs(inputDirectoryPath)
				.parallel()
				.map(payloadList -> payloadList
						.parallel()
						.map(ControlComponentCodeSharesPayload::getNodeId)
						.toList());

		if (verifyNodeIdsConsistency(publicKeysNodeIds, codeSharesNodeIds)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification313.nok.message"));
		}
	}

	private boolean verifyNodeIdsConsistency(final List<Integer> publicKeysNodeIds, final Stream<List<Integer>> codeSharesNodeIds) {
		final boolean verifPublicKeysNodeIdsComplete = Set.copyOf(publicKeysNodeIds).equals(NODE_IDS);
		final boolean verifPublicKeyNodeIdsUnique = publicKeysNodeIds.size() == NODE_IDS.size();

		final boolean verifCodeSharesNodeIds = codeSharesNodeIds
				.parallel()
				.allMatch(nodeIds -> NODE_IDS.equals(Set.copyOf(nodeIds)) && NODE_IDS.size() == nodeIds.size());

		return verifPublicKeysNodeIdsComplete && verifPublicKeyNodeIdsUnique && verifCodeSharesNodeIds;
	}
}
