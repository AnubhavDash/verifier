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
package ch.post.it.evoting.verifier.backend.verifications.tally.completeness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.UncheckedIOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifyTallyCompletenessTest extends TallyVerificationTest {

	@BeforeAll
	static void setupAll() {
		verification = new VerifyTallyCompleteness(pathService, resultPublisherServiceMock);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("invalid setup files fails")
	void invalidSetupFiles() {
		final PathService spyPathService = spy(pathService);
		doThrow(UncheckedIOException.class).when(spyPathService).buildFromRootPath(eq(StructureKey.ELECTION_EVENT_CONTEXT), any());

		final VerifyTallyCompleteness verificationWithSpy = new VerifyTallyCompleteness(spyPathService, resultPublisherServiceMock);
		final VerificationResult result = verificationWithSpy.verify(datasetPath);
		final VerificationResult expectedResult = VerificationResult.failure(verificationWithSpy.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "verification100.nok.message"));
		assertEquals(expectedResult, result);
	}

	@Test
	@DisplayName("invalid tally files fails")
	void invalidTallyFiles() {
		final PathService spyPathService = spy(pathService);
		doThrow(UncheckedIOException.class).when(spyPathService).buildFromDynamicAncestorPath(eq(StructureKey.TALLY_COMPONENT_VOTES), any());

		final VerifyTallyCompleteness verificationWithSpy = new VerifyTallyCompleteness(spyPathService, resultPublisherServiceMock);
		final VerificationResult result = verificationWithSpy.verify(datasetPath);
		final VerificationResult expectedResult = VerificationResult.failure(verificationWithSpy.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "verification100.nok.message"));
		assertEquals(expectedResult, result);
	}
}
