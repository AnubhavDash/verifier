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
package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class CheckPrimeNumberOptionsVOTest extends Block1VerificationAbstractTest {

    @BeforeEach
    void setup() {
        verification = new CheckPrimeNumberOptionsVO();
    }

    @Test
    void executeTestOK() throws Exception {
        VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/CheckPrimeNumberOptionsVOTest/OK").toURI()));
        assertNotNull(verificationResult);
        assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    void executeTestNOK() {
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckPrimeNumberOptionsVOTest/NOK/NOK").toURI()))
        );
        assertEquals("alias does not correspond to voteIdentification", ex.getMessage());
    }

    // TODO Implements the following NOK tests
    //  - the prime number fields are not mutually distinct
    //  - The number of prime numbers does not correspond to the number of answerElements
    //  - alias does not correspond to electionIdentification
    //  - The prime numbers are repeated
    //  - The number of distinct prime numbers does not correspond to the number of list elements
    //  - The number of candidate prime numbers does not correspond to the expected number of voting options for candidates

    @Test
    void executeTestNOKFileNotFound() {
        final IOException ex = assertThrows(
                IOException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckPrimeNumberOptionsVOTest/NOK/NOK-NOFILE").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.CONFIG_ANONYMIZED);
        assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
    }

    @Test
    void executeTestNOKFileNotFound2() {
        final IOException ex = assertThrows(
                IOException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckPrimeNumberOptionsVOTest/NOK/NOK-NOFILE2").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.DATA_CONFIG_UPDATED);
        assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
    }
}
