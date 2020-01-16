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
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckSigValidVotesTest {
    private CheckSigValidVotes checkSigValidVotes;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        checkSigValidVotes = new CheckSigValidVotes();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = new CheckSigValidVotes().verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKCsvKo() throws Exception {
        // TODO Check if test is relevant, because executeTestNOKCertKo got the same error
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the svv_[EE_alias].csv report failed");
        checkSigValidVotes.verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest/NOK/CSV-NOT-OK").toURI()));
    }

    @Test
    public void executeTestNOKCertKo() throws Exception {
        // TODO Check if test is relevant, because executeTestNOKCsvKo got the same error
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the svv_[EE_alias].csv report failed");
        checkSigValidVotes.verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest/NOK/CERT-NOT-OK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage(".*\\.pem");
        checkSigValidVotes.verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest/NOK-NOTFILE").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound2() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage("tenant_.*\\.pem");
        checkSigValidVotes.verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest/NOK-NOTFILE2").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound3() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage("svv_.*\\.csv");
        checkSigValidVotes.verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest/NOK-NOTFILE3").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound4() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(".csv.metadata");
        checkSigValidVotes.verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest/NOK-NOTFILE4").toURI()));
    }

}
