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
package ch.post.it.evoting.verifier.block.block4.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
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
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("TODO");
        verification.verify(Paths.get(getClass().getResource("/CheckOptionsMappingTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage("dataConfig_updated_.*\\.json");
        verification.verify(Paths.get(getClass().getResource("/CheckOptionsMappingTest/NOK-NOFILE").toURI()));
    }

    // TODO File not found error test evoting-decrypt_.*\.xml

    @Test
    public void executeTestNOKFileNotFound2() throws Exception {
        exceptionRule.expect(FileNotFoundException.class);
        exceptionRule.expectMessage("decompressedVotes\\.csv");
        verification.verify(Paths.get(getClass().getResource("/CheckOptionsMappingTest/NOK-NOFILE2").toURI()));
    }

}
