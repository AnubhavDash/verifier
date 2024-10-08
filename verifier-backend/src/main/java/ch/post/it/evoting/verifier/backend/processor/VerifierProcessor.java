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
package ch.post.it.evoting.verifier.backend.processor;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableByteArray;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.domain.encryption.StreamedEncryptionDecryptionService;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.evotinglibraries.domain.validations.PasswordValidation;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.dto.DatasetConfiguration;
import ch.post.it.evoting.verifier.backend.dto.DatasetConfigurationContext;
import ch.post.it.evoting.verifier.backend.dto.DatasetConfigurationSetup;
import ch.post.it.evoting.verifier.backend.dto.DatasetConfigurationTally;
import ch.post.it.evoting.verifier.backend.dto.Verification;
import ch.post.it.evoting.verifier.backend.event.PreSetupEvent;
import ch.post.it.evoting.verifier.backend.event.PreTallyEvent;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.mapper.VerificationMapper;
import ch.post.it.evoting.verifier.backend.tools.Dataset;
import ch.post.it.evoting.verifier.backend.tools.DatasetExtractionException;
import ch.post.it.evoting.verifier.backend.tools.DatasetService;
import ch.post.it.evoting.verifier.backend.tools.DatasetType;
import ch.post.it.evoting.verifier.backend.tools.DirectoryService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;

