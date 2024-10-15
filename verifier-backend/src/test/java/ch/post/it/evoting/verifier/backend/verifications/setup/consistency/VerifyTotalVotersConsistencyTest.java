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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.base.Throwables;

import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.HeaderType;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifyTotalVotersConsistencyTest extends SetupVerificationTest {

	@BeforeAll
	static void setupAll() {
		verification = new VerifyTotalVotersConsistency(resultPublisherServiceMock, electionDataExtractionService);
	}

	@Test
	void verifyOk() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokBadVoterTotal() {
		final Configuration configuration = electionDataExtractionService.getCantonConfig(datasetPath);
		final Configuration configurationMock = spy(configuration);
		final HeaderType headerType = new HeaderType();
		headerType.setVoterTotal(39);
		when(configurationMock.getHeader()).thenReturn(headerType);
		final ElectionDataExtractionService extractionServiceMock = spy(electionDataExtractionService);
		doReturn(configurationMock).when(extractionServiceMock).getCantonConfig(datasetPath);

		final VerifyTotalVotersConsistency verificationBadVoterTotal = new VerifyTotalVotersConsistency(resultPublisherServiceMock,
				extractionServiceMock);

		final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> verificationBadVoterTotal.verify(datasetPath));
		assertEquals("The voter total in the header must be the same as the size of the voter list. [voterTotal: 39, voterCount: 43]",
				Throwables.getRootCause(exception).getMessage());
	}

	@Test
	void verifyNok() {
		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(datasetPath);
		final ElectionEventContextPayload electionEventContextPayloadMock = spy(electionEventContextPayload);
		final ElectionEventContext electionEventContext = electionEventContextPayload.getElectionEventContext();
		final ElectionEventContext electionEventContextMock = spy(electionEventContext);
		doReturn(List.of()).when(electionEventContextMock).verificationCardSetContexts();
		doReturn(electionEventContextMock).when(electionEventContextPayloadMock).getElectionEventContext();

		final ElectionDataExtractionService extractionServiceMock = spy(electionDataExtractionService);
		doReturn(electionEventContextPayloadMock).when(extractionServiceMock).getElectionEventContextPayload(datasetPath);

		final VerifyTotalVotersConsistency verificationNok = new VerifyTotalVotersConsistency(resultPublisherServiceMock,
				extractionServiceMock);
		final VerificationResult verificationResult = verificationNok.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationResult.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification313.nok.message"));

		assertEquals(expectedResult, verificationResult);
	}
}
