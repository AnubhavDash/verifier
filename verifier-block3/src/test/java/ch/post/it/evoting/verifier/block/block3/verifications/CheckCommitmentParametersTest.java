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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckCommitmentParametersTest extends Block3VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckCommitmentParameters();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/CheckCommitmentParametersTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKpIsNotUnique() throws Exception {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("P parameter not unique");
        verification.verify(Paths.get(getClass().getResource("/CheckCommitmentParametersTest/NOK/P-NOK").toURI()));
    }

    @Test
    public void executeTestNOKEulerCriterionKo() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("Commitment parameters verification failed");
        verification.verify(Paths.get(getClass().getResource("/CheckCommitmentParametersTest/NOK/BIGINT-NOK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(verification.getPathService().getStructureNode(StructureKey.COMMITMENT_PARAMETERS).getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckCommitmentParametersTest/NOK-NOTFILE").toURI()));
    }
}
