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
package ch.post.it.evoting.verifier.backend.verifications.tally.authenticity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.signing.SignatureGeneration;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifySignatureTallyComponentVotesTest extends TallyVerificationTest {

	@BeforeEach
	void setUpAll() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
		final SignatureVerification testSignatureVerification = signatureFactory.getTestSignatureVerification();
		verification = new VerifySignatureTallyComponentVotes(resultPublisherServiceMock, electionDataExtractionService, testSignatureVerification);
	}

	@Test
	void testAbsentFileDoesNotVerify() {
		assertThrows(Exception.class, () -> verification.verify(Path.of("")));
	}

	@Test
	void testExpectedSignerSuccess() throws SignatureException {
		final TallyComponentVotesPayload tallyComponentVotesPayload = electionDataExtractionService.getTallyComponentVotesPayloads(datasetPath)
				.findFirst().orElseThrow();
		final SignatureGeneration testSignatureGeneration = signatureFactory.getTestSignatureGeneration(Alias.SDM_TALLY);
		final byte[] signature = testSignatureGeneration.genSignature(tallyComponentVotesPayload,
				ChannelSecurityContextData.tallyComponentVotes(tallyComponentVotesPayload.getElectionEventId(),
						tallyComponentVotesPayload.getBallotBoxId()));
		tallyComponentVotesPayload.setSignature(new CryptoPrimitivesSignature(signature));
		assertTrue(((VerifySignatureTallyComponentVotes) verification).verifySignature(tallyComponentVotesPayload));
	}

	@Test
	void testUnexpectedSignerFails() throws SignatureException {
		final TallyComponentVotesPayload tallyComponentVotesPayload = electionDataExtractionService.getTallyComponentVotesPayloads(datasetPath)
				.findFirst().orElseThrow();
		final SignatureGeneration testSignatureGeneration = signatureFactory.getTestSignatureGeneration(Alias.SDM_CONFIG);
		final byte[] wrongSignature = testSignatureGeneration.genSignature(tallyComponentVotesPayload,
				ChannelSecurityContextData.tallyComponentVotes(tallyComponentVotesPayload.getElectionEventId(),
						tallyComponentVotesPayload.getBallotBoxId()));
		tallyComponentVotesPayload.setSignature(new CryptoPrimitivesSignature(wrongSignature));
		assertFalse(((VerifySignatureTallyComponentVotes) verification).verifySignature(tallyComponentVotesPayload));
	}
}