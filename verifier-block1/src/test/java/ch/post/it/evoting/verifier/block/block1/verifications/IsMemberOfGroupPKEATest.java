/**
 * This file is part of Verifier Swiss Post.
 * <p>
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * <p>
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

public class IsMemberOfGroupPKEATest extends Block1VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new IsMemberOfGroupPKEA();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/IsMemberOfGroupPKEATest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("No such Elements was found in the publicKey");
        verification.verify(Paths.get(getClass().getResource("/IsMemberOfGroupPKEATest/NOK/NOK").toURI()));
    }

    @Test
    public void executeTestNOK2() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("Euler criterion does not equal to 1");
        verification.verify(Paths.get(getClass().getResource("/IsMemberOfGroupPKEATest/NOK/NOK2").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("electoralAuthority\\.json");
        verification.verify(Paths.get(getClass().getResource("/IsMemberOfGroupPKEATest/NOK/NOK-NOFILE").toURI()));
    }

}
