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
package ch.post.it.evoting.verifier.common.block.tools;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetFinalPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetInitialPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ChoiceCodeGenerationDTO;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ReturnCodeGenerationRequestPayload;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ReturnCodeGenerationResponsePayload;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionParameters;
import ch.post.it.evoting.verifier.common.block.exceptions.MalformedFileException;
import ch.post.it.evoting.verifier.common.block.exceptions.MissingFileException;
import ch.post.it.evoting.verifier.common.block.exceptions.UnexpectedFileException;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

@Service
public class ElectionDataExtractionService {

	private final PathService pathService;
	private final ObjectMapper objectMapper;

	public ElectionDataExtractionService(final PathService pathService, final ObjectMapper objectMapper) {
		this.pathService = pathService;
		this.objectMapper = objectMapper;
	}

	/**
	 * Gets the election event.
	 *
	 * @param inputDirectoryPath the root directory containing project files.
	 * @return the election event found in the project files, at the expected location if it exists.
	 * @throws UncheckedIOException if the file cannot be deserialized to an ElectionEvent.
	 */
	public ElectionEvent getElectionEvent(Path inputDirectoryPath) {
		final var dataConfigPathNode = pathService.buildFromRootPath(StructureKey.DATA_CONFIG_UPDATED, inputDirectoryPath);
		try {
			return Deserializer.fromJson(dataConfigPathNode.getPath(), ElectionEvent.class);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize election event.", e);
		}
	}

	public GqGroup getEncryptionParameters(final Path inputDirectoryPath) {
		final var encryptionParametersPathNode = pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);
		final EncryptionParameters encryptionParameters;
		try {
			encryptionParameters = Deserializer.fromJson(encryptionParametersPathNode.getPath(), EncryptionParameters.class);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize encryption parameters.", e);
		}
		final BigInteger p = encryptionParameters.getP();
		final BigInteger q = encryptionParameters.getQ();
		final BigInteger g = encryptionParameters.getG();

