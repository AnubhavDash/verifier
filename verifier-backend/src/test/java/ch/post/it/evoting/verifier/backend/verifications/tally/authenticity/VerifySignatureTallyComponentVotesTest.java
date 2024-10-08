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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.security.SignatureException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifySignatureTallyComponentVotesTest extends TallyVerificationTest {

	@BeforeEach
	void setUpAll() {
		verification = new VerifySignatureTallyComponentVotes(resultPublisherServiceMock, electionDataExtractionService, datasetSignatureVerification);
	}

	@Test
	void testAbsentFileDoesNotVerify() {
		assertThrows(Exception.class, () -> verification.verify(Path.of("")));
	}

	@Test
	void testOK() {
		final TallyComponentVotesPayload tallyComponentVotesPayload = electionDataExtractionService.getTallyComponentVotesPayloads(datasetPath)
				.findFirst()
				.orElseThrow();

		assertTrue(((VerifySignatureTallyComponentVotes) verification).verifySignature(tallyComponentVotesPayload));
	}

	@Test
	void testNOK() throws SignatureException {
		final TallyComponentVotesPayload tallyComponentVotesPayload = electionDataExtractionService.getTallyComponentVotesPayloads(datasetPath)
				.findFirst()
				.orElseThrow();

		final CryptoPrimitivesSignature dummySignature = datasetSignatureFactory.getDummySignature(tallyComponentVotesPayload,
				ChannelSecurityContextData.tallyComponentVotes(tallyComponentVotesPayload.getElectionEventId(),
						tallyComponentVotesPayload.getBallotBoxId()),
				Alias.SDM_TALLY);
		tallyComponentVotesPayload.setSignature(dummySignature);

		assertFalse(((VerifySignatureTallyComponentVotes) verification).verifySignature(tallyComponentVotesPayload));
	}
}