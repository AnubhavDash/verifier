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
package ch.post.it.evoting.verifier.common;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;

/**
 * Class to be extended by all verifications that want to be discovered and run by the Verifier.
 */
@Component
public abstract class AbstractVerification {

	private final ApplicationEventPublisher applicationEventPublisher;

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVerification.class);

	protected AbstractVerification(final ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	/**
	 * Listens asynchronously to all {@link VerifierEvent} and executes the {@link AbstractVerification#verify(VerifierEvent)} method depending on the
	 * sub-class' {@link VerificationTrait}s.
	 *
	 * @param verifierEvent The event to which the method is listening to.
	 */
	@Async
	@EventListener({ VerifierEvent.class })
	public void executeVerify(final VerifierEvent verifierEvent) {
		// Retrieve events to which this verification is listening to.
		final var definition = getVerificationDefinition();
		final List<Class<? extends VerifierEvent>> events = definition.getVerificationTraits().stream()
				.map(VerificationTrait::getEventClass)
				.collect(Collectors.toList());

		// Execute verification only if received event is one listened to.
		if (events.contains(verifierEvent.getClass())) {
			VerificationResultEvent resultEvent;
			try {
				resultEvent = verify(verifierEvent);

				// Publish result event, OK or NOK.
				applicationEventPublisher.publishEvent(resultEvent);
			} catch (Exception e) {
				LOGGER.error("Verification {}-{} failed with exception: ", definition.getBlockId(), definition.getId(), e);

				// Build and publish error result event.
				final var errorResultEvent = VerificationResultEvent.error(this, definition);
				applicationEventPublisher.publishEvent(errorResultEvent);
			}

		}
	}

	public abstract VerificationDefinition getVerificationDefinition();

	/**
	 * Method called when executing a verification that listens to an event.
	 *
	 * @param verifierEvent The event that triggered the verification.
	 * @return The result of the verification in the form of a {@link VerificationResultEvent}.
	 */
	public abstract VerificationResultEvent verify(final VerifierEvent verifierEvent);

}
