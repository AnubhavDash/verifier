/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block3.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;

class CheckIndependenceGeneratorsOnlineTest extends Block3VerificationAbstractTest {

	public CheckIndependenceGeneratorsOnlineTest() {
		super(CheckIndependenceGeneratorsOnline.class);
	}

	@Test
	@Disabled("FIXME")
	void executeTestOK() throws Exception {
		VerificationResult result =
				verification.verify(Paths.get(getClass().getResource("/CheckIndependenceGeneratorsOnlineTest/OK").toURI()));
		assertEquals(Status.OK, result.getStatus());
	}

	@Test
	@Disabled("Fix this test by not using status NOK anymore and implementing meaningful asserts.")
	void executeTestNOK() throws Exception {
		VerificationResult result =
				verification.verify(Paths.get(getClass().getResource("/CheckIndependenceGeneratorsOnlineTest/NOK").toURI()));
		assertEquals(Status.NOK, result.getStatus());
	}

}
