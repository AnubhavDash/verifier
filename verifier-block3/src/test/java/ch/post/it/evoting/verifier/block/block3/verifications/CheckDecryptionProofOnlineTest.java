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
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckDecryptionProofOnlineTest extends Block3VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckDecryptionProofOnline();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult result = verification.verify(Paths.get(getClass().getResource("/CheckDecryptionProofOnlineTest/OK").toURI()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("the number of control components expected is 3 but actual is 1");
        verification.verify(Paths.get(getClass().getResource("/CheckDecryptionProofOnlineTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKNotFile() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        // Get the file name from structure node for the expected message
        StructureNode ccMixingKeysStructureNode = verification.getPathService().getStructureNode(StructureKey.CC_MIXING_KEYS);
        exceptionRule.expectMessage(ccMixingKeysStructureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckDecryptionProofOnlineTest/NOK-NOTFILE").toURI()));
    }

}