@Component
public class VerifierProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(VerifierProcessor.class);
	private static final ImmutableByteArray ASSOCIATED_DATA = ImmutableByteArray.EMPTY;
	private static final String SETUP = SetupEvent.TYPE;
	private static final String TALLY = TallyEvent.TYPE;
	private static final String PRE_SETUP = PreSetupEvent.TYPE;
	private static final String PRE_TALLY = PreTallyEvent.TYPE;
	private final ApplicationContext applicationContext;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final DatasetService datasetService;
	private final ElectionDataExtractionService electionDataExtractionService;
	private final DirectoryService directoryService;
	private final StreamedEncryptionDecryptionService streamedEncryptionDecryptionService;
	private final char[] importDecryptionPassword;
	private Dataset contextDataset;
	private Dataset setupDataset;
	private Dataset tallyDataset;
	private DatasetConfigurationContext datasetConfigurationContext;
	private DatasetConfigurationSetup datasetConfigurationSetup;
	private DatasetConfigurationTally datasetConfigurationTally;
	private ImmutableList<Verification> verifications;

	public VerifierProcessor(final ApplicationContext applicationContext,
			final ApplicationEventPublisher applicationEventPublisher,
			final DatasetService datasetService,
			final ElectionDataExtractionService electionDataExtractionService,
			final DirectoryService directoryService,
			final StreamedEncryptionDecryptionService streamedEncryptionDecryptionService,
			@Value("${import.zip.decryption.password}")
			final char[] importDecryptionPassword) {
		this.applicationContext = applicationContext;
		this.applicationEventPublisher = applicationEventPublisher;
		this.datasetService = datasetService;
		this.electionDataExtractionService = electionDataExtractionService;
		this.directoryService = directoryService;
		this.streamedEncryptionDecryptionService = streamedEncryptionDecryptionService;
		PasswordValidation.validate(importDecryptionPassword, "import decryption");
		this.importDecryptionPassword = importDecryptionPassword;

	}

	@PostConstruct
	private void init() {
		final Map<String, AbstractVerification> verificationBeans = applicationContext.getBeansOfType(AbstractVerification.class);
		LOGGER.debug("Found {} beans of type AbstractVerification.", verificationBeans.size());

		verifications = verificationBeans.values().stream()
				.map(AbstractVerification::getVerificationDefinition)
				.map(VerificationMapper.INSTANCE::map)
				.sorted(Comparator.comparing(Verification::getBlock)
						.thenComparing((o1, o2) -> {
							final double id1 = Double.parseDouble(o1.getVerificationId());
							final double id2 = Double.parseDouble(o2.getVerificationId());
							return Double.compare(id1, id2);
						}))
				.collect(toImmutableList());
	}

	public ImmutableList<Verification> getVerifications() {
		return verifications;
	}

	public void resetExecution() {
		init();
	}

	public DatasetConfiguration getDatasetConfiguration() {
		return new DatasetConfiguration(datasetConfigurationContext, datasetConfigurationSetup, datasetConfigurationTally);
	}

	public void setDataset(final String filename, final DatasetType datasetType, final Path filePath)
			throws DatasetExtractionException {
		checkNotNull(filename);
		checkNotNull(datasetType);
		checkNotNull(filePath);

		switch (datasetType) {
		case CONTEXT -> setDatasetContext(filename, filePath);
		case SETUP -> setDatasetSetup(filename, filePath);
		case TALLY -> setDatasetTally(filename, filePath);
		default -> throw new IllegalArgumentException("The dataset type does not exist.");
		}
	}

	private void setDatasetContext(final String filename, final Path filePath) throws DatasetExtractionException {
		checkNotNull(filename);
		checkNotNull(filePath);

		if (this.contextDataset != null) {
			datasetService.clean(contextDataset, false);
		}
		this.setupDataset = null;
		this.tallyDataset = null;
		this.datasetConfigurationSetup = null;
		this.datasetConfigurationTally = null;

		final Path directory;
		try {
			directory = directoryService.createSecuredDirectory();
		} catch (final IOException e) {
			throw new DatasetExtractionException("Could not create secured directory for dataset extraction.");
		}

		LOGGER.debug("Secured directory successfully created for dataset. [directory: {}]", directory);

		try (final InputStream datasetInputStream = Files.newInputStream(filePath)) {
			this.contextDataset = downloadDataset(datasetInputStream, directory, DatasetType.CONTEXT);
		} catch (final IOException e) {
			throw new DatasetExtractionException("Could not download context dataset.");
		}

		final Path inputDirectory = unpackDataset(contextDataset);

		// Get election event id and number of voters from election event context.
		final ElectionEventContext electionEventContext = electionDataExtractionService.getElectionEventContext(inputDirectory);

		final String electionEventId = electionEventContext.electionEventId();
		final Map<Boolean, Integer> testBallotBoxToTotalNumberOfVoters = electionEventContext.verificationCardSetContexts().stream()
				.collect(Collectors.partitioningBy(
						VerificationCardSetContext::isTestBallotBox,
						Collectors.summingInt(VerificationCardSetContext::getNumberOfVotingCards)));

		// Get election event name, election event date, number of elections, number of votes, number of non-test ballot boxes, number of test
		// ballot boxes, total number of authorized non-test voters and total number of test voters.
		final Configuration configuration = electionDataExtractionService.getCantonConfig(inputDirectory);

		// Get election event seed.
		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(inputDirectory);
		final String electionEventSeed = electionEventContextPayload.getSeed();

		// Get the direct trust certificate fingerprints.
		final ImmutableMap<String, String> aliasesToFingerprints = datasetService.extractFingerprints();

		LOGGER.info("Dataset digest successfully computed.");

		final String electionEventName = configuration.getContest().getContestIdentification();

		final XMLGregorianCalendar xmlElectionEventDate = configuration.getContest().getContestDate();
		final LocalDate electionEventDate = LocalDate.of(xmlElectionEventDate.getYear(), xmlElectionEventDate.getMonth(),
				xmlElectionEventDate.getDay());
		final String formattedElectionEventDate = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH).format(electionEventDate);

		final int numberOfElections = BigInteger.valueOf(configuration.getContest().getElectionGroupBallot().stream().parallel()
				.map(ElectionGroupBallotType::getElectionInformation)
				.mapToLong(Collection::size)
				.sum()).intValueExact();

		final int numberOfVotes = configuration.getContest().getVoteInformation().size();

		final int numberOfBallots = configuration.getContest().getVoteInformation().stream().parallel()
				.map(VoteInformationType::getVote)
				.flatMap(voteType -> voteType.getBallot().stream())
				.mapToInt(ballotType -> ballotType == null ? 0 : 1)
				.reduce(0, Math::addExact);

		final int numberOfNonTestBallotBoxes = Math.toIntExact(configuration.getAuthorizations().getAuthorization().stream().parallel()
				.filter(authorizationType -> !authorizationType.isAuthorizationTest())
				.count());

		final int numberOfTestBallotBoxes = Math.toIntExact(configuration.getAuthorizations().getAuthorization().stream().parallel()
				.filter(AuthorizationType::isAuthorizationTest)
				.count());

		final String datasetHash;
		try {
			datasetHash = DigestUtils.sha256Hex(Files.newInputStream(filePath)).toLowerCase(Locale.ENGLISH);
		} catch (final IOException e) {
			throw new DatasetExtractionException("Could not digest given context dataset.");
		}

		this.datasetConfigurationContext = new DatasetConfigurationContext.Builder()
				.setFilename(filename)
				.setHash(datasetHash.toLowerCase(Locale.ENGLISH))
				.setElectionEventId(electionEventId)
				.setAliasesToFingerprints(aliasesToFingerprints)
				.setElectionEventName(electionEventName)
				.setElectionEventSeed(electionEventSeed)
				.setElectionEventDate(formattedElectionEventDate)
				.setNumberOfElections(numberOfElections)
				.setNumberOfVotes(numberOfVotes)
				.setNumberOfBallots(numberOfBallots)
				.setNumberOfNonTestBallotBoxes(numberOfNonTestBallotBoxes)
				.setNumberOfTestBallotBoxes(numberOfTestBallotBoxes)
				.setTotalNumberOfAuthorizedNonTestVoters(testBallotBoxToTotalNumberOfVoters.get(false))
				.setTotalNumberOfTestVoters(testBallotBoxToTotalNumberOfVoters.get(true))
				.build();
	}

	private void setDatasetSetup(final String filename, final Path filePath) throws DatasetExtractionException {
		checkNotNull(filename);
		checkNotNull(filePath);
		checkNotNull(datasetConfigurationContext, "A context dataset must be uploaded first.");

		if (this.setupDataset != null) {
			datasetService.clean(setupDataset, false);
		}

		try (final InputStream datasetInputStream = Files.newInputStream(filePath)) {
			this.setupDataset = downloadDataset(datasetInputStream, contextDataset.getUnpackFolder(), DatasetType.SETUP);
		} catch (final IOException e) {
			throw new DatasetExtractionException("Could not download setup dataset.");
		}

		checkNotNull(unpackDataset(setupDataset));

		final String datasetHash;
		try {
			datasetHash = DigestUtils.sha256Hex(Files.newInputStream(filePath)).toLowerCase(Locale.ENGLISH);
		} catch (final IOException e) {
			throw new DatasetExtractionException("Could not digest given setup dataset.");
		}

		this.datasetConfigurationSetup = new DatasetConfigurationSetup(filename, datasetHash.toLowerCase(Locale.ENGLISH));
	}

	private void setDatasetTally(final String filename, final Path filePath) throws DatasetExtractionException {
		checkNotNull(filename);
		checkNotNull(filePath);
		checkNotNull(datasetConfigurationContext, "A context dataset must be uploaded first.");

		if (tallyDataset != null) {
			datasetService.clean(tallyDataset, false);
		}

		try (final InputStream datasetInputStream = Files.newInputStream(filePath)) {
			tallyDataset = downloadDataset(datasetInputStream, contextDataset.getUnpackFolder(), DatasetType.TALLY);
		} catch (final IOException e) {
			throw new DatasetExtractionException("Could not download tally dataset.");
		}

		unpackDataset(tallyDataset);

		final int numberOfConfirmedNonTestVotes = 0;
		final int numberOfConfirmedTestVotes = 0;

		final String datasetHash;
		try {
			datasetHash = DigestUtils.sha256Hex(Files.newInputStream(filePath)).toLowerCase(Locale.ENGLISH);
		} catch (final IOException e) {
			throw new DatasetExtractionException("Could not digest given tally dataset.");
		}

		this.datasetConfigurationTally = new DatasetConfigurationTally(filename, datasetHash.toLowerCase(Locale.ENGLISH),
				numberOfConfirmedNonTestVotes, numberOfConfirmedTestVotes);
	}

	private Dataset downloadDataset(final InputStream datasetInputStream, final Path directory, final DatasetType datasetType) {

		final InputStream decryptedStream = streamedEncryptionDecryptionService.decrypt(datasetInputStream, importDecryptionPassword,
				ASSOCIATED_DATA);

		LOGGER.info("Dataset successfully downloaded.");

		return new Dataset(decryptedStream, directory, datasetType);
	}

	private Path unpackDataset(final Dataset dataset) {
		final Path inputDirectory;
		try {
			inputDirectory = datasetService.unpack(dataset).getUnpackFolder();
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}

		LOGGER.info("Dataset successfully unpacked.");

		return inputDirectory;
	}

	public void process(final String runOption) {
		checkNotNull(contextDataset, "A context dataset must be uploaded before running the process.");
		checkState(Objects.nonNull(setupDataset) || Objects.nonNull(tallyDataset),
				"Either a setup or tally dataset must be uploaded before running the process.");
		checkState(contextDataset.isUnpacked(), "A context dataset must be unpacked before running the process.");
		checkState((Objects.nonNull(setupDataset) && setupDataset.isUnpacked()) || (Objects.nonNull(tallyDataset) && tallyDataset.isUnpacked()),
				"Either a setup or tally dataset must be unpacked before running the process.");

		// the context, setup and tally dataset are unpacked in the same folder.
		final Path inputDirectory = contextDataset.getUnpackFolder();

		LOGGER.debug("The input directory is {}", inputDirectory);

		switch (runOption) {
		case PRE_SETUP -> applicationEventPublisher.publishEvent(new PreSetupEvent(this, inputDirectory.toString()));
		case SETUP -> applicationEventPublisher.publishEvent(new SetupEvent(this, inputDirectory.toString()));
		case PRE_TALLY -> applicationEventPublisher.publishEvent(new PreTallyEvent(this, inputDirectory.toString()));
		case TALLY -> applicationEventPublisher.publishEvent(new TallyEvent(this, inputDirectory.toString()));
		default -> LOGGER.error("Unknown event: {}", runOption);
		}
	}

	@PreDestroy
	public void clean() {
		if (this.contextDataset != null) {
			datasetService.clean(contextDataset, false);
			this.contextDataset = null;
		}
		if (this.setupDataset != null) {
			datasetService.clean(setupDataset, false);
			this.setupDataset = null;
		}
		if (this.tallyDataset != null) {
			datasetService.clean(tallyDataset, false);
			this.tallyDataset = null;
		}
	}

	public void cleanSetupTally() {
		this.setupDataset = null;
		this.tallyDataset = null;
	}
}
