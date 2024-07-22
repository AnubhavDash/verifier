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
package ch.post.it.evoting.verifier.backend.verifications.tally.authenticity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.ech.xmlns.ech_0222._1.Delivery;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifySignatureTallyComponentEch0222Test extends TallyVerificationTest {

	@BeforeEach
	void setUpAll() {
		verification = new VerifySignatureTallyComponentEch0222(resultPublisherServiceMock, electionDataExtractionService,
				datasetSignatureVerification, objectMapper);
	}

	@Test
	void testOK() {
		final Delivery delivery = electionDataExtractionService.getTallyComponentEch0222(datasetPath);

		assertTrue(((VerifySignatureTallyComponentEch0222) verification).verifySignature(delivery));
	}

	@Test
	void testNOK() {
		final Delivery delivery = electionDataExtractionService.getTallyComponentEch0222(datasetPath);

		delivery.getDeliveryHeader().setSenderId("");

		assertFalse(((VerifySignatureTallyComponentEch0222) verification).verifySignature(delivery));
	}
}