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
package ch.post.it.evoting.verifier.block.block2.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckNumberVoteCastCodesTest extends Block2VerificationAbstractTest {

	public CheckNumberVoteCastCodesTest() {
		super(CheckNumberVoteCastCodes.class);
	}

	@Test
	@Disabled("Enable when we got secureLog files with correct pattern.")
	void executeTestOK() throws Exception {
		VerificationResult verificationResult =
				verification.verify(Paths.get(getClass().getResource("/CheckNumberVoteCastCodesTest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOKFileNotFoundVoterInformation() {
		final FileNotFoundException ex = assertThrows(
				FileNotFoundException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckNumberVoteCastCodesTest/NOK-NOTFILE").toURI()))
		);
		assertEquals("voterInformation.*\\.csv", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFoundMappingCcHosts() {
		final FileNotFoundException ex = assertThrows(
				FileNotFoundException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckNumberVoteCastCodesTest/NOK-NOTFILE2").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.MAPPING_CC_HOSTS);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}
}
