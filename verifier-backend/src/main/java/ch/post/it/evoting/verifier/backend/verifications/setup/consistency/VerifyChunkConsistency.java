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

import static com.google.common.collect.Sets.newHashSet;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.dataextractors.ControlComponentCodeSharesPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.dataextractors.SetupComponentVerificationDataPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

/**
 * This verification ensures that the chunked files in the audit archive (data set) are consistent with the filename. It also checks the chunk ids are
 * monotonically increasing from 0 for each vcs.
 */
@Component
public class VerifyChunkConsistency extends AbstractVerification {

	private final PathService pathService;
	private final ControlComponentCodeSharesPayloadDataExtractor controlComponentCodeSharesPayloadDataExtractor;
	private final SetupComponentVerificationDataPayloadDataExtractor setupComponentVerificationDataPayloadDataExtractor;

	protected VerifyChunkConsistency(
			final PathService pathService,
			final ResultPublisherService resultPublisherService,
			final ControlComponentCodeSharesPayloadDataExtractor controlComponentCodeSharesPayloadDataExtractor,
			final SetupComponentVerificationDataPayloadDataExtractor setupComponentVerificationDataPayloadDataExtractor) {
		super(resultPublisherService);
		this.pathService = pathService;
		this.controlComponentCodeSharesPayloadDataExtractor = controlComponentCodeSharesPayloadDataExtractor;
		this.setupComponentVerificationDataPayloadDataExtractor = setupComponentVerificationDataPayloadDataExtractor;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification314.description"));
		definition.setId("03.15");
		definition.setName("VerifyChunkConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final PathNode verificationCardSets = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		final List<Function<PathNode, Boolean>> validations = new ArrayList<>();
		validations.add(this::validateControlComponentCodeSharesPayloads);
		validations.add(this::validateSetupComponentVerificationDataPayloads);

		final boolean isChunkIdsCoherent = validations.stream()
				.parallel()
				.map(f -> f.apply(verificationCardSets))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (isChunkIdsCoherent) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification314.nok.message"));
		}
	}

	private boolean validateControlComponentCodeSharesPayloads(final PathNode verificationCardSets) {
		final List<List<Path>> payloadsPerCardSet = verificationCardSets.getRegexPaths().stream()
				.parallel()
				.map(path -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_CODE_SHARES, path).getRegexPaths())
				.toList();

		// validate the monotony of sequence incrementation
		final boolean isSequenceMonotonic = isSequenceMonotonic(payloadsPerCardSet);

		// validate content of file match filename
		final boolean doFileNameMatchContent = payloadsPerCardSet.stream()
				.parallel()
				.flatMap(Collection::stream)
				.allMatch(this::validateControlComponentCodeSharesPayloadContentMatchFileName);

		return isSequenceMonotonic && doFileNameMatchContent;
	}

	private boolean validateControlComponentCodeSharesPayloadContentMatchFileName(final Path payloadPath) {
		final int expectedChunkId = Integer.parseInt(payloadPath.getFileName().toString().split("\\.")[1]);

		return controlComponentCodeSharesPayloadDataExtractor.load(payloadPath).chunkIds().stream()
				.allMatch(chunkId -> chunkId == expectedChunkId);
	}

	private boolean validateSetupComponentVerificationDataPayloads(final PathNode verificationCardSets) {
		final List<List<Path>> payloadsPerCardSet = verificationCardSets.getRegexPaths().stream()
				.parallel()
				.map(path -> pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_VERIFICATION_DATA, path).getRegexPaths())
				.toList();

		// validate the monotony of sequence incrementation
		final boolean isSequenceMonotonic = isSequenceMonotonic(payloadsPerCardSet);

		// validate content of file match filename
		final boolean doFileNameMatchContent = payloadsPerCardSet.stream()
				.flatMap(Collection::stream)
				.parallel()
				.allMatch(this::validateSetupComponentVerificationDataPayloadContentMatchFileName);

		return isSequenceMonotonic && doFileNameMatchContent;
	}

	private boolean validateSetupComponentVerificationDataPayloadContentMatchFileName(final Path payloadPath) {
		final int expectedChunkId = Integer.parseInt(payloadPath.getFileName().toString().split("\\.")[1]);

		final Integer payloadChunkId = setupComponentVerificationDataPayloadDataExtractor.load(payloadPath).chunkId();

		return payloadChunkId == expectedChunkId;
	}

	@VisibleForTesting
	boolean isSequenceMonotonic(final List<List<Path>> payloadsPerCardSet) {
		return payloadsPerCardSet.stream()
				.parallel()
				.allMatch(payloadPath -> {
					final List<Integer> chunkIds = payloadPath.stream()
							.map(path -> Integer.parseInt(path.getFileName().toString().split("\\.")[1]))
							.sorted()
							.toList();

					if (!chunkIds.isEmpty()) {
						return chunkIds.get(0) == 0 && chunkIds.get(chunkIds.size() - 1) == chunkIds.size() - 1 &&
								newHashSet(chunkIds).size() == chunkIds.size();
					}
					return false;
				});
	}
}

