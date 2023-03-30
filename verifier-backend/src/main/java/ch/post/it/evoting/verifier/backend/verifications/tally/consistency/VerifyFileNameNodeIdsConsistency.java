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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

@Component
public class VerifyFileNameNodeIdsConsistency extends AbstractVerification {

	private final PathService pathService;
	private final ObjectMapper objectMapper;

	protected VerifyFileNameNodeIdsConsistency(final ResultPublisherService resultPublisherService, final PathService pathService,
			final ObjectMapper objectMapper) {
		super(resultPublisherService);
		this.pathService = pathService;
		this.objectMapper = objectMapper;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification309.description"));
		definition.setId("8.10");
		definition.setName("VerifyFileNameNodeIdsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final List<Function<Path, Boolean>> validations = new ArrayList<>();
		validations.add(this::verifyControlComponentBallotBoxFileNamesConsistency);
		validations.add(this::verifyControlComponentShuffleFileNamesConsistency);

		final boolean fileNamesConsistent = validations
				.stream()
				.parallel()
				.map(f -> f.apply(inputDirectoryPath))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (fileNamesConsistent) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification309.nok.message"));
		}
	}

	private boolean verifyControlComponentBallotBoxFileNamesConsistency(final Path inputDirectoryPath) {
		final PathNode ballotBoxes = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxes.getRegexPaths().stream()
				.parallel()
				.map(ballotBoxPath -> {
					final PathNode controlComponentBallotBoxNode = pathService.buildFromDynamicAncestorPath(
							StructureKey.CONTROL_COMPONENT_BALLOT_BOX, ballotBoxPath);

					return controlComponentBallotBoxNode.getRegexPaths().stream()
							.parallel()
							.map(path -> {
								final String fileName = path.getFileName().toString();
								final String nodeIdGroup = pathService.getRegexGroup(StructureKey.CONTROL_COMPONENT_BALLOT_BOX, fileName, 1);
								final int fileNodeId = Integer.parseInt(nodeIdGroup);

								final ControlComponentBallotBoxPayload controlComponentBallotBoxPayload;
								try {
									controlComponentBallotBoxPayload = objectMapper.readValue(path.toFile(), ControlComponentBallotBoxPayload.class);
								} catch (final IOException e) {
									final String errorMessage = String.format("Could not deserialize payload from file. [file name: %s]", fileName);
									throw new UncheckedIOException(errorMessage, e);
								}
								final int payloadNodeId = controlComponentBallotBoxPayload.getNodeId();

								return fileNodeId == payloadNodeId;
							})
							.reduce(Boolean::logicalAnd)
							.orElse(Boolean.FALSE);
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	private boolean verifyControlComponentShuffleFileNamesConsistency(final Path inputDirectoryPath) {
		final PathNode ballotBoxes = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxes.getRegexPaths().stream()
				.parallel()
				.map(ballotBoxPath -> {
					final PathNode controlComponentShuffleNode = pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_SHUFFLE,
							ballotBoxPath);

					return controlComponentShuffleNode.getRegexPaths().stream()
							.parallel()
							.map(path -> {
								final String fileName = path.getFileName().toString();
								final String nodeIdGroup = pathService.getRegexGroup(StructureKey.CONTROL_COMPONENT_SHUFFLE, fileName, 1);
								final int fileNodeId = Integer.parseInt(nodeIdGroup);

								final ControlComponentShufflePayload controlComponentShufflePayload;
								try {
									controlComponentShufflePayload = objectMapper.readValue(path.toFile(), ControlComponentShufflePayload.class);
								} catch (final IOException e) {
									final String errorMessage = String.format("Could not deserialize payload from file. [file name: %s]", fileName);
									throw new UncheckedIOException(errorMessage, e);
								}
								final int payloadNodeId = controlComponentShufflePayload.getNodeId();

								return fileNodeId == payloadNodeId;
							})
							.reduce(Boolean::logicalAnd)
							.orElse(Boolean.FALSE);
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}
}
