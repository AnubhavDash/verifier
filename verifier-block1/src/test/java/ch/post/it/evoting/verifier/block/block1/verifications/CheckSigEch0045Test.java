/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General  License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General  License for more details.
 *
 * You should have received a copy of the GNU General  License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block1.verifications;

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

class CheckSigEch0045Test extends Block1VerificationAbstractTest {


    @BeforeEach
    void setup() {
        verification = new CheckSigEch0045();
    }

    @Test
    void executeTestOK() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckSigEch0045Test/OK").toURI()));
        assertNotNull(verificationResult);
        assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    void executeTestNOKCertKo() {
        // TODO Check if test is relevant, because executeTestNOKXmlKo got the same error
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigEch0045Test/NOK/CERT-NOT-OK").toURI()))
        );
        assertEquals("The signature verification of the file failed", ex.getMessage());
    }

    @Test
    void executeTestNOKXmlKo() {
        // TODO Check if test is relevant, because executeTestNOKCertKo got the same error
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigEch0045Test/NOK/XML-NOT-OK").toURI()))
        );
        assertEquals("The signature verification of the file failed", ex.getMessage());
    }

    @Test
    void executeTestNOKFileNotFound() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigEch0045Test/NOK/NOK-NOFILE").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.INTEGRATION_CA);
        assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
    }

    @Test
    void executeTestNOKFileNotFound2() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigEch0045Test/NOK/NOK-NOFILE2").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ECH0045);
        assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
    }

    @Test
    void executeTestNOKFileNotFound3() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigEch0045Test/NOK/NOK-NOFILE3").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ECH0045);
        assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier() + RelationType.P7.toFileExtension()).matches(ex.getMessage()));
    }
}
