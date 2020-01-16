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
import java.nio.file.Paths;

public class CheckTallyingListsTest {

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  private CheckTallyingLists checkTallyingLists;

  @Before
  public void setup() {
    checkTallyingLists = new CheckTallyingLists();
  }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = checkTallyingLists.
                verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/OK").toURI()));

        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage(
                "The occurrences for list are different for counting Circle in eCH-0110 and evoting-decrypt");

        VerificationResult verificationResult = checkTallyingLists.
                verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKVoteCountCountingCircle() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("cannot find the decrypt data for given countingCircle");

        VerificationResult verificationResult = checkTallyingLists.verify(Paths.get(
                getClass().getResource("/CheckTallyingListsTest/NOK-VOTECOUNT-COUNTINGCIRCLE").toURI()));
    }

    @Test
    public void executeTestNOKVoteCountElection() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("cannot find the decrypt data for given election");

        VerificationResult verificationResult = checkTallyingLists.verify(
                Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-VOTECOUNT-ELECTION").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundConfiguration() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage("configuration-anonymized.xml");

        VerificationResult verificationResult = checkTallyingLists.
                verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-CONFIG").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundEvoting() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage("evoting-decrypt_.*\\.xml");

        VerificationResult verificationResult = checkTallyingLists.
                verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-EVOTING").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundECH() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage("eCH-0110_.*\\.xml");

        VerificationResult verificationResult = checkTallyingLists.
                verify(Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-eCH").toURI()));
    }
}
