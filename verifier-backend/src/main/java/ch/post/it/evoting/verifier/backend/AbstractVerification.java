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
package ch.post.it.evoting.verifier.backend;

import java.nio.file.Path;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import ch.post.it.evoting.verifier.backend.event.VerifierEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;

/**
 * Class to be extended by all verifications that want to be discovered and run by the Verifier.
 */
@Component
public abstract class AbstractVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVerification.class);

	private final ResultPublisherService resultPublisherService;

	protected AbstractVerification(final ResultPublisherService resultPublisherService) {
		this.resultPublisherService = resultPublisherService;
	}

	/**
	 * Listens asynchronously to all {@link VerifierEvent} and executes the {@link AbstractVerification#verify(Path)} method depending on the
	 * sub-class' {@link VerifierEvent}s.
	 *
	 * @param verifierEvent The event to which the method is listening to.
	 */
	@Async
	@EventListener({ VerifierEvent.class })
	public void executeVerify(final VerifierEvent verifierEvent) {
		// Retrieve events to which this verification is listening to.
		final VerificationDefinition definition = getVerificationDefinition();
		final Set<String> listenedEvents = definition.getVerifierEvents();

		// Execute verification only if received event is one listened to.
		if (listenedEvents.contains(verifierEvent.getType())) {
			final StopWatch stopWatch = new StopWatch();
			try {
				stopWatch.start();
				final VerificationResult result = verify(verifierEvent.getInputDirectoryPath());
				stopWatch.stop();

				// Publish result event, OK or NOK.
				resultPublisherService.publish(result, stopWatch.getTotalTimeSeconds());
			} catch (final Exception e) {
				stopWatch.stop();
				LOGGER.error("Verification error. [block: {}, id: {}]", definition.getBlock(), definition.getId(), e);

				// Build and publish error result event.
				final VerificationResult errorResult = VerificationResult.error(definition, e);
				resultPublisherService.publish(errorResult, stopWatch.getTotalTimeSeconds());
			}

		}
	}

	public abstract VerificationDefinition getVerificationDefinition();

	/**
	 * Method called when executing a verification that listens to an event.
	 *
	 * @param inputDirectoryPath The event that triggered the verification.
	 * @return The result of the verification in the form of a {@link VerificationResult}.
	 */
	public abstract VerificationResult verify(final Path inputDirectoryPath);

}
