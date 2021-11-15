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
package ch.post.it.evoting.verifier.block.block2.loader;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.block.tools.path.PathHelper;

import reactor.core.publisher.Flux;

public class VoterInformationDataExtractor {
	private VoterInformationDataExtractor() {
		//private ctor, use static
	}

	public static VoterInformationStruct getInfo(Path inputDirectoryPath) throws IOException {
		List<File> voterInformationFiles = PathHelper
				.getFiles(
						inputDirectoryPath
								.resolve(Block2VerificationSuite.PATH_ELECTION_SETUP)
								.resolve(Block2VerificationSuite.PATH_VOTING_CARD_SETS)
								.toFile(),
						"voterInformation.*\\.csv",
						true);

		return Flux.fromStream(voterInformationFiles.stream())
				.flatMap(f -> {
					try {
						return Flux.fromStream(Files.lines(f.toPath())).map(s -> s.split(",")[4]);
					} catch (IOException e) {
						throw new UncheckedIOException("An error occurs while parsing the voterInformation.csv files", e);
					}
				})
				.reduce(new VoterInformationStruct(), (struct, eeid) -> {
					struct.increment();
					struct.setAndCheckUniqueEeid(eeid);
					return struct;
				})
				.block();
	}

}



