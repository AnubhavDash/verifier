/*
 * (c) Copyright 2024 Swiss Post Ltd.
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
package ch.post.it.evoting.verifier.backend.tools;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ech.xmlns.ech_0222._1.Delivery;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.evotinglibraries.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.SetupComponentPublicKeysPayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.evotinglibraries.domain.validations.FailedValidationException;
import ch.post.it.evoting.evotinglibraries.xml.XmlFileRepository;
import ch.post.it.evoting.evotinglibraries.xml.XsdConstants;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.verifier.backend.dataextractors.ControlComponentPublicKeysPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.dataextractors.ElectionEventContextPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.dataextractors.SetupComponentTallyDataPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;

@Service
public class ElectionDataExtractionService {

	private final PathService pathService;
	private final ObjectMapper objectMapper;
	private final ElectionEventContextPayloadDataExtractor electionEventContextPayloadDataExtractor;
	private final XmlFileRepository<Delivery> ech0222XmlFileRepository;
	private final XmlFileRepository<Configuration> configurationXmlFileRepository;
	private final ControlComponentPublicKeysPayloadDataExtractor controlComponentPublicKeysPayloadDataExtractor;
	private final SetupComponentTallyDataPayloadDataExtractor setupComponentTallyDataPayloadDataExtractor;

	public ElectionDataExtractionService(
			final PathService pathService,
			final ObjectMapper objectMapper,
			final XmlFileRepository<Delivery> ech0222XmlFileRepository,
			final XmlFileRepository<Configuration> configurationXmlFileRepository,
			final ElectionEventContextPayloadDataExtractor electionEventContextPayloadDataExtractor,
			final ControlComponentPublicKeysPayloadDataExtractor controlComponentPublicKeysPayloadDataExtractor,
			final SetupComponentTallyDataPayloadDataExtractor setupComponentTallyDataPayloadDataExtractor) {
		this.pathService = pathService;
		this.objectMapper = objectMapper;
		this.electionEventContextPayloadDataExtractor = electionEventContextPayloadDataExtractor;
		this.ech0222XmlFileRepository = ech0222XmlFileRepository;
		this.configurationXmlFileRepository = configurationXmlFileRepository;
		this.controlComponentPublicKeysPayloadDataExtractor = controlComponentPublicKeysPayloadDataExtractor;
		this.setupComponentTallyDataPayloadDataExtractor = setupComponentTallyDataPayloadDataExtractor;
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
	 * Gets the tally component eCH-0222.
	 *
	 * @param inputDirectoryPath the root directory containing project files.
	 * @return the tally component eCH-0222 as {@link Delivery} found in the project files, at the expected location if it
	 * exists.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the file cannot be deserialized to a Delivery.
	 */
	public Delivery getTallyComponentEch0222(final Path inputDirectoryPath) {
		final PathNode deliveryPathNode = pathService.buildFromRootPath(StructureKey.TALLY_COMPONENT_ECH0222, inputDirectoryPath);
		return ech0222XmlFileRepository.read(deliveryPathNode.getPath(), XsdConstants.TALLY_COMPONENT_ECH_0222, Delivery.class);
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

		final ImmutableList<Path> ballotBoxPayloadPaths =
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
								.collect(toImmutableList()))
				.flatMap(ImmutableList::stream);
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

		final ImmutableList<Path> controlComponentShufflePayloadPaths = pathService.buildFromDynamicAncestorPath(
				StructureKey.CONTROL_COMPONENT_SHUFFLE,
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

		final PathNode verificationCardSet = pathService.buildFromRootPath(StructureKey.CONTEXT_VERIFICATION_CARD_SETS_DIR, inputDirectoryPath);

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

		final PathNode verificationCardSets = pathService.buildFromRootPath(StructureKey.CONTEXT_VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

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
	 * Gets all the control components public keys payloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return all control components public keys payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the deserialization of the control components public keys payloads fails.
	 */
	public ImmutableList<ControlComponentPublicKeysPayload> getControlComponentPublicKeysPayloads(final Path inputDirectoryPath) {
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
				.collect(toImmutableList());
	}

	/**
	 * Gets the election event context payload data extraction.
	 *
	 * @param inputDirectoryPath the root directory containing project files.
	 * @return a data extraction of the election event context payload found in the project files, at the expected location if it exists.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the file cannot be read through.
	 */
	public ElectionEventContextPayloadDataExtractor.DataExtraction getElectionEventContextPayloadDataExtraction(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode electionEventContextPathNode = pathService.buildFromRootPath(StructureKey.ELECTION_EVENT_CONTEXT, inputDirectoryPath);

		return electionEventContextPayloadDataExtractor.load(electionEventContextPathNode.getPath());
	}

	/**
	 * Gets all the control components public keys payloads data extractions.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return all control components public keys payloads data extractions.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if any file cannot be read through.
	 */
	public Stream<ControlComponentPublicKeysPayloadDataExtractor.DataExtraction> getControlComponentPublicKeysPayloadsDataExtractions(
			final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode controlComponentPublicKeys = pathService.buildFromRootPath(StructureKey.CONTROL_COMPONENT_PUBLIC_KEYS, inputDirectoryPath);

		return controlComponentPublicKeys.getRegexPaths().stream()
				.parallel()
				.map(controlComponentPublicKeysPayloadDataExtractor::load);
	}

	/**
	 * Gets all setup component tally data payloads data extractions of the different verification card sets as a {@link Stream}.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return all setup component tally data payloads data extractions.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if any file cannot be read through.
	 */
	public Stream<SetupComponentTallyDataPayloadDataExtractor.DataExtraction> getAllSetupComponentTallyDataPayloadsDataExtractions(
			final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode verificationCardSets = pathService.buildFromRootPath(StructureKey.CONTEXT_VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		return verificationCardSets.getRegexPaths().stream()
				.parallel()
				.flatMap(this::getSetupComponentTallyDataPayloadsDataExtractions);
	}

	/**
	 * Gets the setup component tally data payloads data extractions, given a path for a verification card set ID as a {@link Stream}.
	 *
	 * @param verificationCardSetIdPath the path for the verification card set ID.
	 * @return the setup component tally data payloads data extractions.
	 * @throws NullPointerException if {@code verificationCardSetIdPath} is null.
	 * @throws UncheckedIOException if any file cannot be read through.
	 */
	public Stream<SetupComponentTallyDataPayloadDataExtractor.DataExtraction> getSetupComponentTallyDataPayloadsDataExtractions(
			final Path verificationCardSetIdPath) {
		checkNotNull(verificationCardSetIdPath);

		final PathNode nodePath = pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_TALLY_DATA, verificationCardSetIdPath);

		return nodePath.getRegexPaths().stream()
				.parallel()
				.map(setupComponentTallyDataPayloadDataExtractor::load);
	}

	/**
	 * Gets all the verification card set paths from the context dataset.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return all verification card set paths from the context.
	 */
	public ImmutableList<Path> getContextVerificationCardSetPaths(final Path inputDirectoryPath) {
		return pathService.buildFromRootPath(StructureKey.CONTEXT_VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath).getRegexPaths();
	}
}
