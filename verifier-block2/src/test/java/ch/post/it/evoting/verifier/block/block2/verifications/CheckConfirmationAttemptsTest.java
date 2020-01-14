/**
 * This file is part of Verifier Swiss Post.
 * <p>
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block2.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;

public class CheckConfirmationAttemptsTest {
    private CheckConfirmationAttempts checkConfirmationAttempts;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        checkConfirmationAttempts = new CheckConfirmationAttempts();
    }

    @Test
    /* remove ignore when we got secureLog files with correct pattern */
    @Ignore
    public void executeTest() throws Exception {
        VerificationResult verificationResult = checkConfirmationAttempts.verify(new File(getClass().getResource("/CheckConfirmationAttemptsTest/OK").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    @Ignore
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("TODO");
        checkConfirmationAttempts.verify(new File(getClass().getResource("/CheckConfirmationAttemptsTest/NOK").getFile()));
    }

    @Test
    public void executeTestNOKnotFile() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("voterInformation.*\\.csv");
        checkConfirmationAttempts.verify(new File(getClass().getResource("/CheckConfirmationAttemptsTest/NOK-NOTFILE").getFile()));
    }

    @Test
    public void executeTestNOKnotFile2() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("mapping_cc_hosts.csv");
        checkConfirmationAttempts.verify(new File(getClass().getResource("/CheckConfirmationAttemptsTest/NOK-NOTFILE2").getFile()));
    }

    @Test
    @Ignore
    public void executeTestNOKnotFile3() throws Exception {
        // TODO Check for secureLogs file
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("secureLogs");
        checkConfirmationAttempts.verify(new File(getClass().getResource("/CheckConfirmationAttemptsTest/NOK-NOTFILE3").getFile()));
    }

}
