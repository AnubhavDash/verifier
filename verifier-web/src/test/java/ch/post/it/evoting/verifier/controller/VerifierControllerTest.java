/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
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
