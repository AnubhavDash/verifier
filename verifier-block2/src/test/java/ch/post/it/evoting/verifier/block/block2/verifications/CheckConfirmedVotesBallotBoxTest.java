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
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;

public class CheckConfirmedVotesBallotBoxTest {
    private CheckConfirmedVotesBallotBox checkConfirmedVotesBallotBox;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        checkConfirmedVotesBallotBox = new CheckConfirmedVotesBallotBox();
    }

    @Test
    public void executeTest() throws Exception {
        VerificationResult verificationResult = checkConfirmedVotesBallotBox.verify(new File(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/OK").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("There is a mismatch between the list of successful or failed votes and the download ballot box");
        checkConfirmedVotesBallotBox.verify(new File(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK").getFile()));
    }

    // TODO NOK for count mismatch

    @Test
    public void executeTestNOKnotFile() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("downloadedBallotBox.*\\.csv");
        checkConfirmedVotesBallotBox.verify(new File(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-NOTFILE").getFile()));
    }

    @Test
    public void executeTestNOKnotFile2() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("successfulVotes.csv");
        checkConfirmedVotesBallotBox.verify(new File(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-NOTFILE2").getFile()));
    }

    @Test
    public void executeTestNOKnotFile3() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("failedVotes.csv");
        checkConfirmedVotesBallotBox.verify(new File(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-NOTFILE3").getFile()));
    }
}
