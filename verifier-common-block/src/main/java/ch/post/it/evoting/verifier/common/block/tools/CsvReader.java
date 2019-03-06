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
package ch.post.it.evoting.verifier.common.block.tools;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

public class CsvReader<T> {
    private Function<String[], T> mapper;
    private Stream<String> stream;
    private boolean hasHeader;
    private String separator;

    public CsvReader(String filename, Charset charset, boolean hasHeader, String separator, Function<String[], T> mapper) throws IOException {
        this.mapper = mapper;
        this.stream = Files.lines(Paths.get(filename), charset);
        this.hasHeader = hasHeader;
        this.separator = separator;
    }

    public Iterable<T> process() {
        return () -> {
            //skip the first line (header)
            //split with 'separator' character
            //map to object with given Function
            return stream.skip(hasHeader ? 1 : 0).map(l -> l.split(separator)).map(mapper).iterator();
        };
    }
}
