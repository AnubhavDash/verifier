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
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckOptionsMappingTest extends Block4VerificationAbstractTest {
    
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckOptionsMapping();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckOptionsMappingTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestOKWriteIns() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckOptionsMappingTest/OK-WRITEINS").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    @Ignore("TODO")
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("TODO");
        verification.verify(Paths.get(getClass().getResource("/CheckOptionsMappingTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundDataConfig() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.DATA_CONFIG_UPDATED);
        exceptionRule.expectMessage(matchesRegex(structureNode.getQualifier()));
        verification.verify(Paths.get(getClass().getResource("/CheckOptionsMappingTest/NOK-NOFILE").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundBallotBoxIdDirectories() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(verification.getPathService().getStructureNode(StructureKey.BALLOT_BOX_ID_DIR).getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckOptionsMappingTest/NOK-NOFILE2").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundEVotingDecryptResult() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(verification.getPathService().getStructureNode(StructureKey.EVOTING_DECRYPT_RESULT).getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckOptionsMappingTest/NOK-NOFILE3").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundDecompressedVotes() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(verification.getPathService().getStructureNode(StructureKey.DECOMPRESSED_VOTES).getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckOptionsMappingTest/NOK-NOFILE4").toURI()));
    }

}
