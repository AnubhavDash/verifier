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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetFinalPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetInitialPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetShufflePayload;
import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.common.block.exceptions.MalformedFileException;
import ch.post.it.evoting.verifier.common.block.exceptions.MissingFileException;
import ch.post.it.evoting.verifier.common.block.exceptions.UnexpectedFileException;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

@Service
public class ElectionDataExtractionService {

	private PathService pathService;
	private ObjectMapper objectMapper;

	@Autowired
	public ElectionDataExtractionService(final PathService pathService, final ObjectMapper objectMapper) {
		this.pathService = pathService;
		this.objectMapper = objectMapper;
	}

	/**
	 * Gets the election event.
	 *
	 * @param inputDirectoryPath the root directory containing project files.
	 * @return the election event found in the project files, at the expected location if it existst.
	 * @throws FileNotFoundException if the file can't be found at the expected location
	 * @throws IOException,          if the file cannot be deserialized to an ElectionEvent.
	 */
	public ElectionEvent getElectionEvent(Path inputDirectoryPath) throws IOException {
		final PathNode dataConfigPathNode;
		try {
			dataConfigPathNode = pathService.buildFromRootPath(StructureKey.DATA_CONFIG_UPDATED, inputDirectoryPath);
		} catch (IOException e) {
			throw new UncheckedIOException(String.format("Could not find dataConfig_updated in %s", inputDirectoryPath), e);
		}

		return ch.post.it.evoting.verifier.common.block.tools.Deserializer.fromJson(dataConfigPathNode.getPath(), ElectionEvent.class);
	}

	/**
	 * Deserializes the MixnetFinalPayload contained in the ballot box directory. This method fails if there is no MixnetFinalPayload present (e.g. in
	 * the case of an empty ballot box).
	 *
	 * @param ballotBoxDirectoryPath the path of a ballot box containing a MixnetFinalPayload
	 * @throws FileNotFoundException if the file can't be found at the expected location
	 * @throws IOException           if the file can't be deserialized
	 */
	public MixnetFinalPayload getMixnetFinalPayload(final Path ballotBoxDirectoryPath) {
		final PathNode mixnetFinalPayloadPathNode;
		try {
			mixnetFinalPayloadPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX_OFFLINE_MIXING, ballotBoxDirectoryPath);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

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
	 */
	public List<MixnetShufflePayload> getMixnetShufflePayloads(final Path ballotBoxDirectoryPath) {
		final PathNode mixnetShufflePayloadPathNode;
		try {
			mixnetShufflePayloadPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX_ONLINE_MIXING, ballotBoxDirectoryPath);
		} catch (IOException e) {
			throw new UncheckedIOException("Could not find mixnetShufflePayload files", e);
		}

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
		final PathNode mixnetInitialPayloadPathNode;
		try {
			mixnetInitialPayloadPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX_INITIAL, ballotBoxDirectoryPath);
		} catch (IOException e) {
			throw new UncheckedIOException("Could not find mixnetInitialPayload file", e);
		}

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
		final PathNode decompressedVotesPathNode;
		try {
			decompressedVotesPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.DECOMPRESSED_VOTES, ballotBoxDirectoryPath);
		} catch (IOException e) {
			throw new UncheckedIOException("Could not find decompressed votes file", e);
		}
		Iterable<List<String>> iterable = ch.post.it.evoting.verifier.common.block.tools.Deserializer
				.fromCsv(decompressedVotesPathNode.getPath(), "\n", Arrays::asList);
		int count = 0;
		for (List<String> ignored : iterable) {
			count++;
		}
		return count;
	}
}