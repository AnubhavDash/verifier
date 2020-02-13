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
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckConfirmedVotesBallotBoxTest extends Block2VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckConfirmedVotesBallotBox();
    }

    @Test
    public void confirmedVotesEqualSuccessfulVotes() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource(
                "/CheckConfirmedVotesBallotBoxTest/OK-CONF").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void confirmedVotesNotEqualSuccessfulVotes() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The list of confirmed votes and the list of successful votes are not equal.");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-CONF").toURI()));
    }

    @Test
    public void unconfirmedVotesEqualFailedVotes() throws Exception {
        final VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource(
                "/CheckConfirmedVotesBallotBoxTest/OK-UNCONF").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void unconfirmedVotesNotEqualFailedVotes() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The list of unconfirmed votes and the list of failed votes are not equal.");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-UNCONF").toURI()));
    }

    @Test
    public void confirmedUnconfirmedVotesEqualSuccessfulFailedVotes() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource(
                "/CheckConfirmedVotesBallotBoxTest/OK-CONF-UNCONF").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void duplicatedConfirmedUnconfirmedVotes() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("A voting card ID appears multiple times in the downloaded ballot box.");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-DUP-CONF-UNCONF").toURI()));
    }

    @Test
    public void duplicatedConfirmedVotes() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("A voting card ID appears multiple times in the downloaded ballot box.");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-DUP-CONF").toURI()));
    }

    @Test
    public void duplicatedUnconfirmedVotes() throws Exception {
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
    public void downloadedBallotBoxFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.DOWNLOADED_BALLOT_BOX);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmedVotesBallotBoxTest/NOK-NOTFILE").toURI()));
    }
}
