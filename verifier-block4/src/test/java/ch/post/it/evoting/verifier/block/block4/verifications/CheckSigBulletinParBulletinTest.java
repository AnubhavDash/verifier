/**
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
import ch.post.it.evoting.verifier.common.block.VerificationFailureWrappedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;

public class CheckSigBulletinParBulletinTest {
    private CheckSigBulletinParBulletin checkSigBulletinParBulletin;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        checkSigBulletinParBulletin = new CheckSigBulletinParBulletin();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = checkSigBulletinParBulletin.verify(new File(getClass().getResource("/CheckSigBulletinParBulletinTest/OK").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKPdfKo() throws Exception {
        // TODO Check if test is relevant, because executeTestNOKCertKo got the same error
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the bulletin par bulletin report failed");
        checkSigBulletinParBulletin.verify(new File(getClass().getResource("/CheckSigBulletinParBulletinTest/NOK/PDF-NOT-OK").getFile()));
    }

    @Test
    public void executeTestNOKCertKo() throws Exception {
        // TODO Check if test is relevant, because executeTestNOKPdfKo got the same error
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the bulletin par bulletin report failed");
        checkSigBulletinParBulletin.verify(new File(getClass().getResource("/CheckSigBulletinParBulletinTest/NOK/CERT-NOT-OK").getFile()));
    }

    @Test
    public void executeTestNOKFileNotFound() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage(".*ballot.*\\.pdf");
        checkSigBulletinParBulletin.verify(new File(getClass().getResource("/CheckSigBulletinParBulletinTest/NOK-NOTFILE").getFile()));
    }

    @Test
    public void executeTestNOKFileNotFound2() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage("ballot.pdf.p7");
        checkSigBulletinParBulletin.verify(new File(getClass().getResource("/CheckSigBulletinParBulletinTest/NOK-NOTFILE2").getFile()));
    }

}
