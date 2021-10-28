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
package ch.post.it.evoting.verifier.block.block4.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.test.helper.RegexHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckTallyingCandidatesTest extends Block4VerificationAbstractTest {

	public CheckTallyingCandidatesTest() {
		super(CheckTallyingCandidates.class);
	}

	@Test
	void executeTestOK() throws Exception {
		VerificationResult verificationResult =
				verification.verify(Paths.get(getClass().getResource("/CheckTallyingCandidates/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOKMajoralCountOfVotesTotal() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingCandidates/NOK-COUNTOFVOTES-MAJORAL").toURI()))
		);
		assertEquals("The count of votes total for the candidate does not match in majoral election", ex.getMessage());
	}

	@Test
	void executeTestNOKProportionalCountOfVotesTotal() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingCandidates/NOK-COUNTOFVOTES-PROPORTIONAL").toURI()))
		);
		assertEquals("The count of votes total for the candidate does not match in proportional election", ex.getMessage());
	}

	@Test
	void executeTestOKWriteIns() throws Exception {
		VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckTallyingCandidates/OK" +
				"-WRITEINS").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOKWriteInsContainsKeyMajoral() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingCandidates/NOK-WRITEINS-CONTAINSKEY-MAJORAL").toURI()))
		);
		assertEquals("The count for the candidate does not match in writeIns-containsKey majoral election", ex.getMessage());
	}

	@Test
	void executeTestNOKWriteInsContainsKeyProportional() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingCandidates/NOK-WRITEINS-CONTAINSKEY" +
						"-PROPORTIONAL").toURI()))
		);
		assertEquals("The count for the candidate does not match in writeIns-containsKey proportional election", ex.getMessage());
	}

	@Test
	void executeTestNOKWriteInsEqualsMajoral() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingCandidates/NOK-WRITEINS-EQUALS-MAJORAL").toURI()))
		);
		assertEquals("The count for the candidate does not match in writeIns-equals majoral election", ex.getMessage());
	}

	@Test
	void executeTestNOKWriteInsEqualsProportional() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingCandidates/NOK-WRITEINS-EQUALS-PROPORTIONAL").toURI()))
		);
		assertEquals("The count for the candidate does not match in writeIns-equals proportional election", ex.getMessage());
	}

	@Test
	void executeTestNOKDecryptCount() {
		final IllegalArgumentException ex = assertThrows(
				IllegalArgumentException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingCandidates/NOK-DECRYPTCOUNT").toURI()))
		);
		assertTrue(ex.getMessage().contains("cannot find the decrypt data for given countingCircle"));
	}

	@Test
	void executeTestNOKFileNotFoundConfiguration() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingCandidates/NOK-NOFILE-CONFIG").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.CONFIG_ANONYMIZED);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
	}

	@Test
	void executeTestNOKFileNotFoundEvoting() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingCandidates/NOK-NOFILE-EVOTING").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.EVOTING_DECRYPT_RESULT);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
	}

	@Test
	void executeTestNOKFileNotFoundECH0110() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingCandidates/NOK-NOFILE-eCH").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ECH0110);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
	}
}
