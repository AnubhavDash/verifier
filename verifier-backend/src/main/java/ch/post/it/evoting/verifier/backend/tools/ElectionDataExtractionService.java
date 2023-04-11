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

import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.post.it.evoting.cryptoprimitives.domain.election.CombinedCorrectnessInformation;
import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.EncryptionParametersPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.SetupComponentPublicKeysPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ControlComponentCodeSharesPayload;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SetupComponentVerificationDataPayload;
import ch.post.it.evoting.cryptoprimitives.domain.validations.FailedValidationException;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.evotinglibraries.xml.XmlFileRepository;
import ch.post.it.evoting.evotinglibraries.xml.XsdConstants;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingdecrypt.Results;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.protocol.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.verifier.protocol.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

@Service
public class ElectionDataExtractionService {

	private final PathService pathService;
	private final ObjectMapper objectMapper;
	private final XmlFileRepository<Delivery> ech0110XmlFileRepository;
	private final XmlFileRepository<ch.ech.xmlns.ech_0222._1.Delivery> ech0222XmlFileRepository;
	private final XmlFileRepository<Configuration> configurationXmlFileRepository;
	private final XmlFileRepository<Results> resultsXmlFileRepository;

	public ElectionDataExtractionService(
			final PathService pathService,
			final ObjectMapper objectMapper,
			final XmlFileRepository<Delivery> ech0110XmlFileRepository,
			final XmlFileRepository<ch.ech.xmlns.ech_0222._1.Delivery> ech0222XmlFileRepository,
			final XmlFileRepository<Configuration> configurationXmlFileRepository,
			final XmlFileRepository<Results> resultsXmlFileRepository) {
		this.pathService = pathService;
		this.objectMapper = objectMapper;
		this.ech0110XmlFileRepository = ech0110XmlFileRepository;
		this.ech0222XmlFileRepository = ech0222XmlFileRepository;
		this.configurationXmlFileRepository = configurationXmlFileRepository;
		this.resultsXmlFileRepository = resultsXmlFileRepository;
	}

	/**
	 * Gets the canton config.
	 *
	 * @param inputDirectoryPath the root directory containing project files.
	 * @return the canton config as {@link Configuration} found in the project files, at the expected location if it exists.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the file cannot be deserialized to a Configuration.
	 */
	public Configuration getCantonConfig(final Path inputDirectoryPath) {
		final PathNode configurationPathNode = pathService.buildFromRootPath(StructureKey.CONFIGURATION_ANONYMIZED, inputDirectoryPath);
		return configurationXmlFileRepository.read(configurationPathNode.getPath(), XsdConstants.CANTON_CONFIG_XSD, Configuration.class);
	}

	/**
	 * Gets the tally component decrypt.
	 *
	 * @param inputDirectoryPath the root directory containing project files.
	 * @return the tally component decrypt as {@link Results} found in the project files, at the expected location if it exists.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the file cannot be deserialized to a Results.
	 */
	public Results getTallyComponentDecrypt(final Path inputDirectoryPath) {
		final PathNode resultsPathNode = pathService.buildFromRootPath(StructureKey.TALLY_COMPONENT_DECRYPT, inputDirectoryPath);
		return resultsXmlFileRepository.read(resultsPathNode.getPath(), XsdConstants.TALLY_COMPONENT_DECRYPT_XSD, Results.class);
	}

	/**
	 * @param inputDirectoryPath the root directory containing project files.
	 * @return if the tally component decrypt exists return true, otherwise false.
	 */
	public boolean existsTallyComponentDecrypt(final Path inputDirectoryPath) {
		return pathService.existsFromRootPath(StructureKey.TALLY_COMPONENT_DECRYPT, inputDirectoryPath);
	}

