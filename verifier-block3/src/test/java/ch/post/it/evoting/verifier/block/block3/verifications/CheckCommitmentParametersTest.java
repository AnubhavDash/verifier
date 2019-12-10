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

import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class CheckCommitmentParametersTest {

    @Test
    public void executeTestOK() {
        VerificationResult verificationResult = new CheckCommitmentParameters().executeVerification(new File(getClass().getResource("/CheckCommitmentParametersCPTest/OK").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKpIsNotUnique() {
        VerificationResult verificationResult = new CheckCommitmentParameters().executeVerification(new File(getClass().getResource("/CheckCommitmentParametersCPTest/NOK/P-NOK").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.NOK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKEulerCriterionKo() {
        VerificationResult verificationResult = new CheckCommitmentParameters().executeVerification(new File(getClass().getResource("/CheckCommitmentParametersCPTest/NOK/BIGINT-NOK").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.NOK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKFileNotFound() {
        VerificationResult verificationResult = new CheckCommitmentParameters().executeVerification(new File(getClass().getResource("/CheckCommitmentParametersCPTest/NOK-NOTFILE").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.NOK, verificationResult.getStatus());
        Assert.assertEquals(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "test08.file.not.found.message"), verificationResult.getMessage());
    }
}
