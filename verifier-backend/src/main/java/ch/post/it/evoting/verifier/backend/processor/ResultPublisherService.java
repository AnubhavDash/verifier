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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.dto.Verification;
import ch.post.it.evoting.verifier.backend.mapper.VerificationMapper;

@Service
public class ResultPublisherService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResultPublisherService.class);

	private final SimpMessagingTemplate template;

	ResultPublisherService(final SimpMessagingTemplate template) {
		this.template = template;
	}

	public void publish(final VerificationResult verificationResult, final double executionTime) {
		final Verification verification = VerificationMapper.INSTANCE.map(verificationResult.getVerificationDefinition());
		VerificationMapper.INSTANCE.update(verification, verificationResult);

		template.convertAndSend("/pushUpdate", verification);
		LOGGER.info("Verification result published to websocket. [verification: {}, status: {}, executionTime(s): {}]", verification.getId(),
				verification.getStatus(), executionTime);
	}

}
