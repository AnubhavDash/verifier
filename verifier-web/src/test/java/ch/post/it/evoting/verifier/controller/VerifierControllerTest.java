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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.dto.Configuration;
import ch.post.it.evoting.verifier.processor.AlreadyStartedException;
import ch.post.it.evoting.verifier.processor.VerifierProcessor;

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
	void tests_process_callsProcessWithTraits() throws AlreadyStartedException {
		controller.process(VerificationTrait.PRE_DECRYPTION.toString());
		Set<VerificationTrait> arguments = new HashSet<>();
		arguments.add(VerificationTrait.PRE_DECRYPTION);
		verify(processorMock).processVerifications(arguments);
	}

	@Test
	void tests_process_callsProcessWithoutTraits() throws AlreadyStartedException {
		controller.process(null);
		verify(processorMock).processVerifications(ArgumentMatchers.isNull());
	}

	@Test
	void pdf_usesGerman() {
		controller.generatePdf(Locale.GERMAN);
		verify(processorMock).generatePdf(Language.DE);
	}

	@Test
	void pdf_usesFrench() {
		controller.generatePdf(Locale.FRENCH);
		verify(processorMock).generatePdf(Language.FR);
	}

	@Test
	void tests_get_callsGetTestStatus() {
		controller.getTestStatus();
		verify(processorMock, atLeast(1)).getVerificationStatus();
	}

	@Test
	void configurationInputDirectory_callsSetConfiguration() {
		Configuration config = new Configuration();
		controller.setConfigurationInputDirectory(config);
		verify(processorMock).setConfiguration(config);
	}

	@Test
	void configurationInputDirectory_callsGetConfiguration() {
		controller.getConfiguration();
		verify(processorMock).getConfiguration();
	}

}
