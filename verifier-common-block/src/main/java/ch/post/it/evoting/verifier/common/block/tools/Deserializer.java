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

import ch.post.it.evoting.verifier.common.block.dto.CredentialDataElement;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Function;
import java.util.stream.Stream;

public class Deserializer {

    private Deserializer() {
        //private constructor, use static
    }

    public static <T> Stream<T> fromLines(File inputFile, String filenamePattern, Function<String, T> mapper) throws IOException {
        return Files.lines(getFile(inputFile, filenamePattern).toPath()).map(mapper);
    }

    public static <T> T fromJson(File inputDirectory, String filenamePattern, Class<T> targetClazz) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.readValue(getFile(inputDirectory, filenamePattern), targetClazz);
    }

    public static <T> T fromJson(byte[] content, Class<T> targetClazz) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.readValue(new String(content, StandardCharsets.UTF_8), targetClazz);
    }

    public static <T> T fromXml(File inputDirectory, String filenamePattern, Class<T> targetClazz) throws IOException, JAXBException {
        return (T) JAXBContext.newInstance(targetClazz).createUnmarshaller().unmarshal(new FileInputStream(getFile(inputDirectory, filenamePattern)));
    }

    public static <T> Iterable<T> fromCsv(File inputDirectory, String filenamePattern, Function<String[], T> mapper) throws IOException {
        return new CsvReader<>(getFile(inputDirectory, filenamePattern).toString(), StandardCharsets.UTF_8, false, ",", mapper).process();
    }

    public static <T> Iterable<T> fromCsv(File inputDirectory, String filenamePattern, String separator, Function<String[], T> mapper) throws IOException {
        return new CsvReader<>(getFile(inputDirectory, filenamePattern).toString(), StandardCharsets.UTF_8, false, separator, mapper).process();
    }

    private static File getFile(File inputDirectory, String filenamePattern) throws FileNotFoundException {
        return PathHelper.getFile(inputDirectory, filenamePattern);
    }

    public final static Function<String[], CredentialDataElement> toCredentialDataElement = array -> {
        if (array == null || array.length != 2) {
            throw new IllegalArgumentException("Wrong array input format");
        }
        CredentialDataElement cde = new CredentialDataElement();
        cde.setValue1(array[0]);
        cde.setValue2(array[1]);
        return cde;
    };
}
