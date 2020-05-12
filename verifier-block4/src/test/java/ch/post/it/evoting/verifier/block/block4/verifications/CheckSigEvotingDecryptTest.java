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
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class CheckSigEvotingDecryptTest extends Block4VerificationAbstractTest {

    @BeforeEach
    void setup() {
        verification = new CheckSigEvotingDecrypt();
    }

    @Test
    void executeTestOK() throws Exception {
        VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/CheckSigEvotingDecryptTest/OK").toURI()));
        assertNotNull(verificationResult);
        assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    void executeTestNOKXmlKo() {
        // TODO Check if test is relevant, because executeTestNOKCertKo got the same error
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigEvotingDecryptTest/NOK/XML-NOT-OK").toURI()))
        );
        assertEquals("The signature verification of the evoting-decrypt.xml failed", ex.getMessage());
    }

    @Test
    void executeTestNOKCertKo() {
        // TODO Check if test is relevant, because executeTestNOKXmlKo got the same error
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigEvotingDecryptTest/NOK/CERT-NOT-OK").toURI()))
        );
        assertEquals("The signature verification of the evoting-decrypt.xml failed", ex.getMessage());
    }

    @Test
    void executeTestNOKFileNotFoundRootCertificate() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigEvotingDecryptTest/NOK-NOTFILE").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.TENANT_100);
        assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
    }

    @Test
    void executeTestNOKFileNotFoundEVotingDecryptResult() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigEvotingDecryptTest/NOK-NOTFILE2").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.EVOTING_DECRYPT_RESULT);
        assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
    }

    @Test
    void executeTestNOKFileNotFoundEVotingDecryptResultP7() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigEvotingDecryptTest/NOK-NOTFILE3").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.EVOTING_DECRYPT_RESULT);
        assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier() + RelationType.P7.toFileExtension()).matches(ex.getMessage()));
    }

}
