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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;

public class CheckSigEch0110Test {
    private CheckSigEch0110 checkSigEch0110;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        checkSigEch0110 = new CheckSigEch0110();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = checkSigEch0110.verify(new File(getClass().getResource("/CheckSigEch0110Test/OK").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKXmlKo() throws Exception {
        // TODO Check if test is relevant, because executeTestNOKCertKo got the same error
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of eCH-0110.xml failed");
        checkSigEch0110.verify(new File(getClass().getResource("/CheckSigEch0110Test/NOK/XML-NOT-OK").getFile()));
    }

    @Test
    public void executeTestNOKCertKo() throws Exception {
        // TODO Check if test is relevant, because executeTestNOKXmlKo got the same error
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of eCH-0110.xml failed");
        checkSigEch0110.verify(new File(getClass().getResource("/CheckSigEch0110Test/NOK/CERT-NOT-OK").getFile()));
    }

    @Test
    public void executeTestNOKFileNotFound() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage("tenant_.*\\.pem");
        checkSigEch0110.verify(new File(getClass().getResource("/CheckSigEch0110Test/NOK-NOTFILE").getFile()));
    }

    @Test
    public void executeTestNOKFileNotFound2() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage(".*eCH-0110.*\\.xml");
        checkSigEch0110.verify(new File(getClass().getResource("/CheckSigEch0110Test/NOK-NOTFILE2").getFile()));
    }

    @Test
    public void executeTestNOKFileNotFound3() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(".xml.p7");
        checkSigEch0110.verify(new File(getClass().getResource("/CheckSigEch0110Test/NOK-NOTFILE3").getFile()));
    }

}
