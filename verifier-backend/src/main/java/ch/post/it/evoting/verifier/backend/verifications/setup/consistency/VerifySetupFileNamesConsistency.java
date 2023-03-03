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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ControlComponentCodeSharesPayload;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SetupComponentVerificationDataPayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.configuration.ControlComponentPublicKeysPayload;

@Component
public class VerifySetupFileNamesConsistency extends AbstractVerification {

	private static final String DESERIALIZATION_ERROR_MESSAGE = "Could not deserialize payload from file. [file name: %s]";

	private final PathService pathService;

	private final ObjectMapper objectMapper;
	private final ElectionDataExtractionService electionDataExtractionService;

	protected VerifySetupFileNamesConsistency(
			final ResultPublisherService resultPublisherService,
			final PathService pathService,
			final ObjectMapper objectMapper,
			final ElectionDataExtractionService electionDataExtractionService) {
		super(resultPublisherService);
		this.pathService = pathService;
		this.objectMapper = objectMapper;
		this.electionDataExtractionService = electionDataExtractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification301.description"));
		definition.setId(301);
		definition.setName("VerifySetupFileNamesConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final List<Function<Path, Boolean>> validations = new ArrayList<>();
		validations.add(this::verifyControlComponentPublicKeyFileNameConsistency);
		validations.add(this::verifyControlComponentCodeSharesFileNameConsistency);
		validations.add(this::verifySetupComponentVerificationDataFileNameConsistency);

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
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification301.nok.message"));
		}
	}

	private boolean verifyControlComponentPublicKeyFileNameConsistency(final Path inputDirectoryPath) {
		final PathNode controlComponentPublicKeyNodes = pathService.buildFromRootPath(StructureKey.CONTROL_COMPONENT_PUBLIC_KEYS, inputDirectoryPath);

		return controlComponentPublicKeyNodes.getRegexPaths().stream()
				.parallel()
				.map(path -> {
					final String fileName = path.getFileName().toString();
					final String nodeIdGroup = pathService.getRegexGroup(StructureKey.CONTROL_COMPONENT_PUBLIC_KEYS, fileName, 1);
					final int fileNodeId = Integer.parseInt(nodeIdGroup);

					final ControlComponentPublicKeysPayload controlComponentPublicKeysPayload;
					try {
						controlComponentPublicKeysPayload = objectMapper.readValue(path.toFile(), ControlComponentPublicKeysPayload.class);
					} catch (final IOException e) {
						final String errorMessage = String.format(DESERIALIZATION_ERROR_MESSAGE, path.getFileName());
						throw new UncheckedIOException(errorMessage, e);
					}
					final int payloadNodeId = controlComponentPublicKeysPayload.getControlComponentPublicKeys().nodeId();

					return fileNodeId == payloadNodeId;
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	private boolean verifyControlComponentCodeSharesFileNameConsistency(final Path inputDirectoryPath) {
		final PathNode verificationCardSets = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		return verificationCardSets.getRegexPaths().stream()
				.parallel()
				.map(verificationCardSetPath -> {
					final PathNode controlComponentCodeSharesNode = pathService.buildFromDynamicAncestorPath(
							StructureKey.CONTROL_COMPONENT_CODE_SHARES, verificationCardSetPath);

					return controlComponentCodeSharesNode.getRegexPaths().stream()
							.parallel()
							.map(path -> {
								final String fileName = path.getFileName().toString();
								final String chunkIdGroup = pathService.getRegexGroup(StructureKey.CONTROL_COMPONENT_CODE_SHARES, fileName, 1);
								final int fileChunkId = Integer.parseInt(chunkIdGroup);

								final List<ControlComponentCodeSharesPayload> controlComponentCodeSharesPayloads = electionDataExtractionService.getControlComponentCodeSharesOrderByNodeId(
										path);
								final List<Integer> payloadChunkIds = controlComponentCodeSharesPayloads.stream()
										.map(ControlComponentCodeSharesPayload::getChunkId)
										.toList();

								return payloadChunkIds.stream().allMatch(payloadChunkId -> fileChunkId == payloadChunkId);
							})
							.reduce(Boolean::logicalAnd)
							.orElse(Boolean.FALSE);
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	private boolean verifySetupComponentVerificationDataFileNameConsistency(final Path inputDirectoryPath) {
		final PathNode verificationCardSets = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		return verificationCardSets.getRegexPaths().stream()
				.parallel()
				.map(verificationCardSetPath -> {
					final PathNode setupComponentVerificationDataNode = pathService.buildFromDynamicAncestorPath(
							StructureKey.SETUP_COMPONENT_VERIFICATION_DATA, verificationCardSetPath);

					return setupComponentVerificationDataNode.getRegexPaths().stream()
							.parallel()
							.map(path -> {
								final String fileName = path.getFileName().toString();
								final String chunkIdGroup = pathService.getRegexGroup(StructureKey.SETUP_COMPONENT_VERIFICATION_DATA, fileName, 1);
								final int fileChunkId = Integer.parseInt(chunkIdGroup);

								final SetupComponentVerificationDataPayload setupComponentVerificationDataPayload;
								try {
									setupComponentVerificationDataPayload = objectMapper.readValue(path.toFile(),
											SetupComponentVerificationDataPayload.class);
								} catch (final IOException e) {
									final String errorMessage = String.format(DESERIALIZATION_ERROR_MESSAGE, path.toFile().getName());
									throw new UncheckedIOException(errorMessage, e);
								}
								final int payloadChunkId = setupComponentVerificationDataPayload.getChunkId();

								return fileChunkId == payloadChunkId;
							})
							.reduce(Boolean::logicalAnd)
							.orElse(Boolean.FALSE);
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}
}
