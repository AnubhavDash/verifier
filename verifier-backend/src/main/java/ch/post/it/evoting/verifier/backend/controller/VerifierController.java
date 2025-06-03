/*
 * (c) Copyright 2025 Swiss Post Ltd.
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
package ch.post.it.evoting.verifier.backend.controller;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.verifier.backend.domain.VerifierMode;
import ch.post.it.evoting.verifier.backend.dto.Verification;
import ch.post.it.evoting.verifier.backend.processor.VerifierProcessor;

@RestController
@RequestMapping("/api/verifications")
public class VerifierController {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifierController.class);

	private final VerifierProcessor processor;

	VerifierController(final VerifierProcessor processor) {
		this.processor = processor;
	}

	@PostMapping("/reset")
	public void reset() {
		processor.resetExecution();
	}

	@GetMapping
	public ImmutableList<Verification> getVerificationList(
			@RequestParam
			final String mode
	) {
		checkNotNull(mode);
		checkArgument(mode.equals(VerifierMode.TALLY.getMode()) || mode.equals(VerifierMode.SETUP.getMode()));
		return processor.getVerifications(mode);
	}

	@PostMapping
	public ResponseEntity<String> process(
			@RequestParam
			final String runOptions) {
		try {
			processor.process(runOptions);
		} catch (final IllegalArgumentException e) {
			LOGGER.error("Unable to process the verifications", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
