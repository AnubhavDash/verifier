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
package ch.post.it.evoting.verifier.block.block2.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

public class CheckConfirmedVotesBallotBoxTest extends Block2VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckConfirmedVotesBallotBox();
    }

    @Test
    public void successfulVotesEqualConfirmedVotes() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource(
                "/CheckConfirmedVotesBallotBoxTest/OK-SUCC").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void successfulVotesNotEqualConfirmedVotes() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The list of confirmed votes and the list of successful votes are not equal.");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-SUCC").toURI()));
    }

    @Test
    public void failedVotesEqualUnconfirmedVotes() throws Exception {
        final VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource(
                "/CheckConfirmedVotesBallotBoxTest/OK-FAIL").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void failedVotesNotEqualUnconfirmedVotes() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The list of unconfirmed votes and the list of failed votes are not equal.");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-FAIL").toURI()));
    }

    @Test
    public void successfulFailedVotesEqualConfirmedUnconfirmedVotes() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource(
                "/CheckConfirmedVotesBallotBoxTest/OK-SUCC-FAIL").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void duplicatedConfUnconfVotes() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("A voting card ID appears multiple times in the downloaded ballot box.");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-DUP-CONF-UNCONF").toURI()));
    }

    @Test
    public void duplicatedConfVotes() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("A voting card ID appears multiple times in the downloaded ballot box.");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-DUP-CONF").toURI()));
    }

    @Test
    public void duplicatedUnconfVotes() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("A voting card ID appears multiple times in the downloaded ballot box.");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-DUP-UNCONF").toURI()));
    }

    @Test
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The list of confirmed votes and the list of successful votes are not equal.");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKnotFile() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage("downloadedBallotBox.*\\.csv");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-NOTFILE").toURI()));
    }
}
