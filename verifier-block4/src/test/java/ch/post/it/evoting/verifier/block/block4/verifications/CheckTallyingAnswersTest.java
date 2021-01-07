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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.test.helper.RegexHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckTallyingAnswersTest extends Block4VerificationAbstractTest {

    @BeforeEach
    void setup() {
        verification = new CheckTallyingAnswers();
    }

    @Test
    void executeTestOK() throws Exception {
        // TODO The eCH0110 must contains vote results to be valid
        VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/CheckTallyingAnswersTest/OK").toURI()));
        assertNotNull(verificationResult);
        assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    @Disabled("FIXME")
    void executeTestNOK() {
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingAnswersTest/NOK").toURI()))
        );
        assertEquals("Business Failure", ex.getMessage());
    }

    // TODO The following NOK tests must be implemented:
    //  - Number of YES votes verification failed in standard ballot
    //  - Number of NO votes verification failed in standard ballot
    //  - Number of EMPTY votes verification failed in standard ballot
    //  - Number of YES votes verification failed in variant ballot
    //  - Number of NO votes verification failed in variant ballot
    //  - Number of EMPTY votes verification failed in variant ballot

    @Test
    void executeTestNOKFileNotFoundConfiguration() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingAnswersTest/NOK-NOFILE-CONFIG").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.CONFIG_ANONYMIZED);
        assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
    }

    @Test
    void executeTestNOKFileNotFoundEVotingDecryptResult() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingAnswersTest/NOK-NOFILE-EVOTING").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.EVOTING_DECRYPT_RESULT);
        assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
    }

    @Test
    void executeTestNOKFileNotFoundECH0110() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckTallyingAnswersTest/NOK-NOFILE-eCH").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ECH0110);
        assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
    }
}
