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
package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckSigVoterInformationTest extends Block1VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckSigVoterInformation();
    }

    @Test
    public void executeTestAllSignValid() throws Exception {
        final VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource(
                "/CheckSigVoterInformationTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestOneSignInvalid() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the file failed");
        verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundCertificate() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ADMIN_BOARD_CERT);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundIntermediateCertificate() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.TENANT_100);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE2").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundRootCertificate() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.PLATFORM_ROOT_CA);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE3").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundVotingCardSetsIdDir() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.VOTING_CARD_SETS_ID_DIR);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE4").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundCredential() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.VOTER_INFORMATION);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE5").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundCredentialSign() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.VOTER_INFORMATION);
        exceptionRule.expectMessage(matchesRegex(structureNode.getQualifier() + RelationType.SIGN.toFileExtension()));
        verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE6").toURI()));
    }
}
