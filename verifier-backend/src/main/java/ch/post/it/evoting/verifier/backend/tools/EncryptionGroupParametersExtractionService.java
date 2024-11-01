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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.verifier.backend.dataextractors.EncryptionGroupParametersDataExtractor;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;

@Service
public class EncryptionGroupParametersExtractionService {

	private final PathService pathService;
	private final EncryptionGroupParametersDataExtractor encryptionGroupParametersDataExtractor;

	public EncryptionGroupParametersExtractionService(final PathService pathService,
			final EncryptionGroupParametersDataExtractor encryptionGroupParametersDataExtractor) {
		this.pathService = pathService;
		this.encryptionGroupParametersDataExtractor = encryptionGroupParametersDataExtractor;
	}

	/**
	 * Gets the encryption group parameters from the election event context.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return the encryption group parameters of the election event context.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public EncryptionGroupParametersDataExtractor.DataExtraction getFromElectionEventContext(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode electionEventContextPayloadPath = pathService.buildFromRootPath(StructureKey.ELECTION_EVENT_CONTEXT, inputDirectoryPath);

		return encryptionGroupParametersDataExtractor.load(electionEventContextPayloadPath.getPath());
	}

	/**
	 * Gets the encryption group parameters of all control component public key payloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return a stream of the encryption group parameters of the control component public key payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public Stream<EncryptionGroupParametersDataExtractor.DataExtraction> getFromControlComponentPublicKeys(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode controlComponentPublicKeyPayloadPaths = pathService.buildFromRootPath(StructureKey.CONTROL_COMPONENT_PUBLIC_KEYS,
				inputDirectoryPath);
		return controlComponentPublicKeyPayloadPaths.getRegexPaths().stream()
				.parallel()
				.map(encryptionGroupParametersDataExtractor::load);
	}

	/**
	 * Gets the encryption group parameters of all control component ballot box payloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return a stream of the encryption group parameters of the control component ballot box payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public Stream<EncryptionGroupParametersDataExtractor.DataExtraction> getFromControlComponentBallotBoxPayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return pathNode.getRegexPaths().stream()
				.flatMap(ballotBoxIdPath -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_BALLOT_BOX, ballotBoxIdPath)
						.getRegexPaths()
						.stream())
				.parallel()
				.map(encryptionGroupParametersDataExtractor::load);
	}

	/**
	 * Gets the encryption group parameters of all control component shuffle payloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return a stream of the encryption group parameters of the control component shuffle payloads.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public Stream<EncryptionGroupParametersDataExtractor.DataExtraction> getFromControlComponentShufflePayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return pathNode.getRegexPaths().stream()
				.flatMap(ballotBoxIdPath -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_SHUFFLE, ballotBoxIdPath)
						.getRegexPaths()
						.stream())
				.parallel()
				.map(encryptionGroupParametersDataExtractor::load);
	}

	/**
	 * Gets the encryption group parameters of the TallyComponentShufflePayload.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public Stream<EncryptionGroupParametersDataExtractor.DataExtraction> getFromTallyComponentShufflePayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return pathNode.getRegexPaths().stream()
				.parallel()
				.map(ballotBoxPayloadPath -> pathService.buildFromDynamicAncestorPath(StructureKey.TALLY_COMPONENT_SHUFFLE, ballotBoxPayloadPath)
						.getPath())
				.map(encryptionGroupParametersDataExtractor::load);
	}

	/**
	 * Gets the encryption group parameters of the TallyComponentVotesPayloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public Stream<EncryptionGroupParametersDataExtractor.DataExtraction> getFromTallyComponentVotesPayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return pathNode.getRegexPaths().stream()
				.parallel()
				.map(ballotBoxPayloadPath -> pathService.buildFromDynamicAncestorPath(StructureKey.TALLY_COMPONENT_VOTES, ballotBoxPayloadPath)
						.getPath())
				.map(encryptionGroupParametersDataExtractor::load);

	}

	/**
	 * Gets the encryption group parameters of the setup component tally data payloads.
	 *
	 * @param inputDirectoryPath the dataset root directory.
	 * @return a stream of the setup component tally data payloads' encryption group parameters.
	 * @throws NullPointerException if {@code inputDirectoryPath} is null.
	 * @throws UncheckedIOException if the extraction fails.
	 */
	public Stream<EncryptionGroupParametersDataExtractor.DataExtraction> getFromSetupComponentTallyDataPayloads(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		final PathNode pathNode = pathService.buildFromRootPath(StructureKey.CONTEXT_VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		return pathNode.getRegexPaths().stream()
				.parallel()
				.map(vcsIdPath -> pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_TALLY_DATA, vcsIdPath).getPath())
				.map(encryptionGroupParametersDataExtractor::load);
	}
}
