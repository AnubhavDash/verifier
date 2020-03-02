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
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckShuffleArgumentOnlineTest extends Block3VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckShuffleArgumentOnline();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult result = verification.verify(Paths.get(getClass().getResource("/CheckShuffleArgumentOnlineTest/OK").toURI()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    public void executeTestNOK() throws Exception {
        VerificationResult result = verification.verify(Paths.get(getClass().getResource("/CheckShuffleArgumentOnlineTest/NOK").toURI()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }

    @Test
    public void executeTestNOKnot3ControlComponents() throws Exception {
        VerificationResult result = verification.verify(Paths.get(getClass().getResource("/CheckShuffleArgumentOnlineTest/NOK-CC").toURI()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }

    @Test
    public void executeTestKOFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.BALLOT_BOXES_DIR);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckShuffleArgumentOnlineTest/NOK-NOTFILE").toURI()));
    }
}