	/**
	 * Gets the tally component eCH-0110.
	 *
	 * @param inputDirectoryPath the root directory containing project files.
	 * @return the tally component eCH-0110 as {@link Delivery} found in the project files, at the expected location if it exists.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the file cannot be deserialized to a Delivery.
	 */
	public Delivery getTallyComponentEch0110(final Path inputDirectoryPath) {
		final PathNode deliveryPathNode = pathService.buildFromRootPath(StructureKey.TALLY_COMPONENT_ECH0110, inputDirectoryPath);
		return ech0110XmlFileRepository.read(deliveryPathNode.getPath(), XsdConstants.TALLY_COMPONENT_ECH_0110, Delivery.class);
	}

	/**
	 * Gets the tally component eCH-0222.
	 *
	 * @param inputDirectoryPath the root directory containing project files.
	 * @return the tally component eCH-0222 as {@link ch.ech.xmlns.ech_0222._1.Delivery} found in the project files, at the expected location if it
	 * exists.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the file cannot be deserialized to a Delivery.
	 */
	public ch.ech.xmlns.ech_0222._1.Delivery getTallyComponentEch0222(final Path inputDirectoryPath) {
		final PathNode deliveryPathNode = pathService.buildFromRootPath(StructureKey.TALLY_COMPONENT_ECH0222, inputDirectoryPath);
		return ech0222XmlFileRepository.read(deliveryPathNode.getPath(), XsdConstants.TALLY_COMPONENT_ECH_0222,
				ch.ech.xmlns.ech_0222._1.Delivery.class);
	}

