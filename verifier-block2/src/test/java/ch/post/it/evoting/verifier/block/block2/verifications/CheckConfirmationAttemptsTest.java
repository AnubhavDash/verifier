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
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.Paths;

public class CheckConfirmationAttemptsTest extends Block2VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckConfirmationAttempts();
    }

    @Test
    /* remove ignore when we got secureLog files with correct pattern */
    public void executeTest() throws Exception {
        VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/CheckConfirmationAttemptsTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("TODO");
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmationAttemptsTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundVoterInformation() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("voterInformation.*\\.csv"); // TODO trop la mort à faire pour le moment.
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmationAttemptsTest/NOK-NOTFILE").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundMappingCcHosts() throws Exception {
        exceptionRule.expect(IOException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.MAPPING_CC_HOSTS);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckConfirmationAttemptsTest/NOK-NOTFILE2").toURI()));
    }

}
