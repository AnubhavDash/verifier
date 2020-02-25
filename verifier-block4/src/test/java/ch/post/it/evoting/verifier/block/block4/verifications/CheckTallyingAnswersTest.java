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
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckTallyingAnswersTest extends Block4VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckTallyingAnswers();
    }

    @Test
    public void executeTestOK() throws Exception {
        // TODO The eCH0110 must contains vote results to be valid
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckTallyingAnswersTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    @Ignore
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("Business Failure");
        verification.verify(Paths.get(getClass().getResource("/CheckTallyingAnswersTest/NOK").toURI()));
    }

    // TODO The following NOK tests must be implemented:
    //  - Number of YES votes verification failed in standard ballot
    //  - Number of NO votes verification failed in standard ballot
    //  - Number of EMPTY votes verification failed in standard ballot
    //  - Number of YES votes verification failed in variant ballot
    //  - Number of NO votes verification failed in variant ballot
    //  - Number of EMPTY votes verification failed in variant ballot

    @Test
    public void executeTestNOKFileNotFoundConfiguration() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(matchesRegex(verification.getPathService().getStructureNode(StructureKey.CONFIG_ANONYMIZED).getQualifier()));
        verification.verify(Paths.get(getClass().getResource("/CheckTallyingAnswersTest/NOK-NOFILE-CONFIG").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundEVotingDecryptResult() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(matchesRegex(verification.getPathService().getStructureNode(StructureKey.EVOTING_DECRYPT_RESULT).getQualifier()));
        verification.verify(Paths.get(getClass().getResource("/CheckTallyingAnswersTest/NOK-NOFILE-EVOTING").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundECH0110() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(matchesRegex(verification.getPathService().getStructureNode(StructureKey.ECH0110).getQualifier()));
        verification.verify(Paths.get(getClass().getResource("/CheckTallyingAnswersTest/NOK-NOFILE-eCH").toURI()));
    }

}
