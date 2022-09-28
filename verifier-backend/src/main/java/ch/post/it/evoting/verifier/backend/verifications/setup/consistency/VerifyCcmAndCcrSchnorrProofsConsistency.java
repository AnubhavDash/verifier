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

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.election.ControlComponentPublicKeys;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.ZqGroup;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.SchnorrProof;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.configuration.ControlComponentPublicKeysPayload;

@Component
public class VerifyCcmAndCcrSchnorrProofsConsistency extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;

	protected VerifyCcmAndCcrSchnorrProofsConsistency(final ElectionDataExtractionService extractionService,
			final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification313.description"));
		definition.setId(313);
		definition.setName("VerifyCcmAndCcrSchnorrProofsConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) {
		final ElectionEventContextPayload electionEventContextPayload = extractionService.getElectionEventContextPayload(inputDirectoryPath);
		final List<ControlComponentPublicKeysPayload> controlComponentPublicKeysPayloads = extractionService.getControlComponentPublicKeysPayloads(
				inputDirectoryPath);

		final Map<Integer, GroupVector<SchnorrProof, ZqGroup>> electionEventCcmjSchnorrProofs = electionEventContextPayload.getElectionEventContext()
				.combinedControlComponentPublicKeys().stream()
				.collect(Collectors.toMap(ControlComponentPublicKeys::nodeId, ControlComponentPublicKeys::ccmjSchnorrProofs));

		final boolean sameCcmjSchnorrProofs = controlComponentPublicKeysPayloads.stream()
				.map(ControlComponentPublicKeysPayload::getControlComponentPublicKeys)
				.map(controlComponentPublicKeys -> electionEventCcmjSchnorrProofs.get(controlComponentPublicKeys.nodeId())
						.equals(controlComponentPublicKeys.ccmjSchnorrProofs()))
				.reduce(Boolean::logicalAnd)
				.orElseThrow();

		final Map<Integer, GroupVector<SchnorrProof, ZqGroup>> electionEventCcrjSchnorrProofs = electionEventContextPayload.getElectionEventContext()
				.combinedControlComponentPublicKeys().stream()
				.collect(Collectors.toMap(ControlComponentPublicKeys::nodeId, ControlComponentPublicKeys::ccrjSchnorrProofs));

		final boolean sameCcrjSchnorrProofs = controlComponentPublicKeysPayloads.stream()
				.map(ControlComponentPublicKeysPayload::getControlComponentPublicKeys)
				.map(controlComponentPublicKeys -> electionEventCcrjSchnorrProofs.get(controlComponentPublicKeys.nodeId())
						.equals(controlComponentPublicKeys.ccrjSchnorrProofs()))
				.reduce(Boolean::logicalAnd)
				.orElseThrow();

		if (sameCcmjSchnorrProofs && sameCcrjSchnorrProofs) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification313.nok.message"));
		}
	}
}
