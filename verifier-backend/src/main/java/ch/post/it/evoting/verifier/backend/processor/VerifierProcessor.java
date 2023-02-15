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

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.verifier.backend.dto.DatasetConfiguration;
import ch.post.it.evoting.verifier.backend.dto.Verification;
import ch.post.it.evoting.verifier.backend.event.PreSetupEvent;
import ch.post.it.evoting.verifier.backend.event.PreTallyEvent;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.backend.mapper.VerificationMapper;
import ch.post.it.evoting.verifier.backend.tools.Dataset;
import ch.post.it.evoting.verifier.backend.tools.DatasetExtractionException;
import ch.post.it.evoting.verifier.backend.tools.DatasetService;

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
	private final AtomicInteger finishedVerificationCounter = new AtomicInteger(0);
	private Dataset dataset;
	private DatasetConfiguration datasetConfiguration;
	private List<Verification> verifications;
	private long processingVerificationCount = 0;

	public VerifierProcessor(final ApplicationContext applicationContext, final ApplicationEventPublisher applicationEventPublisher,
			DatasetService datasetService) {
		this.applicationContext = applicationContext;
		this.applicationEventPublisher = applicationEventPublisher;
		this.datasetService = datasetService;
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
		final List<Verification> result = new LinkedList<>(verifications);
		result.sort(Comparator.comparing(Verification::getBlock).thenComparingInt(Verification::getVerificationId));

		return result;
	}

	public void resetExecution() {
		init();
	}

	public DatasetConfiguration getDatasetConfiguration() {
		return datasetConfiguration;
	}

	public void setDataset(final byte[] file, final String filename) throws DatasetExtractionException {
		checkNotNull(file);
		checkNotNull(filename);

		this.dataset = new Dataset(file);

		// Get election event id and number of voters from election event context.
		final ElectionEventContext electionEventContext = datasetService.extractElectionEventContext(this.dataset);

		// Get election event name, election event date, number of elections, number of votes, number of non-test ballot boxes, number of test
		// ballot boxes, total number of authorized non-test voters and total number of test voters
		final Configuration configuration = datasetService.extractConfiguration(this.dataset);

		final String electionEventId = electionEventContext.electionEventId();
		final Map<Boolean, Integer> testBallotBoxToTotalNumberOfVoters = electionEventContext.verificationCardSetContexts().stream()
				.collect(Collectors.partitioningBy(
						VerificationCardSetContext::testBallotBox,
						Collectors.summingInt(VerificationCardSetContext::numberOfVotingCards)));

		// Get the direct trust certificate fingerprints.
		final Map<String, String> aliasesToFingerprints = datasetService.extractFingerprints();

		final String datasetHash = DigestUtils.sha256Hex(file).toUpperCase();

		final String electionEventName = configuration.getContest().getContestIdentification();

		final XMLGregorianCalendar xmlElectionEventDate = configuration.getContest().getContestDate();
		LocalDate electionEventDate = LocalDate.of(xmlElectionEventDate.getYear(), xmlElectionEventDate.getMonth(), xmlElectionEventDate.getDay());
		String formattedelectionEventDate = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH).format(electionEventDate);

		final int numberOfElections = configuration.getContest().getElectionInformation().size();
		final int numberOfVotes = configuration.getContest().getVoteInformation().size();

		final long numberOfNonTestBallotBoxes = configuration.getAuthorizations().getAuthorization().stream().parallel()
				.filter(authorizationType -> !authorizationType.isAuthorizationTest())
				.count();

		final long numberOfTestBallotBoxes = configuration.getAuthorizations().getAuthorization().stream().parallel()
				.filter(AuthorizationType::isAuthorizationTest)
				.count();

		this.datasetConfiguration = new DatasetConfiguration(filename, String.join(":", datasetHash.split("(?<=\\G.{2})")), electionEventId,
				aliasesToFingerprints, electionEventName, formattedelectionEventDate, numberOfElections, numberOfVotes, numberOfNonTestBallotBoxes,
				numberOfTestBallotBoxes, testBallotBoxToTotalNumberOfVoters.get(false), testBallotBoxToTotalNumberOfVoters.get(true));
	}

	public void process(String runOption) throws IOException {
		checkNotNull(dataset, "A dataset must be uploaded before running the process");

		final Path inputDirectory = datasetService.unpack(dataset).getUnpackFolder()
				.orElseThrow(() -> new IllegalStateException("The dataset could not be unpacked"));
		LOGGER.debug("The input directory is {}", inputDirectory);

		resetRunningCounter();
		switch (runOption) {
		case PRE_SETUP -> {
			addToRunningCounter(Set.of(PRE_SETUP));
			applicationEventPublisher.publishEvent(new PreSetupEvent(this, inputDirectory.toString()));
		}
		case SETUP -> {
			addToRunningCounter(Set.of(SETUP));
			applicationEventPublisher.publishEvent(new SetupEvent(this, inputDirectory.toString()));
		}
		case PRE_TALLY -> {
			addToRunningCounter(Set.of(PRE_TALLY));
			applicationEventPublisher.publishEvent(new PreTallyEvent(this, inputDirectory.toString()));
		}
		case TALLY -> {
			addToRunningCounter(Set.of(TALLY));
			applicationEventPublisher.publishEvent(new TallyEvent(this, inputDirectory.toString()));
		}
		default -> LOGGER.error("Unknown event: {}", runOption);
		}
	}

	private void resetRunningCounter() {
		processingVerificationCount = 0;
		finishedVerificationCounter.set(0);
	}

	private void addToRunningCounter(Set<String> events) {
		processingVerificationCount += this.getVerifications().stream().filter(v -> v.getVerifierEvents().stream().anyMatch(events::contains))
				.count();
	}

	@Async
	@EventListener(VerificationResultEvent.class)
	public void verificationListener(final VerificationResultEvent event) {
		final boolean containsPreSetup = event.getVerificationResult().getVerificationDefinition().getVerifierEvents().contains(PRE_SETUP);
		final boolean containsPreTally = event.getVerificationResult().getVerificationDefinition().getVerifierEvents().contains(PRE_TALLY);

		if (!containsPreSetup && !containsPreTally && finishedVerificationCounter.incrementAndGet() == processingVerificationCount) {
			datasetService.clean(dataset);
		}
	}
}
