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
package ch.post.it.evoting.verifier.block.block3.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

public class CheckSigProofsTest extends Block3VerificationAbstractTest {

    @Before
    public void setup() {
        verification = new CheckSigProofs();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckSigProofsTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKJsonKo() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckSigProofsTest/NOK/JSON-NOT-OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.NOK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKCertKo() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckSigProofsTest/NOK/CERT-NOT-OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.NOK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKFileNotFound() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckSigProofsTest/NOK-NOTFILE").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.NOK, verificationResult.getStatus());
    }
}
