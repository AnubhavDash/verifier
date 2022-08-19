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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.dto.DatasetConfiguration;
import ch.post.it.evoting.verifier.backend.dto.Verification;
import ch.post.it.evoting.verifier.backend.event.ConfigurationEvent;
import ch.post.it.evoting.verifier.backend.event.DecryptionEvent;
import ch.post.it.evoting.verifier.backend.event.PreConfigurationEvent;
import ch.post.it.evoting.verifier.backend.event.PreDecryptionEvent;
import ch.post.it.evoting.verifier.backend.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.backend.mapper.VerificationMapper;
import ch.post.it.evoting.verifier.backend.tools.Dataset;
import ch.post.it.evoting.verifier.backend.tools.DatasetService;

@Component
public class VerifierProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifierProcessor.class);
	private static final String CONFIGURATION = ConfigurationEvent.TYPE;
	private static final String DECRYPTION = DecryptionEvent.TYPE;
	private static final String PRE_CONFIGURATION = PreConfigurationEvent.TYPE;
	public static final String PRE_DECRYPTION = PreDecryptionEvent.TYPE;
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

	public void setDataset(byte[] file, String filename) {
		checkNotNull(file, "zip file must be not null");
		checkNotNull(filename, "filename must be not null");

		this.dataset = new Dataset(file);
		this.datasetConfiguration = new DatasetConfiguration(filename, DigestUtils.sha256Hex(file).toUpperCase());
	}

	public void process(String runOptions) throws IOException {
		checkNotNull(dataset, "A dataset must be uploaded before running the process");

		final Path inputDirectory = datasetService.unpack(dataset).getUnpackFolder()
				.orElseThrow(() -> new IllegalStateException("The dataset could not be unpacked"));
		LOGGER.debug("The input directory is {}", inputDirectory);

		final Set<String> events = new HashSet<>(Arrays.asList(runOptions.split(",")));
		resetRunningCounter();
		for (String event : events) {
			switch (event) {
			case CONFIGURATION -> {
				addToRunningCounter(Set.of(CONFIGURATION, PRE_CONFIGURATION));
				applicationEventPublisher.publishEvent(new PreConfigurationEvent(this, inputDirectory.toString()));
				applicationEventPublisher.publishEvent(new ConfigurationEvent(this, inputDirectory.toString()));
			}
			case DECRYPTION -> {
				addToRunningCounter(Set.of(DECRYPTION, PRE_DECRYPTION));
				applicationEventPublisher.publishEvent(new PreDecryptionEvent(this, inputDirectory.toString()));
				applicationEventPublisher.publishEvent(new DecryptionEvent(this, inputDirectory.toString()));
			}
			default -> LOGGER.error("Unknown event: {}", event);
			}
		}
	}

	private void resetRunningCounter() {
		processingVerificationCount = 0;
		finishedVerificationCounter.set(0);
	}

	private void addToRunningCounter(Set<String> events) {
		processingVerificationCount += this.getVerifications().stream().filter(v -> v.getVerifierEvents().stream().anyMatch(events::contains)).count();
	}

	@Async
	@EventListener(VerificationResultEvent.class)
	public void verificationListener() {
		if (finishedVerificationCounter.incrementAndGet() == processingVerificationCount) {
			datasetService.clean(dataset);
		}
	}
}