		return new GqGroup(p, q, g);
	}

	public ImmutableList<BigInteger> getPrimes(final Path inputDirectoryPath) {
		final PathNode primesPathNode;
		primesPathNode = pathService.buildFromRootPath(StructureKey.PRIMES, inputDirectoryPath);
		try (Stream<BigInteger> iterable = Files.lines(primesPathNode.getPath()).map(BigInteger::new)) {
			return iterable.collect(ImmutableList.toImmutableList());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Deserializes the MixnetFinalPayload contained in the ballot box directory. This method fails if there is no MixnetFinalPayload present (e.g. in
	 * the case of an empty ballot box).
	 *
	 * @param ballotBoxDirectoryPath the path of a ballot box containing a MixnetFinalPayload
	 * @throws UncheckedIOException if the mixnet final payload could not be read from the file
	 */
	public MixnetFinalPayload getMixnetFinalPayload(final Path ballotBoxDirectoryPath) {
		final var mixnetFinalPayloadPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX_OFFLINE_MIXING,
				ballotBoxDirectoryPath);

		try {
			return objectMapper.readValue(mixnetFinalPayloadPathNode.getPath().toFile(), MixnetFinalPayload.class);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Deserializes the MixnetShufflePayloads contained in the ballot box directory. This method fails if there are no MixnetShufflePayloads present
	 * (e.g. in the case of an empty ballot box).
	 *
	 * @param ballotBoxDirectoryPath the path of a ballot box containing MixnetShufflePayloads
	 * @return a list of mixnet shuffle payloads
	 * @throws MissingFileException    if one or more mixnet shuffle payload files are missing
	 * @throws UnexpectedFileException if too many mixnet shuffle payload files are present
	 * @throws MalformedFileException  if the node Ids in the mixnet shuffle payload files are incorrect
	 * @throws UncheckedIOException    if the mixnet shuffle payload could not be read from the file
	 */
	public List<MixnetShufflePayload> getMixnetShufflePayloads(final Path ballotBoxDirectoryPath) {
		final var mixnetShufflePayloadPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX_ONLINE_MIXING,
				ballotBoxDirectoryPath);

		List<MixnetShufflePayload> payloads = new ArrayList<>();
		for (Path mixnetShufflePayloadPath : mixnetShufflePayloadPathNode.getRegexPaths()) {
			final MixnetShufflePayload payload;
			try {
				payload = objectMapper.readValue(mixnetShufflePayloadPath.toFile(), MixnetShufflePayload.class);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			payloads.add(payload);
		}

		if (payloads.size() < 3) {
			throw new MissingFileException("Missing shufflePayload file(s)");
		} else if (payloads.size() > 3) {
			throw new UnexpectedFileException("Too many shufflePayload files");
		}

		final Set<Integer> nodeIds = payloads.stream()
				.map(MixnetShufflePayload::getNodeId)
				.collect(Collectors.toSet());

		if (!nodeIds.containsAll(Set.of(1, 2, 3))) {
			throw new MalformedFileException(String.format("The expected node Ids are {1, 2, 3}. Received %s", nodeIds));
		}

		return payloads;
	}

	/**
	 * Deserializes the MixnetInitialPayload contained in the ballot box directory. This method fails if there is no MixnetInitialPayload present
	 * (e.g. in the case of an empty ballot box).
	 *
	 * @param ballotBoxDirectoryPath the path of a ballot box containing a MixnetInitialPayload
	 * @return a mixnet initial payload
	 * @throws UncheckedIOException if the mixnet initial payload could not be read from the file
	 */
	public MixnetInitialPayload getMixnetInitialPayload(final Path ballotBoxDirectoryPath) {
		final var mixnetInitialPayloadPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX_INITIAL, ballotBoxDirectoryPath);

		try {
			return objectMapper.readValue(mixnetInitialPayloadPathNode.getPath().toFile(), MixnetInitialPayload.class);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Gets the number of votes in a ballot box.
	 *
	 * @param ballotBoxDirectoryPath the path to the ballot box directory
	 * @return the count of the number of votes in that ballot box
	 */
	@SuppressWarnings("java:S1481")
	public int getNumberOfVotes(final Path ballotBoxDirectoryPath) {
		final var decompressedVotesPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.DECOMPRESSED_VOTES, ballotBoxDirectoryPath);
		Iterable<List<String>> iterable = ch.post.it.evoting.verifier.common.block.tools.Deserializer
				.fromCsv(decompressedVotesPathNode.getPath(), "\n", Arrays::asList);
		var count = 0;
		for (List<String> ignored : iterable) {
			count++;
		}
		return count;
	}

	public ReturnCodeGenerationRequestPayload deserializeReturnCodeGenerationRequestPayload(final Path votingCardSetIDPath) {
		final PathNode nodePath;
		try {
			nodePath = pathService.buildFromDynamicAncestorPath(StructureKey.RETURN_CODE_GENERATION_REQUEST_PAYLOAD, votingCardSetIDPath);

			return objectMapper.readValue(nodePath.getPath().toFile(), ReturnCodeGenerationRequestPayload.class);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize control component request payload for votingCardSetIDPath : " + votingCardSetIDPath,
					e);
		}
	}

	public List<ReturnCodeGenerationResponsePayload> deserializeControlComponentContributions(final Path votingCardSetIDPath) {
		final PathNode nodePath;
		try {
			nodePath = pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_CONTRIBUTION, votingCardSetIDPath);

			final List<ChoiceCodeGenerationDTO<ReturnCodeGenerationResponsePayload>> choiceCodeGenerationDTOS = objectMapper.readValue(
					nodePath.getPath().toFile(), new TypeReference<>() {
					});

			return choiceCodeGenerationDTOS
					.stream()
					.map(ChoiceCodeGenerationDTO::getPayload)
					.collect(Collectors.toList());

		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize control component contribution for votingCardSetIDPath : " + votingCardSetIDPath,
					e);
		}
	}
}