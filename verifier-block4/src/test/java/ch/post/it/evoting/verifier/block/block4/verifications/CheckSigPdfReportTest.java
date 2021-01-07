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
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckSigPdfReportTest extends Block4VerificationAbstractTest {

    @BeforeEach
    void setup() {
        verification = new CheckSigPdfReport();
    }

    @Test
    @Disabled("report.pdf and report.pdf.p7 are missing in the new dataset")
    void executeTestOK() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckSigPdfReportTest/OK").toURI()));
        assertNotNull(verificationResult);
        assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    void executeTestNOKPdfKo() {
        // TODO Check if test is relevant, because executeTestNOKCertKo got the same error
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigPdfReportTest/NOK/PDF-NOT-OK").toURI()))
        );
        assertEquals("The signature verification of PDF Report failed", ex.getMessage());
    }

    @Test
    void executeTestNOKCertKo() {
        // TODO Check if test is relevant, because executeTestNOKPdfKo got the same error
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigPdfReportTest/NOK/CERT-NOT-OK").toURI()))
        );
        assertEquals("The signature verification of PDF Report failed", ex.getMessage());
    }

    @Test
    void executeTestNOKFileNotFoundRootCertificate() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigPdfReportTest/NOK-NOTFILE").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.TENANT_100);
        assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
    }

    @Test
    void executeTestNOKFileNotFoundReportResult() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigPdfReportTest/NOK-NOTFILE2").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.REPORT_RESULT);
        assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
    }

    @Test
    void executeTestNOKFileNotFoundReportResultP7() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigPdfReportTest/NOK-NOTFILE3").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.REPORT_RESULT);
        assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier() + RelationType.P7.toFileExtension()).matches(ex.getMessage()));
    }
}
