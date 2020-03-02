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
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class CheckSigDataConfigTest extends Block1VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckSigDataConfig();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/CheckSigDataConfigTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the file dataConfig_updated failed");
        verification.verify(Paths.get(getClass().getResource("/CheckSigDataConfigTest/NOK/NOK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundCertificate() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ADMIN_BOARD_CERT);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigDataConfigTest/NOK/NOK-NOFILE").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundRootCertificate() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.TENANT_100);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigDataConfigTest/NOK/NOK-NOFILE2").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundDataConfig() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.DATA_CONFIG_UPDATED);
        exceptionRule.expectMessage(matchesRegex(structureNode.getQualifier()));
        verification.verify(Paths.get(getClass().getResource("/CheckSigDataConfigTest/NOK/NOK-NOFILE3").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundDataConfigMetadata() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.DATA_CONFIG_UPDATED);
        exceptionRule.expectMessage(matchesRegex(structureNode.getQualifier() + RelationType.METADATA.toFileExtension()));
        verification.verify(Paths.get(getClass().getResource("/CheckSigDataConfigTest/NOK/NOK-NOFILE4").toURI()));
    }

}
