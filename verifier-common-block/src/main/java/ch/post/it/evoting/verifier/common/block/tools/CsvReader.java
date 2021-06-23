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
package ch.post.it.evoting.verifier.common.block.tools;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import ch.post.it.evoting.verifier.common.block.exceptions.CsvReaderException;

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
