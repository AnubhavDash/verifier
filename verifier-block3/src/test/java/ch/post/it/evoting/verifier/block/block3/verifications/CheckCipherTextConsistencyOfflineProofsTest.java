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
import org.junit.Test;

import java.io.File;

public class CheckCipherTextConsistencyOfflineProofsTest {

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult result = new CheckCipherTextConsistencyOfflineProofs().verify(new File(getClass().getResource("/CheckCiphertextConsistencyOfflineProofsTest/OK").getFile()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    public void executeTestNOK() throws Exception {
        VerificationResult result = new CheckCipherTextConsistencyOfflineProofs().verify(new File(getClass().getResource("/CheckCiphertextConsistencyOfflineProofsTest/NOK").getFile()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }

}
