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
package ch.post.it.evoting.verifier.block.block4.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckTallyingListsTest extends Block4VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckTallyingLists();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The occurrences for list are different for counting Circle in eCH-0110 and evoting-decrypt");
        verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKVoteCountCountingCircle() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("cannot find the decrypt data for given countingCircle");
        verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-VOTECOUNT-COUNTINGCIRCLE").toURI()));
    }

    @Test
    public void executeTestNOKVoteCountElection() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("cannot find the decrypt data for given election");
        verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-VOTECOUNT-ELECTION").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundConfiguration() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(matchesRegex(verification.getPathService().getStructureNode(StructureKey.CONFIG_ANONYMIZED).getQualifier()));
        verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-CONFIG").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundEVoting() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(matchesRegex(verification.getPathService().getStructureNode(StructureKey.EVOTING_DECRYPT_RESULT).getQualifier()));
        verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-EVOTING").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundECH0110() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(matchesRegex(verification.getPathService().getStructureNode(StructureKey.ECH0110).getQualifier()));
        verification.verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-eCH").toURI()));
    }
}
