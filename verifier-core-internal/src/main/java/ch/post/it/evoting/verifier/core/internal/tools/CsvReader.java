/*
 * Copyright 2022 Post CH Ltd
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
package ch.post.it.evoting.verifier.core.internal.tools;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import ch.post.it.evoting.verifier.core.internal.exceptions.CsvReaderException;

public class CsvReader<T> {
	private final Function<String[], T> mapper;
	private final Stream<String> stream;
	private final boolean hasHeader;
	private final String separator;

	public CsvReader(String filename, Charset charset, boolean hasHeader, String separator, Function<String[], T> mapper) {
		this(Paths.get(filename), charset, hasHeader, separator, mapper);
	}

	public CsvReader(Path filePath, Charset charset, boolean hasHeader, String separator, Function<String[], T> mapper) {
		this(Collections.singletonList(filePath), charset, hasHeader, separator, mapper);
	}

	public CsvReader(List<Path> filePaths, Charset charset, boolean hasHeader, String separator, Function<String[], T> mapper) {
		this.mapper = mapper;
		this.hasHeader = hasHeader;
		this.separator = separator;

		this.stream = filePaths.stream().flatMap(p -> {
			try {
				return Files.lines(p, charset);
			} catch (IOException e) {
				throw new CsvReaderException(e);
			}
		});
	}

	public Iterable<T> process() {
		return () ->
				//skip the first line (header)
				//split with 'separator' character
				//map to object with given Function
				stream.skip(hasHeader ? 1 : 0).map(l -> l.split(separator)).map(mapper).iterator();

	}
}
