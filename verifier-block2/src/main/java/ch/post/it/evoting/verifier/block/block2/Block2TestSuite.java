/**
 * This file is part of Verifier Swiss Post.
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block2;

import ch.post.it.evoting.verifier.common.block.TestSuite;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Block2TestSuite extends TestSuite {

    public static final String RESOURCE_BUNDLE_NAME = "block2/resources";

    public static final Path PATH_BALLOTBOXES = Paths.get("ballotboxes/");
    public static final Path PATH_ELECTION_SETUP = Paths.get("election_setup/");
    public static final Path PATH_CERTIFICATES = Paths.get("certificates/");
    public static final Path PATH_CC_LOG_SIGN_CERTIFICATES = Paths.get("certificates/log_sign_keys");
    public static final Path PATH_CC_CA_CERTIFICATES = Paths.get("certificates/cc_ca_keys");
    public static final Path PATH_VOTING_CARD_SETS = Paths.get("voting_card_sets/");
    public static final Path PATH_SECURE_LOGS = Paths.get("secureLogs/");

    public Block2TestSuite() {
        super(Block2TestSuite.class.getPackage().getName() + ".tests");
    }

}
