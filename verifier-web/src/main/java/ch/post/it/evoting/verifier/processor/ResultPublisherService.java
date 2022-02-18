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
package ch.post.it.evoting.verifier.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.verifier.plugin.contract.Status;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.mapper.VerificationMapper;

@Service
public class ResultPublisherService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResultPublisherService.class);

	private final SimpMessagingTemplate template;

	ResultPublisherService(final SimpMessagingTemplate template) {
		this.template = template;
	}

	@Async
	@EventListener(VerificationResultEvent.class)
	public void publishResult(final VerificationResultEvent event) {
		final var verification = VerificationMapper.INSTANCE.map(event.getVerificationDefinition());
		VerificationMapper.INSTANCE.update(verification, event);

		if (event.getStatus() == Status.UNEXPECTED_ERROR) {
			LOGGER.error("Verification result event ERROR received [{}], publishing to websocket...", verification);
		} else {
			LOGGER.info("Verification result event received [{}], publishing to websocket...", verification);
		}

		template.convertAndSend("/pushUpdate", verification);
	}

}
