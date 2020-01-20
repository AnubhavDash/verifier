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
package ch.post.it.evoting.verifier.block.block3.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckSigDownloadedBallotBoxTest extends Block3VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckSigDownloadedBallotBox();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKInvalidSignature() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the file failed");
        verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(CheckSigDownloadedBallotBox.DOWNLOADED_BALLOT_BOX_CSV);
        verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK-NOFILE").toURI()));
    }

    @Test
    public void executeTestNOKBallotFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(CheckSigDownloadedBallotBox.BALLOT_BOX_JSON);
        verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK-NOFILE2").toURI()));
    }

    @Test
    public void executeTestNOKElectionFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(CheckSigDownloadedBallotBox.ELECTION_INFORMATION_CONTENTS_JSON);
        verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK-NOFILE3").toURI()));
    }

    @Test
    public void executeTestNOKSignCertNotFound() throws Exception {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage(String.format("%s certificate is missing!", CheckSigDownloadedBallotBox.BALLOT_BOX_CERT));
        verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK-NOCERT").toURI()));
    }

    @Test
    public void executeTestNOKInterCertNotFound() throws Exception {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage(String.format("%s certificate is missing!", CheckSigDownloadedBallotBox.SERVICES_CA));
        verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK-NOCERT2").toURI()));
    }

    @Test
    public void executeTestNOKRootNotFound() throws Exception {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage(String.format("%s certificate is missing!", CheckSigDownloadedBallotBox.ELECTION_ROOT_CA));
        verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK-NOCERT3").toURI()));
    }
}
