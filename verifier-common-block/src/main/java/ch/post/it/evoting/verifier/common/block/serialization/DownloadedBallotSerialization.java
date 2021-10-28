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
package ch.post.it.evoting.verifier.common.block.serialization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.DownloadedBallot;

public class DownloadedBallotSerialization {

	private DownloadedBallotSerialization() {
		throw new UnsupportedOperationException("DownloadedBallotSerialization should not be instantiated");
	}

	//Should be used in a try with resource block to guarantee that the file stream is closed
	public static Stream<DownloadedBallot> deserializeDownloadedBallotBox(final Path path) throws IOException {
		return Files.lines(path)
				.map(DownloadedBallotSerialization::deserializeDownloadedBallot)
				// Remove empty lines, signature etc...
				.filter(Objects::nonNull);
	}

	public static DownloadedBallot deserializeDownloadedBallot(String line) {
		if (!line.isEmpty() && line.contains("}}|") && !line.contains("||")) {
			int endJsonObjectIndex = line.indexOf("}}|") + 2;
			var json = line.substring(0, endJsonObjectIndex);
			try {
				return Deserializer.fromJson(TypeConverter.stringToByte(json), DownloadedBallot.class);
			} catch (IOException e) {
				throw new RuntimeException();
			}
		} else {
			return null;
		}
	}
}