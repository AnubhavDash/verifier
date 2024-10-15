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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import static ch.post.it.evoting.evotinglibraries.domain.ControlComponentConstants.NODE_IDS;
import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

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

@Component("VerifyTallyNodeIdsConsistency")
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
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification809.description"));
		definition.setId("08.09");
		definition.setName("VerifyNodeIdsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final Map<String, List<ControlComponentBallotBoxPayload>> ballotBoxPayloads = extractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
						inputDirectoryPath)
				.parallel()
				.collect(Collectors.groupingByConcurrent(ControlComponentBallotBoxPayload::getBallotBoxId, Collectors.toUnmodifiableList()));
		final Map<String, List<ControlComponentShufflePayload>> shufflePayloads = extractionService.getAllControlComponentShufflePayloadsOrderedByNodeId(
						inputDirectoryPath)
				.parallel()
				.collect(Collectors.groupingByConcurrent(ControlComponentShufflePayload::getBallotBoxId, Collectors.toUnmodifiableList()));

		if (isNodeIdConsistent(ballotBoxPayloads, shufflePayloads)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification809.nok.message"));
		}
	}

	private boolean isNodeIdConsistent(final Map<String, List<ControlComponentBallotBoxPayload>> ballotBoxPayloads,
			final Map<String, List<ControlComponentShufflePayload>> shufflePayloads) {
		checkState(ballotBoxPayloads.keySet().equals(shufflePayloads.keySet()),
				"The ballot box Ids of the ControlComponentPayloads and the shufflePayloads must be equal.");

		final Set<String> ballotBoxIds = ballotBoxPayloads.keySet();

		return ballotBoxIds.stream()
				.parallel()
				.allMatch(bbId -> {
							final List<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloads = ballotBoxPayloads.get(bbId);
							final List<Integer> ballotBoxPayloadsNodeIdList = controlComponentBallotBoxPayloads.stream()
									.parallel()
									.map(ControlComponentBallotBoxPayload::getNodeId)
									.toList();
							final Set<Integer> ballotBoxPayloadsNodeIds = Set.copyOf(ballotBoxPayloadsNodeIdList);
							final boolean bbPayloadNodeIdsConsistent =
									ballotBoxPayloadsNodeIdList.size() == ballotBoxPayloadsNodeIds.size() && ballotBoxPayloadsNodeIds.containsAll(NODE_IDS)
											&& NODE_IDS.containsAll(ballotBoxPayloadsNodeIds);

							final List<ControlComponentShufflePayload> controlComponentShufflePayloads = shufflePayloads.get(bbId);
							final List<Integer> shufflePaylodsNodeIdList = controlComponentShufflePayloads.stream()
									.parallel()
									.map(ControlComponentShufflePayload::getNodeId)
									.toList();
							final Set<Integer> shufflePayloadsNodeIds = Set.copyOf(shufflePaylodsNodeIdList);
							final boolean shufflePayloadsNodeIdsConsistent =
									shufflePaylodsNodeIdList.size() == shufflePayloadsNodeIds.size() && shufflePayloadsNodeIds.containsAll(NODE_IDS)
											&& NODE_IDS.containsAll(shufflePayloadsNodeIds);

							return bbPayloadNodeIdsConsistent && shufflePayloadsNodeIdsConsistent;
						}
				);
	}
}
