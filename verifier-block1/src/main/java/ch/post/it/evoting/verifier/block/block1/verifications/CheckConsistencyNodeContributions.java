/*
 * Copyright 2021 Post CH Ltd
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
package ch.post.it.evoting.verifier.block.block1.verifications;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.cryptoprimitives.Validations;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ChoiceCodeGenerationDTO;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ReturnCodeGenerationOutput;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ReturnCodeGenerationResponsePayload;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.AbstractVerification;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionParameters;
import ch.post.it.evoting.verifier.common.block.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;

@Component
public class CheckConsistencyNodeContributions extends AbstractVerification {

	private final ElectionDataExtractionService deserializer;
	private final PathService pathService;
	private final ObjectMapper objectMapper;

	public CheckConsistencyNodeContributions(final PathService pathService, final ElectionDataExtractionService deserializer,
			final ApplicationEventPublisher applicationEventPublisher, final ObjectMapper objectMapper) {
		super(applicationEventPublisher);
		this.pathService = pathService;
		this.deserializer = deserializer;
		this.objectMapper = objectMapper;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(1);
		definition.setCategory(Category.CONSISTENCY);
		definition
				.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification41.description"));
		definition.setId(41);
		definition.setName("checkConsistencyNodeContributions");
		definition.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		definition.addVerificationTrait(VerificationTrait.BLOCK_1);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		final boolean verification = getVerificationInputs(inputDirectoryPath)
				.parallel()
				.map(this::checkConsistencyNodeContributions)
				.reduce(Boolean::logicalAnd).orElse(Boolean.FALSE);

		if (verification) {
			return VerificationResultEvent.success(this, this.getVerificationDefinition());
		} else {
			return VerificationResultEvent.failure(this, this.getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification41.nok.message"));
		}
	}

	/**
	 * Gets all inputs for the verification from the relevant files.
	 *
	 * @param inputDirectoryPath the root directory of files
	 * @return a Stream of verification input, one per verification card set
	 */
	private Stream<NodeContributionsChunk> getVerificationInputs(final Path inputDirectoryPath) {
		final var electionEvent = deserializer.getElectionEvent(inputDirectoryPath);
		final var electionEventId = TypeConverter.UUIDToStringWithoutDash(electionEvent.getId());
		final GqGroup encryptionParameters = extractEncryptionParameters(inputDirectoryPath);

		// Iterate over all verification card set ids directories
		final var verificationCardSetsIdPathNode = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		return verificationCardSetsIdPathNode.getRegexPaths().stream()
				.map(vcsIdPath -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_CONTRIBUTION, vcsIdPath))
				.flatMap(nodeContributionsChunkPathNode -> nodeContributionsChunkPathNode.getRegexPaths().stream())
				.map(chunkFilePath -> {
					final List<ChoiceCodeGenerationDTO<ReturnCodeGenerationResponsePayload>> choiceCodeGenerationDTOS;
					try {
						choiceCodeGenerationDTOS = objectMapper.readValue(chunkFilePath.toFile(), new TypeReference<>() {
						});
					} catch (IOException e) {
						throw new UncheckedIOException("Failed to deserialize the node contributions chunk file.", e);
					}
					final var verificationCardSetId = chunkFilePath.getParent().getFileName().toString();
					final var datasetValues = new DatasetValues(electionEventId, verificationCardSetId, encryptionParameters);
					return new NodeContributionsChunk(datasetValues, choiceCodeGenerationDTOS);
				});
	}

	/**
	 * Check the consistency of a node contributions file.
	 *
	 * @param nodeContributionsChunk node contributions for one chunk
	 * @return {@code true} if the electionEventId from the dataset equals the electionEventId of the node outputs
	 * <br>AND if the verificationCardSetId from the dataset equals the verificationCardSetId of the node outputs
	 * <br>AND if all the following attributes are equal between node outputs:
	 * <ul>
	 *     <li>chunkId</li>
	 *     <li>verificationCardIds</li>
	 * </ul>
	 */
	private boolean checkConsistencyNodeContributions(final NodeContributionsChunk nodeContributionsChunk) {
		final boolean contextEquals = nodeContributionsChunk.nodeOutputs.stream()
				.parallel()
				.map(nodeOutput -> nodeOutput.getPayload().getElectionEventId().equals(nodeContributionsChunk.datasetValues.electionEventId)
						&& nodeOutput.getPayload().getVerificationCardSetId().equals(nodeContributionsChunk.datasetValues.verificationCardSetId)
						&& nodeOutput.getPayload().getEncryptionGroup().equals(nodeContributionsChunk.datasetValues.encryptionParameters))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		final boolean areChunkIdsConsistent = Validations.allEqual(nodeContributionsChunk.nodeOutputs.stream().map(ChoiceCodeGenerationDTO::getPayload),
				ReturnCodeGenerationResponsePayload::getChunkId);

		final boolean areVerificationCardSetIdsDistinct = nodeContributionsChunk.nodeOutputs.stream()
				.map(ChoiceCodeGenerationDTO::getPayload)
				.map(p -> p.getReturnCodeGenerationOutputs().stream()
						.map(ReturnCodeGenerationOutput::getVerificationCardId)
						.collect(Collectors.toList()))
				// All four control components should have one distinct list of verification card IDs
				.distinct().limit(2).count() <= 1;

		return contextEquals && areChunkIdsConsistent && areVerificationCardSetIdsDistinct;
	}

	// Extract encryption parameters from the dataset and a GqGroup
	private GqGroup extractEncryptionParameters(Path inputDirectoryPath) {
		final var encryptParams = pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);
		final EncryptionParameters encryptionParameters;
		try {
			encryptionParameters = objectMapper.readValue(encryptParams.getPath().toFile(), EncryptionParameters.class);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize encryption parameters.", e);
		}
		return new GqGroup(encryptionParameters.getP(), encryptionParameters.getQ(), encryptionParameters.getG());
	}

	// Data class containing the values from the dataset and the node contribution outputs for one chunk
	static class NodeContributionsChunk {
		private final DatasetValues datasetValues;
		private final List<ChoiceCodeGenerationDTO<ReturnCodeGenerationResponsePayload>> nodeOutputs;

		public NodeContributionsChunk(final DatasetValues datasetValues,
				final List<ChoiceCodeGenerationDTO<ReturnCodeGenerationResponsePayload>> nodeOutputs) {
			this.datasetValues = datasetValues;
			this.nodeOutputs = nodeOutputs;
		}
	}

	static class DatasetValues {
		private final String electionEventId;
		private final String verificationCardSetId;
		private final GqGroup encryptionParameters;

		public DatasetValues(final String electionEventId, final String verificationCardSetId, final GqGroup encryptionParameters) {
			this.electionEventId = electionEventId;
			this.verificationCardSetId = verificationCardSetId;
			this.encryptionParameters = encryptionParameters;
		}
	}
}
