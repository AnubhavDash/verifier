/*
 * Copyright 2021 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.block.block4;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.common.block.VerificationSuite;

@Component
public class Block4VerificationSuite extends VerificationSuite {

	public static final String RESOURCE_BUNDLE_NAME = "block4/resources";

	public static final Path PATH_CERTIFICATES = Paths.get("certificates/");
	public static final Path PATH_ADMINBOARD = Paths.get("adminboard/");
	public static final Path PATH_ELECTION_SETUP = Paths.get("election_setup/");
	public static final Path PATH_BALLOTBOXES = Paths.get("ballotboxes/");
	public static final Path PATH_RESULTS = Paths.get("results/");

	public Block4VerificationSuite() {
		super(Block4VerificationSuite.class.getPackage().getName() + ".verifications");
	}

}
