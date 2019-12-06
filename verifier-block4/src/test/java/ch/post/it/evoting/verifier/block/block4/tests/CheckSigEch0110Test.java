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
package ch.post.it.evoting.verifier.block.block4.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class CheckSigEch0110Test {

    @Test
    public void executeTestOK() {
        VerificationResult verificationResult = new CheckSigEch0110().executeVerification(new File(getClass().getResource("/CheckSigEch0110Test/OK").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKXmlKo() {
        VerificationResult verificationResult = new CheckSigEch0110().executeVerification(new File(getClass().getResource("/CheckSigEch0110Test/NOK/XML-NOT-OK").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.NOK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKCertKo() {
        VerificationResult verificationResult = new CheckSigEch0110().executeVerification(new File(getClass().getResource("/CheckSigEch0110Test/NOK/CERT-NOT-OK").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.NOK, verificationResult.getStatus());
    }
    @Test
    public void executeTestNOKFileNotFound() {
        VerificationResult verificationResult = new CheckSigEch0110().executeVerification(new File(getClass().getResource("/CheckSigEch0110Test/NOK-NOTFILE").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.NOK, verificationResult.getStatus());
    }

}
