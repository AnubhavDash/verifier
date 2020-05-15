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

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckCipherTextConsistencyOnlineProofsTest extends Block3VerificationAbstractTest {

    @BeforeEach
    void setup() {
        verification = new CheckCipherTextConsistencyOnlineProofs();
    }

    @Test
    void executeTestOK() throws Exception {
        VerificationResult result =
                verification.verify(Paths.get(getClass().getResource("/CheckCipherTextConsistencyOnlineProofsTest/OK").toURI()));
        assertEquals(Status.OK, result.getStatus());
    }

    @Test
    void executeTestNOK() {
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckCipherTextConsistencyOnlineProofsTest/NOK").toURI()))
        );
        assertEquals("Same vote not exist (vote non confirmé)", ex.getMessage());
    }

}
