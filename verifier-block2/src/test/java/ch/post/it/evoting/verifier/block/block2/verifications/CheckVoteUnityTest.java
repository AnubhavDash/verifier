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
package ch.post.it.evoting.verifier.block.block2.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class CheckVoteUnityTest {

    @Test
    @Ignore
    public void executeTest() {
        VerificationResult verificationResult = new CheckVoteUnity().executeVerification(new File(getClass().getResource("/CheckVoteUnityTest/OK").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    @Ignore
    public void executeTestNOK() {
        VerificationResult verificationResult = new CheckVoteUnity().executeVerification(new File(getClass().getResource("/CheckVoteUnityTest/NOK").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.NOK, verificationResult.getStatus());
    }

    @Test
    @Ignore
    public void executeTestNOKnotFile() {
        VerificationResult verificationResult = new CheckVoteUnity().executeVerification(new File(getClass().getResource("/CheckVoteUnityTest/NOK-NOTFILE").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.NOK, verificationResult.getStatus());
    }
}
