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
package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.Paths;

public class CheckSigElectionImportTest extends Block1VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckSigElectionImport();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckSigElectionImportTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKCertKo() throws Exception {
        // TODO Check if test is relevant, because executeTestNOKJsonKo got the same error
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the file failed");
        verification.verify(Paths.get(getClass().getResource("/CheckSigElectionImportTest/NOK/CERT-NOT-OK").toURI()));
    }

    @Test
    public void executeTestNOKJsonKo() throws Exception {
        // TODO Check if test is relevant, because executeTestNOKCertKo got the same error
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the file failed");
        verification.verify(Paths.get(getClass().getResource("/CheckSigElectionImportTest/NOK/JSON-NOT-OK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("integrationCA.pem");
        verification.verify(Paths.get(getClass().getResource("/CheckSigElectionImportTest/NOK/NOK-NOFILE").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound2() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("AP_election_import_.*\\.json");
        verification.verify(Paths.get(getClass().getResource("/CheckSigElectionImportTest/NOK/NOK-NOFILE2").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound3() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("AP_election_import_demo_bk_06.json.p7");
        verification.verify(Paths.get(getClass().getResource("/CheckSigElectionImportTest/NOK/NOK-NOFILE3").toURI()));
    }
}