	/**
	 * Gets the encryption parameters payload.
	 *
	 * @param inputDirectoryPath the root directory containing project files.
	 * @return the encryption parameters payload found in the project files, at the expected location if it exists.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the file cannot be deserialized to an EncryptionParametersPayload.
	 */
	public EncryptionParametersPayload getEncryptionParametersPayload(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode encryptionParametersPathNode = pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);
		try {
			return objectMapper.readValue(encryptionParametersPathNode.getPath().toFile(), EncryptionParametersPayload.class);
		} catch (final IOException e) {
			throw new UncheckedIOException("Failed to deserialize encryption parameters.", e);
		}
	}

	/**
	 * Gets the election event context payload.
	 *
	 * @param inputDirectoryPath the root directory containing project files.
	 * @return the election event context payload found in the project files, at the expected location if it exists.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the file cannot be deserialized to an ElectionEventContextPayload.
	 */
	public ElectionEventContextPayload getElectionEventContextPayload(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode electionEventContextPathNode = pathService.buildFromRootPath(StructureKey.ELECTION_EVENT_CONTEXT, inputDirectoryPath);
		try {
			return objectMapper.readValue(electionEventContextPathNode.getPath().toFile(), ElectionEventContextPayload.class);
		} catch (final IOException e) {
			throw new UncheckedIOException("Failed to deserialize election event.", e);
		}
	}

	/**
	 * Gets the setup component public keys payload.
	 *
	 * @param inputDirectoryPath the root directory containing project files.
	 * @return the setup component public keys payload found in the project files, at the expected location if it exists.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the file cannot be deserialized to a SetupComponentPublicKeysPayload.
	 */
	public SetupComponentPublicKeysPayload getSetupComponentPublicKeysPayload(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode setupComponentPublicKeysPathNode = pathService.buildFromRootPath(StructureKey.SETUP_COMPONENT_PUBLIC_KEYS, inputDirectoryPath);
		try {
			return objectMapper.readValue(setupComponentPublicKeysPathNode.getPath().toFile(), SetupComponentPublicKeysPayload.class);
		} catch (final IOException e) {
			throw new UncheckedIOException("Failed to deserialize election event.", e);
		}
	}

	/**
	 * Gets the election event context.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return the election event context for the given id.
	 * @throws NullPointerException      if {@code inputDirectoryPath} is null.
	 * @throws FailedValidationException if {@code electionEventId} is invalid.
	 * @throws UncheckedIOException      if the deserialization of the election event context fails.
	 */
	public ElectionEventContext getElectionEventContext(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode electionEventContextPayloadPath = pathService.buildFromRootPath(StructureKey.ELECTION_EVENT_CONTEXT, inputDirectoryPath);
		try {
			return objectMapper.readValue(electionEventContextPayloadPath.getPath().toFile(), ElectionEventContextPayload.class)
					.getElectionEventContext();
		} catch (final IOException e) {
			throw new UncheckedIOException(
					String.format("Failed to deserialize election event context. [path:%s ]", electionEventContextPayloadPath.getPath()), e);
		}

	}

	/**
	 * Gets all control component ballot box payloads of the different ballot boxes, ordered by node id as a {@link Stream}.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return the control component ballot box payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the deserialization of any control component ballot box fails.
	 */
	public Stream<ControlComponentBallotBoxPayload> getAllControlComponentBallotBoxPayloadsOrderedByNodeId(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		return getControlComponentBallotBoxPayloadsByBallotBox(inputDirectoryPath)
				.parallel()
				.flatMap(Function.identity())
				.sorted(Comparator.comparingInt(ControlComponentBallotBoxPayload::getNodeId));
	}

	/**
	 * Gets all control component ballot box payloads of the different ballot boxes grouped by ballot boxes as a {@link Stream}.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return the control component ballot box payloads by ballot boxes.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the deserialization of any control component ballot box fails.
	 */
	private Stream<Stream<ControlComponentBallotBoxPayload>> getControlComponentBallotBoxPayloadsByBallotBox(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode ballotBoxes = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxes.getRegexPaths().stream()
				.parallel()
				.map(ballotBox -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_BALLOT_BOX, ballotBox))
				.map(controlComponentBallotBoxes ->
						controlComponentBallotBoxes.getRegexPaths().stream()
								.parallel()
								.map(controlComponentBallotBox -> {
									try {
										return objectMapper.readValue(controlComponentBallotBox.toFile(), ControlComponentBallotBoxPayload.class);
									} catch (final IOException e) {
										throw new UncheckedIOException(
												String.format("Failed to deserialize control component ballot box payload. [path: %s]",
														controlComponentBallotBox), e);
									}
								})
								.sorted(Comparator.comparingInt(ControlComponentBallotBoxPayload::getNodeId)));
	}

	/**
	 * Gets all control component ballot box payloads for the given ballot box, ordered by node id as a {@link Stream}.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @param ballotBoxId        the ballot box id for which to get the control component ballot box payloads.
	 * @return the control component ballot box payloads.
	 * @throws NullPointerException      if {@code inputDirectoryPath} is null.
	 * @throws FailedValidationException if {@code ballotBoxId} is invalid.
	 * @throws UncheckedIOException      if the deserialization of any control component ballot box fails.
	 */
	public Stream<ControlComponentBallotBoxPayload> getControlComponentBallotBoxPayloadsOrderedByNodeId(final Path inputDirectoryPath,
			final String ballotBoxId) {
		checkNotNull(inputDirectoryPath);
		validateUUID(ballotBoxId);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOXES_DIR, inputDirectoryPath);

		final Path ballotBoxIdPath = pathNode.getPath().resolve(ballotBoxId);
		return getControlComponentBallotBoxPayloadsOrderedByNodeId(ballotBoxIdPath);
	}

	/**
	 * Gets the control component ballot box payloads contained in the ballot box directory, ordered by node id as a {@link Stream}.
	 *
	 * @param ballotBoxDirectoryPath the path of a ballot box containing the control component ballot box payloads.
	 * @throws NullPointerException if {@code ballotBoxDirectoryPath} is null.
	 * @throws UncheckedIOException if the control component ballot box payloads could not be read.
	 */
	public Stream<ControlComponentBallotBoxPayload> getControlComponentBallotBoxPayloadsOrderedByNodeId(final Path ballotBoxDirectoryPath) {
		checkNotNull(ballotBoxDirectoryPath);

		final List<Path> ballotBoxPayloadPaths =
				pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_BALLOT_BOX, ballotBoxDirectoryPath).getRegexPaths();

		return ballotBoxPayloadPaths.stream()
				.parallel()
				.map(path -> {
					try {
						return objectMapper.readValue(path.toFile(), ControlComponentBallotBoxPayload.class);
					} catch (final IOException e) {
						throw new UncheckedIOException(String.format("Failed to deserialize control component ballot box payload. [path: %s]", path),
								e);
					}
				})
				.sorted(Comparator.comparingInt(ControlComponentBallotBoxPayload::getNodeId));
	}

	/**
	 * Gets all control component shuffle payloads of the different ballot boxes ordered by node id as a {@link Stream}.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return the control component shuffle payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the deserialization of any control component shuffle fails.
	 */
	public Stream<ControlComponentShufflePayload> getAllControlComponentShufflePayloadsOrderedByNodeId(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode ballotBoxes = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxes.getRegexPaths().stream()
				.parallel()
				.map(ballotBox -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_SHUFFLE, ballotBox))
				.map(controlComponentShuffles ->
						controlComponentShuffles.getRegexPaths().stream()
								.map(controlComponentShuffle -> {
									try {
										return objectMapper.readValue(controlComponentShuffle.toFile(), ControlComponentShufflePayload.class);
									} catch (final IOException e) {
										throw new UncheckedIOException(
												String.format("Failed to deserialize control component shuffle payload. [path: %s]",
														controlComponentShuffle), e);
									}
								}).sorted(Comparator.comparingInt(ControlComponentShufflePayload::getNodeId))
								.toList())
				.flatMap(List::stream);
	}

	/**
	 * Gets all control component shuffle payloads for the given ballot box order by node id as a {@link Stream}.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @param ballotBoxId        the ballot box id for which to get the control component shuffle payloads.
	 * @return the control component shuffle payloads.
	 * @throws NullPointerException      if {@code inputDirectoryPath} is null.
	 * @throws FailedValidationException if {@code ballotBoxId} is invalid.
	 * @throws UncheckedIOException      if the deserialization of any control component shuffle payloads fails.
	 */
	public Stream<ControlComponentShufflePayload> getControlComponentShufflePayloadsOrderedByNodeId(final Path inputDirectoryPath,
			final String ballotBoxId) {
		checkNotNull(inputDirectoryPath);
		validateUUID(ballotBoxId);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOXES_DIR, inputDirectoryPath);

		final Path ballotBoxIdPath = pathNode.getPath().resolve(ballotBoxId);
		return getControlComponentShufflePayloadsOrderedByNodeId(ballotBoxIdPath);
	}

	/**
	 * Gets the control component shuffle payloads contained in the ballot box directory ordered by node id as a {@link Stream}.
	 *
	 * @param ballotBoxDirectoryPath the path of a ballot box containing the control component shuffle payloads.
	 * @throws NullPointerException if {@code ballotBoxDirectoryPath} is null.
	 * @throws UncheckedIOException if the control component shuffle payloads could not be read.
	 */
	public Stream<ControlComponentShufflePayload> getControlComponentShufflePayloadsOrderedByNodeId(final Path ballotBoxDirectoryPath) {
		checkNotNull(ballotBoxDirectoryPath);

		final List<Path> controlComponentShufflePayloadPaths = pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_SHUFFLE,
				ballotBoxDirectoryPath).getRegexPaths();

		return controlComponentShufflePayloadPaths.stream()
				.parallel()
				.map(path -> {
					try {
						return objectMapper.readValue(path.toFile(), ControlComponentShufflePayload.class);
					} catch (final IOException e) {
						throw new UncheckedIOException(String.format("Failed to deserialize control component shuffle payload. [path: %s]", path), e);
					}
				})
				.sorted(Comparator.comparingInt(ControlComponentShufflePayload::getNodeId));
	}

	public SetupComponentTallyDataPayload getSetupComponentTallyDataPayload(final Path verificationCardSetIdPath) {
		checkNotNull(verificationCardSetIdPath);

		final PathNode nodePath = pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_TALLY_DATA, verificationCardSetIdPath);

		try {
			return objectMapper.readValue(nodePath.getPath().toFile(), SetupComponentTallyDataPayload.class);
		} catch (final IOException e) {
			throw new UncheckedIOException(
					String.format("Failed to deserialize SetupComponentTallyDataPayload. [verificationCardSetIdPath: %s]", verificationCardSetIdPath),
					e);
		}
	}

	/**
	 * Gets the setup component tally data payload for the given verification card set.
	 *
	 * @param inputDirectoryPath    the dataset root directory.
	 * @param verificationCardSetId the verification card set id for which to get setup component tally data payload.
	 * @return the setup component tally data payload.
	 * @throws NullPointerException      if {@code inputDirectoryPath} is null.
	 * @throws FailedValidationException if {@code verificationCardSetId} is invalid.
	 * @throws UncheckedIOException      if the deserialization of the setup component tally data payload fails.
	 */
	public SetupComponentTallyDataPayload getSetupComponentTallyDataPayload(final Path inputDirectoryPath, final String verificationCardSetId) {
		checkNotNull(inputDirectoryPath);
		validateUUID(verificationCardSetId);

		final PathNode verificationCardSet = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SETS_DIR, inputDirectoryPath);

		final Path verificationCardSetIdPath = verificationCardSet.getPath().resolve(verificationCardSetId);
		final Path tallyDataPath = pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_TALLY_DATA, verificationCardSetIdPath)
				.getPath();

		try {
			return objectMapper.readValue(tallyDataPath.toFile(), SetupComponentTallyDataPayload.class);
		} catch (final IOException e) {
			throw new UncheckedIOException(String.format("Failed to deserialize setup component tally data payload. [path: %s]", tallyDataPath), e);
		}
	}

	/**
	 * Gets all setup component tally data payloads of the different verification card sets as a {@link Stream}.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return all setup component tally data payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the deserialization of the setup component tally data payloads fails.
	 */
	public Stream<SetupComponentTallyDataPayload> getSetupComponentTallyDataPayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode verificationCardSets = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		return verificationCardSets.getRegexPaths().stream()
				.parallel()
				.map(verificationCardSet -> pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_TALLY_DATA, verificationCardSet))
				.map(tallyData -> {
					try {
						return objectMapper.readValue(tallyData.getPath().toFile(), SetupComponentTallyDataPayload.class);
					} catch (final IOException e) {
						throw new UncheckedIOException(
								String.format("Failed to deserialize setup component tally data payload. [path: %s]", tallyData.getPath()), e);
					}
				});
	}

	/**
	 * Gets the combined correctness information for the given verification card set.
	 *
	 * @param inputDirectoryPath    the dataset root directory.
	 * @param verificationCardSetId the verification card set id for which to get setup component tally data payload.
	 * @return the combined correctness information.
	 * @throws NullPointerException      if {@code inputDirectoryPath} is null.
	 * @throws FailedValidationException if {@code verificationCardSetId} is invalid.
	 * @throws UncheckedIOException      if the deserialization of the setup component verification data payload fails.
	 */
	public CombinedCorrectnessInformation getCombinedCorrectnessInformation(final Path inputDirectoryPath, final String verificationCardSetId) {
		checkNotNull(inputDirectoryPath);
		validateUUID(verificationCardSetId);

		final PathNode verificationCardSet = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SETS_DIR, inputDirectoryPath);

		final Path verificationCardSetIdPath = verificationCardSet.getPath().resolve(verificationCardSetId);
		// All chunks contain the same combined correctness information.
		final Path setupComponentVerificationDataPath = pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_VERIFICATION_DATA,
				verificationCardSetIdPath).getRegexPaths().get(0);

		try {
			return objectMapper.readValue(setupComponentVerificationDataPath.toFile(), SetupComponentVerificationDataPayload.class)
					.getCombinedCorrectnessInformation();
		} catch (final IOException e) {
			throw new UncheckedIOException(
					String.format("Failed to deserialize combined correctness information. [path: %s]", setupComponentVerificationDataPath), e);
		}
	}

	/**
	 * Gets all tally component shuffle payloads of the different ballot boxes as a {@link Stream}.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return all tally component shuffle payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the deserialization of the tally component shuffle payloads fails.
	 */
	public Stream<TallyComponentShufflePayload> getTallyComponentShufflePayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode ballotBoxes = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxes.getRegexPaths()
				.stream()
				.parallel()
				.map(bbId -> getTallyComponentShufflePayload(inputDirectoryPath, bbId.toFile().getName()));
	}

	/**
	 * Deserializes the TallyComponentShufflePayload contained in the ballot box directory.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @param ballotBoxId        the ballot box id for which to get the tally component shuffle payload.
	 * @throws UncheckedIOException if the tally component shuffle payload could not be read from the file.
	 */
	public TallyComponentShufflePayload getTallyComponentShufflePayload(final Path inputDirectoryPath, final String ballotBoxId) {
		checkNotNull(inputDirectoryPath);
		validateUUID(ballotBoxId);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOXES_DIR, inputDirectoryPath);
		final Path ballotBoxDirectoryPath = pathNode.getPath().resolve(ballotBoxId);

		return getTallyComponentShufflePayload(ballotBoxDirectoryPath);
	}

	/**
	 * Gets the tally component shuffle payload contained in the ballot box directory.
	 *
	 * @param ballotBoxDirectoryPath the path of a ballot box containing a tally component shuffle payload.
	 * @throws NullPointerException if {@code ballotBoxDirectoryPath} is null.
	 * @throws UncheckedIOException if the tally component shuffle payload could not be read.
	 */
	public TallyComponentShufflePayload getTallyComponentShufflePayload(final Path ballotBoxDirectoryPath) {
		checkNotNull(ballotBoxDirectoryPath);

		final PathNode tallyComponentShufflePayload = pathService.buildFromDynamicAncestorPath(StructureKey.TALLY_COMPONENT_SHUFFLE,
				ballotBoxDirectoryPath);

		try {
			return objectMapper.readValue(tallyComponentShufflePayload.getPath().toFile(), TallyComponentShufflePayload.class);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Gets all tally component votes payloads of the different ballot boxes as a {@link Stream}.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return all tally component votes payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the deserialization of the tally component votes payloads fails.
	 */
	public Stream<TallyComponentVotesPayload> getTallyComponentVotesPayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode ballotBoxes = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxes.getRegexPaths()
				.stream()
				.parallel()
				.map(this::getTallyComponentVotesPayload);
	}

	/**
	 * Gets the tally component votes payload for the given ballot box directory.
	 *
	 * @param ballotBoxDirectoryPath the path of a ballot box containing a tally component votes payload.
	 * @throws NullPointerException if {@code ballotBoxDirectoryPath} is null.
	 * @throws UncheckedIOException if the tally component votes payload could not be read.
	 */
	public TallyComponentVotesPayload getTallyComponentVotesPayload(final Path ballotBoxDirectoryPath) {
		checkNotNull(ballotBoxDirectoryPath);

		final PathNode tallyComponentVotesPayload = pathService.buildFromDynamicAncestorPath(StructureKey.TALLY_COMPONENT_VOTES,
				ballotBoxDirectoryPath);

		try {
			return objectMapper.readValue(tallyComponentVotesPayload.getPath().toFile(), TallyComponentVotesPayload.class);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Gets the tally component votes data payload for the given ballot box.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @param ballotBoxId        the ballot box id for which to get tally component votes payload.
	 * @return tally component votes payload.
	 * @throws NullPointerException      if {@code inputDirectoryPath} is null.
	 * @throws FailedValidationException if {@code ballotBoxId} is invalid.
	 * @throws UncheckedIOException      if the deserialization of the tally component votes payload fails.
	 */
	public TallyComponentVotesPayload getTallyComponentVotesPayload(final Path inputDirectoryPath, final String ballotBoxId) {
		checkNotNull(inputDirectoryPath);
		validateUUID(ballotBoxId);

		final PathNode ballotBox = pathService.buildFromRootPath(StructureKey.BALLOT_BOXES_DIR, inputDirectoryPath);

		final Path ballotBoxIdPath = ballotBox.getPath().resolve(ballotBoxId);
		final Path tallyComponentVotesPath = pathService.buildFromDynamicAncestorPath(StructureKey.TALLY_COMPONENT_VOTES, ballotBoxIdPath)
				.getPath();

		try {
			return objectMapper.readValue(tallyComponentVotesPath.toFile(), TallyComponentVotesPayload.class);
		} catch (final IOException e) {
			throw new UncheckedIOException(
					String.format("Failed to deserialize tally component votes payload. [path: %s]", tallyComponentVotesPath), e);
		}
	}

	/**
	 * Gets all setup component verification data payloads of the different verification card sets ordered by chunk id as a {@link Stream}.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return all setup component verification data payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the deserialization of the setup component verification data payloads fails.
	 */
	public Stream<SetupComponentVerificationDataPayload> getSetupComponentVerificationDataPayloadsOrderByChunkId(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode verificationCardSets = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		return verificationCardSets.getRegexPaths()
				.stream()
				.parallel()
				.flatMap(this::deserializeSetupComponentVerificationDataPayloadOrderByChunkId)
				.sorted(Comparator.comparingInt(SetupComponentVerificationDataPayload::getChunkId));
	}

	/**
	 * Deserializes the setup component verification data payloads chunks, given a path for a verification card set ID ordered by chunk id as a
	 * {@link Stream}.
	 *
	 * @param verificationCardSetIdPath the path for the verification card set ID.
	 * @return List of {@code SetupComponentVerificationDataPayload} each corresponding to a chunk.
	 * @throws NullPointerException if {@code verificationCardSetIdPath} is null.
	 */
	public Stream<SetupComponentVerificationDataPayload> deserializeSetupComponentVerificationDataPayloadOrderByChunkId(
			final Path verificationCardSetIdPath) {
		checkNotNull(verificationCardSetIdPath);

		final PathNode nodePath = pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_VERIFICATION_DATA, verificationCardSetIdPath);

		return nodePath.getRegexPaths().stream()
				.parallel()
				.map(this::getSetupComponentVerificationDataPayload)
				.sorted(Comparator.comparingInt(SetupComponentVerificationDataPayload::getChunkId));
	}

	public SetupComponentVerificationDataPayload getSetupComponentVerificationDataPayload(final Path setupComponentVerificationDataPayloadPath) {
		checkNotNull(setupComponentVerificationDataPayloadPath);

		try {
			return objectMapper.readValue(setupComponentVerificationDataPayloadPath.toFile(), SetupComponentVerificationDataPayload.class);
		} catch (final IOException e) {
			throw new UncheckedIOException(
					String.format("Failed to deserialize SetupComponentVerificationDataPayload payload. [filePath: %s]",
							setupComponentVerificationDataPayloadPath), e);
		}
	}

	/**
	 * Gets all control component code shares payloads of the different verification card sets ordered by node id as a {@link Stream}.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return all control component code shares payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the deserialization of the control component code shares payloads fails.
	 */
	public Stream<ControlComponentCodeSharesPayload> getControlComponentCodeSharesPayloadsOrderedByNodeId(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		return getControlComponentCodeSharesPayloadsByChunkAndVcs(inputDirectoryPath)
				.flatMap(Function.identity())
				.sorted(Comparator.comparingInt(ControlComponentCodeSharesPayload::getNodeId));
	}

	/**
	 * Gets all control component code shares payloads of the different verification card sets as a {@link Stream}.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return all control component code shares payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the deserialization of the control component code shares payloads fails.
	 */
	public Stream<ControlComponentCodeSharesPayload> getControlComponentCodeSharesPayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode verificationCardSets = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);
		return verificationCardSets.getRegexPaths().stream()
				.parallel()
				.flatMap(verificationCardSetIdPath -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_CODE_SHARES,
						verificationCardSetIdPath).getRegexPaths().stream().parallel())
				.flatMap(this::getControlComponentCodeShares);
	}

	/**
	 * Deserializes and returns the control component code shares payloads for each chunk, given a path for a verification card set ID, ordered by
	 * chunk id and node id.
	 *
	 * @param verificationCardSetIdPath the path for the verification card set ID.
	 * @return List of {@code ReturnCodeGenerationResponsePayload} for each chunk.
	 * @throws NullPointerException if {@code verificationCardSetIdPath} is null.
	 */
	public List<List<ControlComponentCodeSharesPayload>> deserializeControlComponentCodeSharesPayloadsOrderByChunkIdAndNodeId(
			final Path verificationCardSetIdPath) {
		checkNotNull(verificationCardSetIdPath);

		final PathNode nodePath = pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_CODE_SHARES, verificationCardSetIdPath);
		final List<Path> filePaths = nodePath.getRegexPaths();
		return filePaths.stream()
				.parallel()
				.map(this::getControlComponentCodeSharesOrderByNodeId)
				.map(Stream::toList)
				.sorted(Comparator.comparingInt(controlComponentCodeSharesPayloads -> controlComponentCodeSharesPayloads.get(0).getChunkId()))
				.toList();
	}

	/**
	 * Gets all control component code shares payloads of the different verification card sets as a {@link Stream}.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return all control component code shares payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the deserialization of the control component code shares payloads fails.
	 */
	public Stream<Stream<ControlComponentCodeSharesPayload>> getControlComponentCodeSharesPayloadsByChunkAndVcs(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode verificationCardSets = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);
		return verificationCardSets.getRegexPaths().stream()
				.parallel()
				.flatMap(verificationCardSetIdPath -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_CODE_SHARES,
								verificationCardSetIdPath).getRegexPaths()
						.stream()
						.parallel())
				.map(this::getControlComponentCodeSharesOrderByNodeId);
	}

	public Stream<ControlComponentCodeSharesPayload> getControlComponentCodeSharesOrderByNodeId(final Path controlComponentCodeSharesPayloadsPath) {
		checkNotNull(controlComponentCodeSharesPayloadsPath);

		return getControlComponentCodeShares(controlComponentCodeSharesPayloadsPath)
				.parallel()
				.sorted(Comparator.comparingInt(ControlComponentCodeSharesPayload::getNodeId));
	}

	private Stream<ControlComponentCodeSharesPayload> getControlComponentCodeShares(final Path controlComponentCodeSharesPayloadsPath) {
		checkNotNull(controlComponentCodeSharesPayloadsPath);

		try {
			return Arrays.stream(objectMapper.readValue(controlComponentCodeSharesPayloadsPath.toFile(), ControlComponentCodeSharesPayload[].class));
		} catch (final IOException e) {
			throw new UncheckedIOException(
					String.format("Failed to deserialize ControlComponentCodeShares payloads. [filePath: %s]",
							controlComponentCodeSharesPayloadsPath), e);
		}
	}

	/**
	 * Gets all the control components public keys payloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return all control components public keys payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the deserialization of the control components public keys payloads fails.
	 */
	public List<ControlComponentPublicKeysPayload> getControlComponentPublicKeysPayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode controlComponentPublicKeys = pathService.buildFromRootPath(StructureKey.CONTROL_COMPONENT_PUBLIC_KEYS, inputDirectoryPath);

		return controlComponentPublicKeys
				.getRegexPaths()
				.stream()
				.parallel()
				.map(path -> {
					try {
						return objectMapper.readValue(path.toFile(), ControlComponentPublicKeysPayload.class);
					} catch (final IOException e) {
						throw new UncheckedIOException("Failed to deserialize control component public keys payload.", e);
					}
				})
				.toList();
	}

}
