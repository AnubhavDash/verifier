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

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.security.SignatureException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifySignatureControlComponentBallotBoxTest extends TallyVerificationTest {

	@BeforeEach
	void setUpAll() {
		verification = new VerifySignatureControlComponentBallotBox(resultPublisherServiceMock, electionDataExtractionService,
				datasetSignatureVerification);
	}

	@Test
	void testAbsentFileDoesNotVerify() {
		assertThrows(Exception.class, () -> verification.verify(Path.of("")));
	}

	@Test
	void testOK() {
		final ControlComponentBallotBoxPayload controlComponentBallotBoxPayload = electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
				datasetPath)
				.collect(toImmutableList())
				.get(0);

		assertTrue(((VerifySignatureControlComponentBallotBox) verification).verifySignature(controlComponentBallotBoxPayload));
	}

	@Test
	void testNOK() throws SignatureException {
		final ControlComponentBallotBoxPayload controlComponentBallotBoxPayload = electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
				datasetPath)
				.collect(toImmutableList())
				.get(0);

		final int nodeId = controlComponentBallotBoxPayload.getNodeId();
		final CryptoPrimitivesSignature dummySignature = datasetSignatureFactory.getDummySignature(controlComponentBallotBoxPayload,
				ChannelSecurityContextData.controlComponentBallotBox(nodeId, controlComponentBallotBoxPayload.getElectionEventId(),
						controlComponentBallotBoxPayload.getBallotBoxId()),
				Alias.getControlComponentByNodeId(nodeId));
		controlComponentBallotBoxPayload.setSignature(dummySignature);

		assertFalse(((VerifySignatureControlComponentBallotBox) verification).verifySignature(controlComponentBallotBoxPayload));
	}
}
