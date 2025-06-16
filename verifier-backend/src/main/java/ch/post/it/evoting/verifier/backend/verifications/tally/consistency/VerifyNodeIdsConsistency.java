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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.mapping;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.ControlComponentNode;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;

@Component("verifyTallyNodeIdsConsistency")
public class VerifyNodeIdsConsistency extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;

	public VerifyNodeIdsConsistency(final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService extractionService) {
		super(resultPublisherService);
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification802.description"));
		definition.setId("08.02");
		definition.setName("VerifyNodeIdsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		if (verifyNodeIdsConsistency(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification802.nok.message"));
		}
	}

	private boolean verifyNodeIdsConsistency(final Path inputDirectoryPath) {
		// Input.
		final Map<String, ImmutableList<Integer>> controlComponentBallotBoxNodeIdsMap = extractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
						inputDirectoryPath)
				.parallel()
				.collect(Collectors.groupingByConcurrent(ControlComponentBallotBoxPayload::getBallotBoxId,
						mapping(ControlComponentBallotBoxPayload::getNodeId, toImmutableList())));
		final Map<String, ImmutableList<Integer>> onlineControlComponentShuffleNodeIdsMap = extractionService.getAllControlComponentShufflePayloadsOrderedByNodeId(
						inputDirectoryPath)
				.parallel()
				.collect(Collectors.groupingByConcurrent(ControlComponentShufflePayload::getBallotBoxId,
						mapping(ControlComponentShufflePayload::getNodeId, toImmutableList())));

		// Cross-check.
		checkState(controlComponentBallotBoxNodeIdsMap.keySet().equals(onlineControlComponentShuffleNodeIdsMap.keySet()),
				"The ballot box ids of the control component ballot box payloads and the control component shuffle payloads must be equal.");

		// Operation.
		return controlComponentBallotBoxNodeIdsMap.keySet().stream().parallel()
				.allMatch(bb -> {
							final ImmutableList<Integer> controlComponentBallotBoxNodeIds = controlComponentBallotBoxNodeIdsMap.get(bb);
							final boolean isCompleteBallotBoxNodeIds = controlComponentBallotBoxNodeIds.toImmutableSet()
									.equals(ControlComponentNode.ids());
							final boolean isUniqueBallotBoxNodeIds = controlComponentBallotBoxNodeIds.size() == ControlComponentNode.ids().size();

							final ImmutableList<Integer> controlComponentShufflePayloads = onlineControlComponentShuffleNodeIdsMap.get(bb);
							final boolean isCompleteShuffleNodeIds = controlComponentShufflePayloads.toImmutableSet()
									.equals(ControlComponentNode.ids());
							final boolean isUniqueShuffleNodeIds = controlComponentShufflePayloads.size() == ControlComponentNode.ids().size();

							return isCompleteBallotBoxNodeIds && isUniqueBallotBoxNodeIds && isCompleteShuffleNodeIds && isUniqueShuffleNodeIds;
						}
				);
	}
}
