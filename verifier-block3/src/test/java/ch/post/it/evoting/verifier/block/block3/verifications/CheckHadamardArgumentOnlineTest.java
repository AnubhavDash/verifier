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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class CheckHadamardArgumentOnlineTest extends Block3VerificationAbstractTest {

    @BeforeEach
    void setup() {
        verification = new CheckHadamardArgumentOnline();
    }

    @Test
    void executeTestOK() throws Exception {
        VerificationResult result = verification.verify(Paths.get(getClass().getResource("/CheckHadamardArgumentOnlineTest/OK").toURI()));
        assertEquals(Status.OK, result.getStatus());
    }

    @Test
    @Disabled("Fix this test by not using status NOK anymore and implementing meaningful asserts.")
    void executeTestNOK() throws Exception {
        VerificationResult result = verification.verify(Paths.get(getClass().getResource("/CheckHadamardArgumentOnlineTest/NOK").toURI()));
        assertEquals(Status.NOK, result.getStatus());
    }

    @Test
    void executeTestKOFileNotFound() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckHadamardArgumentOnlineTest/NOK-NOTFILE").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.BALLOT_BOXES_DIR);
        assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
    }
}
