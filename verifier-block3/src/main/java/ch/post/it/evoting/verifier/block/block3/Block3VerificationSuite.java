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
package ch.post.it.evoting.verifier.block.block3;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.common.block.VerificationSuite;

@Component
public class Block3VerificationSuite extends VerificationSuite {

	public static final String RESOURCE_BUNDLE_NAME = "block3/resources";
	public static final Path PATH_ELECTION_SETUP = Paths.get("election_setup/");
	public static final Path PATH_BALLOTBOXES = Paths.get("ballotboxes/");
	public static final Path PATH_CERTIFICATES = Paths.get("certificates/");
	public static final Path PATH_CC_MIXING_KEYS = Paths.get("certificates/cc_mixing_keys/");
	public static final Path PATH_ADMINBOARD = Paths.get("adminboard/");
	public static final Path PATH_CRYPTO_SETUP = Paths.get("crypto_setup/");

	public Block3VerificationSuite() {
		super(Block3VerificationSuite.class.getPackage().getName() + ".verifications");
	}

}
