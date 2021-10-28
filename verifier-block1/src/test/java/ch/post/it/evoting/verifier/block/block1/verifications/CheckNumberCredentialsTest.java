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
package ch.post.it.evoting.verifier.block.block1.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckNumberCredentialsTest extends Block1VerificationAbstractTest {

	public CheckNumberCredentialsTest() {
		super(CheckNumberCredentials.class);
	}

	@Test
	void executeTestOK() throws Exception {
		VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckNumberCredentialsTest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOK() {
		final VerificationFailureException ex = assertThrows(VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckNumberCredentialsTest/NOK/NOK").toURI())));
		assertEquals("The number of credentials and the number of expected voters do not match", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFound() {
		final IOException ex = assertThrows(IOException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckNumberCredentialsTest/NOK/NOK-NOFILE").toURI())));
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.CONFIG_ANONYMIZED);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFound2() {
		final IOException ex = assertThrows(IOException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckNumberCredentialsTest/NOK/NOK-NOFILE2").toURI())));
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.CREDENTIAL_DATA);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}
}
