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

public class CheckTallyingCandidatesTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private CheckTallyingCandidates checkTallyingCandidates;

    @Before
    public void setup() {
        checkTallyingCandidates = new CheckTallyingCandidates();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = checkTallyingCandidates.
                verify(new File(getClass().getResource("/CheckTallyingCandidates/OK").getFile()));

        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKMajoralCountOfVotesTotal() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage(
                "The count of votes total for the candidate does not match in majoral election");

        VerificationResult verificationResult = checkTallyingCandidates.verify(
                new File(getClass().getResource("/CheckTallyingCandidates/NOK-COUNTOFVOTES-MAJORAL").getFile()));
    }

    @Test
    public void executeTestNOKProportionalCountOfVotesTotal() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage(
                "The count of votes total for the candidate does not match in proportional election");

        VerificationResult verificationResult = checkTallyingCandidates.verify(new File(
                getClass().getResource("/CheckTallyingCandidates/NOK-COUNTOFVOTES-PROPORTIONAL").getFile()));
    }

    @Test
    public void executeTestOKWriteIns() throws Exception {
        VerificationResult verificationResult = checkTallyingCandidates.
                verify(new File(getClass().getResource("/CheckTallyingCandidates/OK-WRITEINS").getFile()));

        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKWriteInsContainsKeyMajoral() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage(
                "The count for the candidate does not match in writeIns-containsKey majoral election");

        VerificationResult verificationResult = checkTallyingCandidates.verify(new File(
                getClass().getResource("/CheckTallyingCandidates/NOK-WRITEINS-CONTAINSKEY-MAJORAL").getFile()));
    }

    @Test
    public void executeTestNOKWriteInsContainsKeyProportional() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage(
                "The count for the candidate does not match in writeIns-containsKey proportional election");

        VerificationResult verificationResult = checkTallyingCandidates.verify(new File(
                getClass().getResource("/CheckTallyingCandidates/NOK-WRITEINS-CONTAINSKEY-PROPORTIONAL").getFile()));
    }

    @Test
    public void executeTestNOKWriteInsEqualsMajoral() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage(
                "The count for the candidate does not match in writeIns-equals majoral election");

        VerificationResult verificationResult = checkTallyingCandidates.verify(new File(
                getClass().getResource("/CheckTallyingCandidates/NOK-WRITEINS-EQUALS-MAJORAL").getFile()));
    }

    @Test
    public void executeTestNOKWriteInsEqualsProportional() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage(
                "The count for the candidate does not match in writeIns-equals proportional election");

        VerificationResult verificationResult = checkTallyingCandidates.verify(new File(
                getClass().getResource("/CheckTallyingCandidates/NOK-WRITEINS-EQUALS-PROPORTIONAL").getFile()));
    }

    @Test
    public void executeTestNOKDecryptCount() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("cannot find the decrypt data for given countingCircle");

        VerificationResult verificationResult = checkTallyingCandidates.
                verify(new File(getClass().getResource("/CheckTallyingCandidates/NOK-DECRYPTCOUNT").getFile()));
    }

    @Test
    public void executeTestNOKFileNotFoundConfiguration() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage("configuration-anonymized.xml");

        VerificationResult verificationResult = checkTallyingCandidates.
                verify(new File(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-CONFIG").getFile()));
    }

    @Test
    public void executeTestNOKFileNotFoundEvoting() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage("evoting-decrypt_.*\\.xml");

        VerificationResult verificationResult = checkTallyingCandidates.
                verify(new File(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-EVOTING").getFile()));
    }

    @Test
    public void executeTestNOKFileNotFoundECH() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage("eCH-0110_.*\\.xml");

        VerificationResult verificationResult = checkTallyingCandidates.
                verify(new File(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-eCH").getFile()));
    }
}
