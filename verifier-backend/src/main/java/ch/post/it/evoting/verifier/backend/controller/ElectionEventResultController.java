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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.tally.BallotBoxResult;
import ch.post.it.evoting.verifier.backend.processor.VerifierProcessor;

@RestController
@RequestMapping("/api/electioneventresult")
public class ElectionEventResultController {

	private final VerifierProcessor processor;

	ElectionEventResultController(final VerifierProcessor processor) {
		this.processor = processor;
	}

	@GetMapping
	public ImmutableList<BallotBoxResult> getElectionEventResult() {
		return processor.getElectionEventResult();
	}
}
