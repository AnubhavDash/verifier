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

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap.toImmutableMap;

import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.ZqGroup;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.SchnorrProof;
import ch.post.it.evoting.evotinglibraries.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.evotinglibraries.domain.election.ControlComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.SetupComponentPublicKeysPayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component
public class VerifyCCMAndCCRSchnorrProofsConsistency extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;

	protected VerifyCCMAndCCRSchnorrProofsConsistency(final ElectionDataExtractionService extractionService,
			final ResultPublisherService resultPublisherService) {
		super(resultPublisherService);
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification310.description"));
		definition.setId("03.10");
		definition.setName("VerifyCcmAndCcrSchnorrProofsConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		if (verifyCCMAndCCRSchnorrProofsConsistency(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification310.nok.message"));
		}
	}

	private boolean verifyCCMAndCCRSchnorrProofsConsistency(final Path inputDirectoryPath) {

		// Input.
		final ImmutableList<ControlComponentPublicKeysPayload> onlineControlComponentPublicKeys = extractionService.getControlComponentPublicKeysPayloads(
				inputDirectoryPath).collect(toImmutableList());
		final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload = extractionService.getSetupComponentPublicKeysPayload(
				inputDirectoryPath);

		// Operation.
		final Stream<BiFunction<SetupComponentPublicKeysPayload, ImmutableList<ControlComponentPublicKeysPayload>, Boolean>> validations = Stream.of(
				this::validateCCMSchnorrProofsConsistency,
				this::validateCCRSchnorrProofsConsistency);

		return validations
				.parallel()
				.map(f -> f.apply(setupComponentPublicKeysPayload, onlineControlComponentPublicKeys))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	private boolean validateCCMSchnorrProofsConsistency(final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload,
			final ImmutableList<ControlComponentPublicKeysPayload> controlComponentPublicKeysPayloads) {
		final ImmutableMap<Integer, GroupVector<SchnorrProof, ZqGroup>> electionEventCcmjSchnorrProofs = setupComponentPublicKeysPayload.getSetupComponentPublicKeys()
				.combinedControlComponentPublicKeys().stream()
				.collect(toImmutableMap(ControlComponentPublicKeys::nodeId, ControlComponentPublicKeys::ccmjSchnorrProofs));

		return controlComponentPublicKeysPayloads.stream()
				.parallel()
				.map(ControlComponentPublicKeysPayload::getControlComponentPublicKeys)
				.map(controlComponentPublicKeys -> electionEventCcmjSchnorrProofs.get(controlComponentPublicKeys.nodeId())
						.equals(controlComponentPublicKeys.ccmjSchnorrProofs()))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	private boolean validateCCRSchnorrProofsConsistency(final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload,
			final ImmutableList<ControlComponentPublicKeysPayload> controlComponentPublicKeysPayloads) {
		final ImmutableMap<Integer, GroupVector<SchnorrProof, ZqGroup>> electionEventCcrjSchnorrProofs = setupComponentPublicKeysPayload.getSetupComponentPublicKeys()
				.combinedControlComponentPublicKeys().stream()
				.collect(toImmutableMap(ControlComponentPublicKeys::nodeId, ControlComponentPublicKeys::ccrjSchnorrProofs));

		return controlComponentPublicKeysPayloads.stream()
				.parallel()
				.map(ControlComponentPublicKeysPayload::getControlComponentPublicKeys)
				.map(controlComponentPublicKeys -> electionEventCcrjSchnorrProofs.get(controlComponentPublicKeys.nodeId())
						.equals(controlComponentPublicKeys.ccrjSchnorrProofs()))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}
}
