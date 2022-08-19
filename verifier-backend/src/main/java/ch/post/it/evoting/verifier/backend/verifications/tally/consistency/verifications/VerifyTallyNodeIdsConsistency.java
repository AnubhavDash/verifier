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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency.verifications;

import static com.google.common.collect.Sets.newHashSet;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.DecryptionEvent;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

@Component
public class VerifyTallyNodeIdsConsistency extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;
	private final HashSet<Integer> expectedNodeIds;

	public VerifyTallyNodeIdsConsistency(final ApplicationEventPublisher applicationEventPublisher,
			final ElectionDataExtractionService extractionService) {
		super(applicationEventPublisher);
		this.extractionService = extractionService;
		this.expectedNodeIds = newHashSet(1, 2, 3, 4);
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification43.description"));
		definition.setId(43);
		definition.setName(this.getClass().getSimpleName());
		definition.addVerifierEvent(DecryptionEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) {

		final List<List<ControlComponentBallotBoxPayload>> payloads = extractionService.getControlComponentBallotBoxPayloadsByBallotBox(inputDirectoryPath);

		if (isNodeIdConsistent(payloads)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification43.nok.message"));
		}
	}

	private boolean isNodeIdConsistent(final List<List<ControlComponentBallotBoxPayload>> payloads) {
		return payloads.stream()
				.allMatch(payloadsInBallotBox -> {
							final Set<Integer> currentNodeIds = payloadsInBallotBox.stream()
									.map(ControlComponentBallotBoxPayload::getNodeId)
									.collect(Collectors.toSet());

							return expectedNodeIds.containsAll(currentNodeIds) && currentNodeIds.containsAll(expectedNodeIds);
						}
				);
	}
}
