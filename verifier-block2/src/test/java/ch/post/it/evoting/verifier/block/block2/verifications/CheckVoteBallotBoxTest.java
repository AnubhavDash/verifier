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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;

class CheckVoteBallotBoxTest extends Block2VerificationAbstractTest {

    @BeforeEach
    void setup() {
        verification = new CheckVoteBallotBox();
    }

    @Test
    void executeTestOK() throws Exception {
        VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/CheckVoteBallotBoxTest/OK").toURI()));
        assertNotNull(verificationResult);
        assertEquals(Status.OK, verificationResult.getStatus());
    }

    // TODO Implement the following missing tests cases:
    //  - Duplicate votingCardId in downloadedBallotBox files
    //  - The number of encrypted votes in the secure logs and downloaded boxes are not equal
    //  - EncryptedOptions is not the same in DownloadedBallotBox and SecureLogs
    //  - Unknown votingCardId

    @Test
    void executeTestNOKFileNotFound() {
        final FileNotFoundException ex = assertThrows(
                FileNotFoundException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckVoteBallotBoxTest/NOK-NOTFILE").toURI()))
        );
        assertEquals("voterInformation.*\\.csv", ex.getMessage());
    }

}
