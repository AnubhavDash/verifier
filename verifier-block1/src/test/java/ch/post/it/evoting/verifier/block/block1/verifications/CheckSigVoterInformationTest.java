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
package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.VerificationFailureWrappedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.isA;

public class CheckSigVoterInformationTest extends Block1VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckSigVoterInformation();
    }

    @Test
    public void executeTestAllSignValid() throws Exception {
        final VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource(
                "/CheckSigVoterInformationTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestOneSignInvalid() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the file failed");
        verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKCredentialFileNotFound() throws Exception {
        exceptionRule.expect(VerificationFailureWrappedException.class);
        exceptionRule.expectCause(isA(NoSuchFileException.class));
        exceptionRule.expectMessage("voterInformation.csv");
        verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE").toURI()));
    }

    @Test
    public void executeTestNOKSignFileNotFound() throws Exception {
        exceptionRule.expect(VerificationFailureWrappedException.class);
        exceptionRule.expectCause(isA(NoSuchFileException.class));
        exceptionRule.expectMessage("voterInformation.csv.sign");
        verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE2").toURI()));
    }
}
