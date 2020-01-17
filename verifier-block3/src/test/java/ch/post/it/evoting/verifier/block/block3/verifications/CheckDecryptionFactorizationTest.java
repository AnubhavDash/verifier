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
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.nio.file.Paths;

public class CheckDecryptionFactorizationTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult result = new CheckDecryptionFactorization().verify(Paths.get(getClass().getResource("/CheckDecryptionFactorizationTest/OK").toURI()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    public void executeTestWithWriteInsOK() throws Exception {
        VerificationResult result = new CheckDecryptionFactorization().verify(Paths.get(getClass().getResource("/CheckDecryptionFactorizationTest/OK-WRITE-INS").toURI()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("factorization not correct !");

        VerificationResult result = new CheckDecryptionFactorization().verify(Paths.get(getClass().getResource("/CheckDecryptionFactorizationTest/NOK").toURI()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }
}
