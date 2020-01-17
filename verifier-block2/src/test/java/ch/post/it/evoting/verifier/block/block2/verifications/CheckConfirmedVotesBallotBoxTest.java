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
import java.nio.file.Paths;

public class CheckConfirmedVotesBallotBoxTest extends Block2VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckConfirmedVotesBallotBox();
    }

    @Test
    public void executeTest() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("There is a mismatch between the list of successful or failed votes and the download ballot box");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK").toURI()));
    }

    // TODO NOK for count mismatch

    @Test
    public void executeTestNOKnotFile() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("downloadedBallotBox.*\\.csv");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-NOTFILE").toURI()));
    }

    @Test
    public void executeTestNOKnotFile2() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("successfulVotes.csv");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-NOTFILE2").toURI()));
    }

    @Test
    public void executeTestNOKnotFile3() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("failedVotes.csv");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-NOTFILE3").toURI()));
    }
}
