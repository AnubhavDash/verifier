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
package ch.post.it.evoting.verifier.block.block4.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.helper.RegexHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class CheckTallyingListsTest extends Block4VerificationAbstractTest {

    @BeforeEach
    void setup() {
        verification = new CheckTallyingLists();
    }

    @Test
    void executeTestOK() throws Exception {
        VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/OK").toURI()));
        assertNotNull(verificationResult);
        assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    void executeTestNOK() {
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK").toURI()))
        );
        assertEquals("The occurrences for list are different for counting Circle in eCH-0110 and evoting-decrypt", ex.getMessage());
    }

    @Test
    void executeTestNOKVoteCountCountingCircle() {
        final IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-VOTECOUNT-COUNTINGCIRCLE").toURI()))
        );
        assertTrue(ex.getMessage().contains("cannot find the decrypt data for given countingCircle"));
    }

    @Test
    void executeTestNOKVoteCountElection() {
        final IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-VOTECOUNT-ELECTION").toURI()))
        );
        assertEquals("cannot find the decrypt data for given election : WRONG-IDENTIFIER", ex.getMessage());
    }

    @Test
    void executeTestNOKFileNotFoundConfiguration() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-CONFIG").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.CONFIG_ANONYMIZED);
        assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
    }

    @Test
    void executeTestNOKFileNotFoundEVoting() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-EVOTING").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.EVOTING_DECRYPT_RESULT);
        assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
    }

    @Test
    void executeTestNOKFileNotFoundECH0110() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-eCH").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ECH0110);
        assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
    }
}
