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
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckSigBulletinParBulletinTest extends Block4VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckSigBulletinParBulletin();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckSigBulletinParBulletinTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKPdfKo() throws Exception {
        // TODO Check if test is relevant, because executeTestNOKCertKo got the same error
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the bulletin par bulletin report failed");
        verification.verify(Paths.get(getClass().getResource("/CheckSigBulletinParBulletinTest/NOK/PDF-NOT-OK").toURI()));
    }

    @Test
    public void executeTestNOKCertKo() throws Exception {
        // TODO Check if test is relevant, because executeTestNOKPdfKo got the same error
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the bulletin par bulletin report failed");
        verification.verify(Paths.get(getClass().getResource("/CheckSigBulletinParBulletinTest/NOK/CERT-NOT-OK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundCertificate() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(matchesRegex(verification.getPathService().getStructureNode(StructureKey.TENANT_100).getQualifier()));
        verification.verify(Paths.get(getClass().getResource("/CheckSigBulletinParBulletinTest/NOK-NOTFILE").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundBallotPDF() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(matchesRegex(verification.getPathService().getStructureNode(StructureKey.BALLOT_RESULT).getQualifier()));
        verification.verify(Paths.get(getClass().getResource("/CheckSigBulletinParBulletinTest/NOK-NOTFILE2").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundBallotMetadata() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(matchesRegex(
                verification.getPathService().getStructureNode(StructureKey.BALLOT_RESULT).getQualifier()
                        + RelationType.P7.toFileExtension()
        ));
        verification.verify(Paths.get(getClass().getResource("/CheckSigBulletinParBulletinTest/NOK-NOTFILE3").toURI()));
    }

}
