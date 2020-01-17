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
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class CheckNumberVoteCastCodesTest extends Block2VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckNumberVoteCastCodes();
    }

    @Test
    @Ignore
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckNumberVoteCastCodesTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    @Ignore
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("TODO");
        verification.verify(Paths.get(getClass().getResource("/CheckNumberVoteCastCodesTest/NOK").toURI()));
    }

    // TODO Implement the following missing tests cases:
    //  - No values found while counting log foreach control component
    //  - Number of component control is not 4
    //  - No GENPVCC log found for the defined electionEventId
    //  - Count of log for partial vote cast code generation is not the same for each control component
    //  - The number of log entries does not match with the number of voters

    @Test
    public void executeTestNOKnotFile() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("voterInformation.*\\.csv");
        verification.verify(Paths.get(getClass().getResource("/CheckNumberVoteCastCodesTest/NOK-NOTFILE").toURI()));
    }

    @Test
    public void executeTestNOKnotFile2() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("mapping_cc_hosts.csv");
        verification.verify(Paths.get(getClass().getResource("/CheckNumberVoteCastCodesTest/NOK-NOTFILE2").toURI()));
    }
}
