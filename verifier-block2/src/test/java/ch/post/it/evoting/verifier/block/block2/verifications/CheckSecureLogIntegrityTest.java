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
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.HmacGenerator;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckSecureLogIntegrityTest extends Block2VerificationAbstractTest {

	public CheckSecureLogIntegrityTest() {
		super(CheckSecureLogIntegrity.class);
	}

	@Test
	@Disabled("Enable when we got secureLog files with correct pattern?")
	void executeTestOK() throws Exception {
		VerificationResult verificationResult =
				verification.verify(Paths.get(getClass().getResource("/CheckSecureLogIntegrityTest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	@Disabled("Enable when we got secureLog files with correct pattern?")
	void executeTestNOK() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSecureLogIntegrityTest/NOK").toURI()))
		);
		assertEquals("Check secure log integrity failed", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFoundSecureLogDir() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSecureLogIntegrityTest/NOK-NOTFILE").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.SECURE_LOG_DIR);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void generateHmacCheckpoint() {
		final String endLsk = "U9LwEJ1oDitWij/tX8SJ44FqDGrFFhJXXr+Nakj509w=";
		final String hmac = "cxEQsXjOFU09oL8gGvDMSC0TzYDwtGrJdfL27iOzNnQ=";
		final String phmac = "qwQjgCt3g7+6MPQen2pB4weT1e/FgBvl2iRn/LOJ3IU=";
		final String lsk = "Jd10KtoV11/a3XgJwIAU71K0PCjDBTDEa+/M7GtYcWg=";
		final String esk = "XFikMrFPxOG+NleCjE7MYZ4XrWgWELyjx8VJJnd/ErnTh/hUk++E0NIFG/PAzYj5HmauQNZbkxXxSvoGZ1DM5U/eCI0paNPzIn5S6W7IgUTT6" +
				"/ll7vk2j9ZsU1qGrhRMcOZd3Uo" +
				"+b78OMP58iX7fyofhfJpvhs5M2PbAPfculfLoNQq9OynVIrIDplGX8rF3FOeZ9kXJUnMQK9qrKhHEPer5PXqOb47kqdnVHfDu7cDbDzZc34YDFd7pCWbVhC8GPhfZ/CNUot9A1Wghu1ECg6VHvRSK9q31dk4dNziEBp7yz8M/B6E8HvYcRidHYxOq4Qe9tlB61KwLwLClInX0tiI25dXOV53ux2TS387la2O5MlfFpGV2je3d98UI6E5mSXNO7tlxfRoRDbNebai+HMZr2fuuZ4bztVNkRKhrJmA5CXNwESrv+Q==";
		final String raw = "2018-10-25 14:25:17,956|DEBUG|TIMER-LOG|New Secret Key generated.\n";
		final String ls = "10000";
		final String tl = "300000";
		final String ts = "1540470317956";

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (DataOutputStream stream = new DataOutputStream(bytes)) {
			stream.write(Base64.decode(phmac));
			stream.write(Base64.decode(lsk));
			stream.write(Base64.decode(esk));
			if (StringUtils.isNotEmpty(ls)) {
				stream.writeInt(Integer.parseInt(ls));
			}
			if (StringUtils.isNotEmpty(tl)) {
				stream.writeLong(Long.parseLong(tl));
			}
			stream.writeLong(Long.parseLong(ts));
			stream.write(raw.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			fail(e.getMessage());
		}

		String calculatedHmac = Base64.toBase64String(HmacGenerator.hash(bytes.toByteArray(), Base64.decode(endLsk)));

		assertEquals(hmac, calculatedHmac);
	}

	@Test
	void generateHmacRegularLog() {
		final String endLsk = "MD+KuCAgFCcbqETTqDeI79Fr9P3TMq2lpGfuahpZGp8=";
		final String hmac = "gtGMUiyEZlA/MeVMJAMbSmUj3DYjOkxWm7N+4i9kiZc=";
		final String phmac = "K2d+ArwhI/x6lSzFqpSc4f3AyxSLDK3109J/oMrEh7Y=";
		final String lsk = "";
		final String esk = "";
		final String raw = "2018-10-25 14:29:44,113|DEBUG|pool-1-thread-57|Log File Name: /data/logs/cg/" +
				"./logs/cg_secure-20181025-142944-25.log\n";
		final String ls = "";
		final String tl = "";
		final String ts = "1540470584113";

		//2018-10-25 14:29:44,113|DEBUG|pool-1-thread-57|Log File Name: /data/logs/cg/./logs/cg_secure-20181025-142944-25.log
		// {*TS::1540470584113,HMAC::gtGMUiyEZlA/MeVMJAMbSmUj3DYjOkxWm7N+4i9kiZc=*}"}}

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (DataOutputStream stream = new DataOutputStream(bytes)) {
			stream.write(Base64.decode(phmac));
			stream.write(Base64.decode(lsk));
			stream.write(Base64.decode(esk));
			if (StringUtils.isNotEmpty(ls)) {
				stream.writeInt(Integer.parseInt(ls));
			}
			if (StringUtils.isNotEmpty(tl)) {
				stream.writeLong(Long.parseLong(tl));
			}
			stream.writeLong(Long.parseLong(ts));
			stream.write(raw.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			fail(e.getMessage());
		}

		String calculatedHmac = Base64.toBase64String(HmacGenerator.hash(bytes.toByteArray(), Base64.decode(endLsk)));

		assertEquals(hmac, calculatedHmac);
	}
}
