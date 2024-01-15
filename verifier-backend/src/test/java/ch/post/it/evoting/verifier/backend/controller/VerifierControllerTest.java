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
package ch.post.it.evoting.verifier.backend.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.backend.processor.VerifierProcessor;

class VerifierControllerTest {

	private VerifierController controller;
	private VerifierProcessor processorMock;

	@BeforeEach
	void setup() {
		processorMock = mock(VerifierProcessor.class);
		controller = new VerifierController(processorMock);
	}

	@Test
	void ping_returnsTrue() {
		assertTrue(controller.ping());
	}

	@Test
	void tests_get_callsGetTestStatus() {
		controller.getTestStatus();
		verify(processorMock, atLeast(1)).getVerifications();
	}

	@Test
	void configurationInputDirectory_callsGetConfiguration() {
		controller.getDatasetConfiguration();
		verify(processorMock).getDatasetConfiguration();
	}

}
