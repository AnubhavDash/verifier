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
package ch.post.it.evoting.verifier.backend.tools;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.cryptoprimitives.domain.ControlComponentConstants;
import ch.post.it.evoting.verifier.backend.domain.EncryptionGroupParameters;
import ch.post.it.evoting.verifier.backend.domain.EncryptionGroupParametersPayload;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;

@Service
public class EncryptionGroupParametersExtractionService {

	private final PathService pathService;
	private final ObjectMapper objectMapper;

	public EncryptionGroupParametersExtractionService(final PathService pathService, final ObjectMapper objectMapper) {
		this.pathService = pathService;
		this.objectMapper = objectMapper;
	}

	/**
	 * Gets the encryption group parameters from the election event context.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return the encryption group parameters of the election event context.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public EncryptionGroupParameters getFromElectionEventContext(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode electionEventContextPayloadPath = pathService.buildFromRootPath(StructureKey.ELECTION_EVENT_CONTEXT, inputDirectoryPath);
		try {
			return objectMapper.readValue(electionEventContextPayloadPath.getPath().toFile(), EncryptionGroupParametersPayload.class).gqGroup();
		} catch (final IOException e) {
			throw new UncheckedIOException(String.format(
					"Failed to deserialize the encryption group parameters from the election event context payload. [path: %s]",
					electionEventContextPayloadPath), e);
		}
	}

	/**
	 * Gets the encryption group parameters from the encryption parameters payload.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return the encryption group parameters of the encryption parameters payload.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public EncryptionGroupParameters getFromEncryptionParameters(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode encryptionParametersPayloadPath = pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);
		try {
			return objectMapper.readValue(encryptionParametersPayloadPath.getPath().toFile(), EncryptionGroupParametersPayload.class).gqGroup();
		} catch (final IOException e) {
			throw new UncheckedIOException(String.format(
					"Failed to deserialize the encryption group parameters from the election event context payload. [path: %s]",
					encryptionParametersPayloadPath), e);
		}

	}

	/**
	 * Gets the encryption group parameters of all control component public key payloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return a stream of the encryption group parameters of the control component public key payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public Stream<EncryptionGroupParameters> getFromControlComponentPublicKeys(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode controlComponentPublicKeyPayloadPaths = pathService.buildFromRootPath(StructureKey.CONTROL_COMPONENT_PUBLIC_KEYS,
				inputDirectoryPath);
		return controlComponentPublicKeyPayloadPaths.getRegexPaths().stream()
				.map(path -> {
					try {
						return objectMapper.readValue(path.toFile(), EncryptionGroupParametersPayload.class).gqGroup();
					} catch (final IOException e) {
						throw new UncheckedIOException(String.format(
								"Failed to deserialize the encryption group parameters from the control component public key payload. [path: %s]",
								path), e);
					}
				});
	}

	/**
	 * Gets the encryption group parameters of all control component ballot box payloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return a stream of the encryption group parameters of the control component ballot box payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public Stream<EncryptionGroupParameters> getFromControlComponentBallotBoxPayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return pathNode.getRegexPaths().stream()
				.flatMap(ballotBoxIdPath -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_BALLOT_BOX, ballotBoxIdPath)
						.getRegexPaths()
						.stream())
				.map(ballotBoxPayloadPaths -> {
					try {
						return objectMapper.readValue(ballotBoxPayloadPaths.toFile(), EncryptionGroupParametersPayload.class).gqGroup();
					} catch (final IOException e) {
						throw new UncheckedIOException(String.format(
								"Failed to deserialize the encryption group parameters from the control component ballot box payload. [path: %s]",
								ballotBoxPayloadPaths), e);
					}
				});
	}

	/**
	 * Gets the encryption group parameters of all control component shuffle payloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return a stream of the encryption group parameters of the control component shuffle payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public Stream<EncryptionGroupParameters> getFromControlComponentShufflePayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return pathNode.getRegexPaths().stream()
				.flatMap(ballotBoxIdPath -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_SHUFFLE, ballotBoxIdPath)
						.getRegexPaths()
						.stream())
				.map(ballotBoxPayloadPaths -> {
					try {
						return objectMapper.readValue(ballotBoxPayloadPaths.toFile(), EncryptionGroupParametersPayload.class).gqGroup();
					} catch (final IOException e) {
						throw new UncheckedIOException(String.format(
								"Failed to deserialize the encryption group parameters from the control component shuffle payload. [path: %s]",
								ballotBoxPayloadPaths), e);
					}
				});
	}

	/**
	 * Gets the encryption group parameters of the TallyComponentShufflePayload.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public EncryptionGroupParameters getFromTallyComponentShufflePayload(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
		final PathNode tallyComponentShufflePayloadPath = pathService.buildFromDynamicAncestorPath(StructureKey.TALLY_COMPONENT_SHUFFLE,
				pathNode.getPath());

		try {
			return objectMapper.readValue(tallyComponentShufflePayloadPath.getPath().toFile(), EncryptionGroupParametersPayload.class).gqGroup();
		} catch (final IOException e) {
			throw new UncheckedIOException(
					String.format("Failed to deserialize the encryption group parameters from the tally component shuffle payload. [path: %s]",
							tallyComponentShufflePayloadPath), e);
		}
	}

	/**
	 * Gets the encryption group parameters of the TallyComponentVotesPayloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public EncryptionGroupParameters getFromTallyComponentVotesPayload(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
		final PathNode tallyComponentVotesPayloadPath = pathService.buildFromDynamicAncestorPath(StructureKey.TALLY_COMPONENT_VOTES,
				pathNode.getPath());

		try {
			return objectMapper.readValue(tallyComponentVotesPayloadPath.getPath().toFile(), EncryptionGroupParametersPayload.class).gqGroup();
		} catch (final IOException e) {
			throw new UncheckedIOException(
					String.format("Failed to deserialize the encryption group parameters from the tally component votes payload. [path: %s]",
							tallyComponentVotesPayloadPath), e);
		}
	}

	/**
	 * Gets the encryption group parameters of the SetupComponentVerificationDataPayloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public Stream<EncryptionGroupParameters> getFromSetupComponentVerificationDataPayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		return pathNode.getRegexPaths().stream()
				.flatMap(ballotBoxIdPath -> pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_VERIFICATION_DATA, ballotBoxIdPath)
						.getRegexPaths()
						.stream())
				.map(setupComponentVerificationDataPath -> {
					try {
						return objectMapper.readValue(setupComponentVerificationDataPath.toFile(),
								EncryptionGroupParametersPayload.class).gqGroup();
					} catch (final IOException e) {
						throw new UncheckedIOException(String.format(
								"Failed to deserialize the encryption group parameters from the setup component verification data payload. [path: %s]",
								setupComponentVerificationDataPath), e);
					}
				});
	}

	/**
	 * Gets the encryption group parameters of the control component code shares payloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return a stream of the control component code shares payloads' encryption group parameters.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public Stream<EncryptionGroupParameters> getFromControlComponentCodeShares(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		return pathNode.getRegexPaths().stream()
				.flatMap(verificationCardSetIdPath -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_CODE_SHARES,
								verificationCardSetIdPath)
						.getRegexPaths()
						.stream())
				.map(controlComponentCodeSharesPath -> {
					try {
						return objectMapper.readTree(controlComponentCodeSharesPath.toFile());
					} catch (final IOException e) {
						throw new UncheckedIOException(String.format(
								"Failed to read the control component code shares. [path: %s]",
								controlComponentCodeSharesPath), e);
					}
				})
				.flatMap(node -> IntStream.range(0, ControlComponentConstants.NODE_IDS.size()).mapToObj(node::get))
				.map(node -> {
					try {
						return objectMapper.readValue(node.toString(), EncryptionGroupParametersPayload.class).gqGroup();
					} catch (final IOException e) {
						throw new UncheckedIOException(
								String.format(
										"Failed to deserialize the encryption group parameters from the control component code shares payload. [node: %s]",
										node), e);
					}
				});
	}

	/**
	 * Gets the encryption group parameters of the setup component tally data payloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return a stream of the setup component tally data payloads' encryption group parameters.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public Stream<EncryptionGroupParameters> getFromSetupComponentTallyDataPayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		return pathNode.getRegexPaths().stream()
				.map(vcsIdPath -> pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_TALLY_DATA, vcsIdPath))
				.map(setupComponentTallyDataPath -> {
					try {
						return objectMapper.readValue(setupComponentTallyDataPath.getPath().toFile(),
								EncryptionGroupParametersPayload.class).gqGroup();
					} catch (final IOException e) {
						throw new UncheckedIOException(String.format(
								"Failed to deserialize the encryption group parameters from the setup component tally data payload. [path: %s]",
								setupComponentTallyDataPath), e);
					}
				});
	}
}
