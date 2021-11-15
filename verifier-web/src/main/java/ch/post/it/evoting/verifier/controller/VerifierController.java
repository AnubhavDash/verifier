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
package ch.post.it.evoting.verifier.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.event.Block1Event;
import ch.post.it.evoting.verifier.common.event.Block2Event;
import ch.post.it.evoting.verifier.common.event.Block3Event;
import ch.post.it.evoting.verifier.common.event.Block4Event;
import ch.post.it.evoting.verifier.common.event.PreDecryptionEvent;
import ch.post.it.evoting.verifier.dto.Configuration;
import ch.post.it.evoting.verifier.dto.Verification;
import ch.post.it.evoting.verifier.processor.VerifierProcessor;

@RestController
@RequestMapping("/api/")
public class VerifierController {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifierController.class);

	private final VerifierProcessor processor;
	private final ApplicationEventPublisher applicationEventPublisher;

	VerifierController(final VerifierProcessor processor, final ApplicationEventPublisher applicationEventPublisher) {
		this.processor = processor;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@GetMapping("/ping")
	public boolean ping() {
		return true;
	}

	@GetMapping("/shutdown")
	public void shutdown() {
		final var executor = new ScheduledThreadPoolExecutor(1);
		final long DELAY_BEFORE_EXECUTION = 10;
		executor.schedule(() -> System.exit(0), DELAY_BEFORE_EXECUTION, TimeUnit.MILLISECONDS);
	}

	@PostMapping("/reset")
	public void reset() {
		this.processor.resetExecution();
	}

	@GetMapping(value = "/verifications/*.pdf", produces = "application/pdf")
	public byte[] generatePdf(Locale locale) {
		return this.processor.generatePdf(getLanguage(locale));
	}

	@GetMapping(value = "/configurationInputDirectory")
	public Configuration getConfiguration() {
		return this.processor.getConfiguration();
	}

	@PostMapping("/configurationInputDirectory")
	public void setConfigurationInputDirectory(
			@RequestBody
					Configuration value) {
		this.processor.setConfiguration(value);
	}

	@GetMapping("/verifications")
	public List<Verification> getTestStatus() {
		return this.processor.getVerifications();
	}

	@PostMapping("/verifications")
	public ResponseEntity<String> process(
			@RequestParam()
			final String runOptions) {

		final String inputDirectory = this.processor.getConfiguration().getInputDirectory();
		LOGGER.debug("The input directory is {}", inputDirectory);

		final Set<String> events = new HashSet<>(Arrays.asList(runOptions.split(",")));
		for (String event : events) {
			switch (event) {
			case "PRE_DECRYPTION":
				applicationEventPublisher.publishEvent(new PreDecryptionEvent(this, inputDirectory));
				break;
			case "BLOCK_1":
				applicationEventPublisher.publishEvent(new Block1Event(this, inputDirectory));
				break;
			case "BLOCK_2":
				applicationEventPublisher.publishEvent(new Block2Event(this, inputDirectory));
				break;
			case "BLOCK_3":
				applicationEventPublisher.publishEvent(new Block3Event(this, inputDirectory));
				break;
			case "BLOCK_4":
				applicationEventPublisher.publishEvent(new Block4Event(this, inputDirectory));
				break;
			default:
				LOGGER.error("Unknown event: {}", event);
				break;
			}
		}

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	private Language getLanguage(Locale locale) {
		var language = Language.DE;
		final Optional<Language> optLanguage = Arrays.stream(Language.values())
				.filter(l -> l.getLocale().getLanguage().equalsIgnoreCase(locale.getLanguage()))
				.findFirst();
		if (optLanguage.isPresent()) {
			language = optLanguage.get();
		}
		return language;
	}

}
