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
package ch.post.it.evoting.verifier.backend.processor;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingdecrypt.ElectionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingdecrypt.Results;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingdecrypt.VoteType;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.dto.DatasetConfiguration;
import ch.post.it.evoting.verifier.backend.dto.Verification;
import ch.post.it.evoting.verifier.backend.event.PreSetupEvent;
import ch.post.it.evoting.verifier.backend.event.PreTallyEvent;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.mapper.VerificationMapper;
import ch.post.it.evoting.verifier.backend.tools.Dataset;
import ch.post.it.evoting.verifier.backend.tools.DatasetExtractionException;
import ch.post.it.evoting.verifier.backend.tools.DatasetService;
import ch.post.it.evoting.verifier.backend.tools.DirectoryService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;

@Component
public class VerifierProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(VerifierProcessor.class);

	private static final String SETUP = SetupEvent.TYPE;
	private static final String TALLY = TallyEvent.TYPE;
	private static final String PRE_SETUP = PreSetupEvent.TYPE;
	private static final String PRE_TALLY = PreTallyEvent.TYPE;
	private final ApplicationContext applicationContext;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final DatasetService datasetService;
	private final ElectionDataExtractionService electionDataExtractionService;
	private final DirectoryService directoryService;
	private Dataset dataset;
	private DatasetConfiguration datasetConfiguration;
	private List<Verification> verifications;

	public VerifierProcessor(final ApplicationContext applicationContext,
			final ApplicationEventPublisher applicationEventPublisher,
			final DatasetService datasetService,
			final ElectionDataExtractionService electionDataExtractionService,
			final DirectoryService directoryService) {
		this.applicationContext = applicationContext;
		this.applicationEventPublisher = applicationEventPublisher;
		this.datasetService = datasetService;
		this.electionDataExtractionService = electionDataExtractionService;
		this.directoryService = directoryService;
	}

	@PostConstruct
	private void init() {
		final Map<String, AbstractVerification> verificationBeans = applicationContext.getBeansOfType(AbstractVerification.class);
		LOGGER.debug("Found {} beans of type AbstractVerification.", verificationBeans.size());

		verifications = verificationBeans.values().stream()
				.map(AbstractVerification::getVerificationDefinition)
				.map(VerificationMapper.INSTANCE::map)
				.toList();
	}

	public List<Verification> getVerifications() {
		final List<Verification> result = new ArrayList<>(verifications);
		result.sort(Comparator.comparing(Verification::getBlock)
				.thenComparing((o1, o2) -> {
					double id1 = Double.parseDouble(o1.getVerificationId());
					double id2 = Double.parseDouble(o2.getVerificationId());
					return Double.compare(id1, id2);
				}));

		return result;
	}

	public void resetExecution() {
		init();
	}

	public DatasetConfiguration getDatasetConfiguration() {
		return datasetConfiguration;
	}

	public void setDataset(final InputStream datasetInputStream, final String filename) throws DatasetExtractionException {
		checkNotNull(datasetInputStream);
		checkNotNull(filename);

		final Path tempDirectory;
		try {
			tempDirectory = directoryService.createSecuredTemporaryDirectory("verifier-dataset");
		} catch (final IOException e) {
			throw new DatasetExtractionException("Could not create secured temporary directory for dataset extraction.");
		}

		LOGGER.debug("Secured temporary directory successfully created. [directory: {}]", tempDirectory);

		if (this.dataset != null) {
			datasetService.clean(dataset);
		}

		this.dataset = new Dataset(datasetInputStream, tempDirectory);

		LOGGER.info("Dataset successfully downloaded.");

		final Path inputDirectory;
		try {
			inputDirectory = datasetService.unpack(dataset).getUnpackFolder();
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}

		LOGGER.info("Dataset successfully unpacked.");

		// Get election event id and number of voters from election event context.
		final ElectionEventContext electionEventContext = electionDataExtractionService.getElectionEventContext(inputDirectory);

		// Get election event name, election event date, number of elections, number of votes, number of non-test ballot boxes, number of test
		// ballot boxes, total number of authorized non-test voters and total number of test voters
		final Configuration configuration = electionDataExtractionService.getCantonConfig(inputDirectory);

		final String electionEventId = electionEventContext.electionEventId();
		final Map<Boolean, Integer> testBallotBoxToTotalNumberOfVoters = electionEventContext.verificationCardSetContexts().stream()
				.collect(Collectors.partitioningBy(
						VerificationCardSetContext::testBallotBox,
						Collectors.summingInt(VerificationCardSetContext::numberOfVotingCards)));

		// Get the direct trust certificate fingerprints.
		final Map<String, String> aliasesToFingerprints = datasetService.extractFingerprints();

		final String datasetHash;
		try (final InputStream unpackedDatasetInputStream = dataset.newInputStream()) {
			datasetHash = DigestUtils.sha256Hex(unpackedDatasetInputStream).toUpperCase();
		} catch (final IOException e) {
			throw new DatasetExtractionException("Failed to digest given dataset.");
		}

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

		final long numberOfNonTestBallotBoxes = configuration.getAuthorizations().getAuthorization().stream().parallel()
				.filter(authorizationType -> !authorizationType.isAuthorizationTest())
				.count();

		final long numberOfTestBallotBoxes = configuration.getAuthorizations().getAuthorization().stream().parallel()
				.filter(AuthorizationType::isAuthorizationTest)
				.count();

		Integer numberOfConfirmedNonTestVotes = null;
		Integer numberOfConfirmedTestVotes = null;
		if (electionDataExtractionService.existsTallyComponentDecrypt(inputDirectory)) {
			final Results tallyComponentDecrypt = electionDataExtractionService.getTallyComponentDecrypt(inputDirectory);

			numberOfConfirmedNonTestVotes = getNumberOfConfirmedVotes(configuration, tallyComponentDecrypt, false);
			numberOfConfirmedTestVotes = getNumberOfConfirmedVotes(configuration, tallyComponentDecrypt, true);
		}

		this.datasetConfiguration = new DatasetConfiguration(filename, String.join(":", datasetHash.split("(?<=\\G.{2})")), electionEventId,
				aliasesToFingerprints, electionEventName, formattedElectionEventDate, numberOfElections, numberOfVotes, numberOfNonTestBallotBoxes,
				numberOfTestBallotBoxes, testBallotBoxToTotalNumberOfVoters.get(false), testBallotBoxToTotalNumberOfVoters.get(true),
				numberOfConfirmedNonTestVotes, numberOfConfirmedTestVotes);
	}

	public void process(final String runOption) {
		checkNotNull(dataset, "A dataset must be uploaded before running the process.");
		checkState(dataset.isUnpacked(), "A dataset must be unpacked before running the process.");

		final Path inputDirectory = dataset.getUnpackFolder();

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
		if (this.dataset != null) {
			datasetService.clean(dataset);
			this.dataset = null;
		}
	}

	private static int getNumberOfConfirmedVotes(final Configuration configuration, final Results tallyComponentDecrypt,
			final boolean testAuthorizations) {

		return configuration.getAuthorizations().getAuthorization().stream().parallel()
				.filter(authorizationType -> testAuthorizations == authorizationType.isAuthorizationTest())
				.map(AuthorizationType::getAuthorizationIdentification)
				.map(nonTestAuthorizationIdentification -> tallyComponentDecrypt.getBallotsBox().stream().parallel()
						.filter(bb -> bb.getBallotBoxIdentification().equals(nonTestAuthorizationIdentification))
						.collect(MoreCollectors.onlyElement()))
				.map(nonTestBallotBox -> nonTestBallotBox.getCountingCircle().stream().parallel()
						.map(countingCircle -> countingCircle.getDomainOfInfluence().stream().parallel()
								.map(domainOfInfluence -> {
									final List<VoteType> voteList = domainOfInfluence.getVote();
									final List<ElectionType> electionList = domainOfInfluence.getElection();
									final boolean hasVotes = Objects.nonNull(voteList) && !voteList.isEmpty();
									final boolean hasElections = Objects.nonNull(electionList) && !electionList.isEmpty();

									if (hasVotes) {
										return voteList.get(0).getBallot().size();
									} else {
										if (hasElections) {
											return electionList.get(0).getBallot().size();
										} else {
											return 0;
										}
									}
								}).reduce(0, Math::addExact)
						).reduce(0, Math::addExact)
				).reduce(0, Math::addExact);
	}
}
