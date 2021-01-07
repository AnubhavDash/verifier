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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.post.it.evoting.verifier.common.block.dto.CredentialDataElement;
import ch.post.it.evoting.verifier.common.block.dto.revised.AuthenticationToken;
import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.common.block.dto.revised.PlaintextEqualityProof;
import ch.post.it.evoting.verifier.common.block.dto.revised.PreImageProof;
import ch.post.it.evoting.verifier.common.block.dto.revised.PublicKey;
import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.Ciphertext;
import ch.post.it.evoting.verifier.common.block.dto.revised.serialization.AuthenticationTokenDeserializer;
import ch.post.it.evoting.verifier.common.block.dto.revised.serialization.Base64BigIntegerDeserializer;
import ch.post.it.evoting.verifier.common.block.dto.revised.serialization.CiphertextDeserializer;
import ch.post.it.evoting.verifier.common.block.dto.revised.serialization.ElectionEventDeserializer;
import ch.post.it.evoting.verifier.common.block.dto.revised.serialization.ListDeserializer;
import ch.post.it.evoting.verifier.common.block.dto.revised.serialization.LocalDateTimeDeserializer;
import ch.post.it.evoting.verifier.common.block.dto.revised.serialization.PlaintextEqualityProofDeserializer;
import ch.post.it.evoting.verifier.common.block.dto.revised.serialization.PreImageProofDeserializer;
import ch.post.it.evoting.verifier.common.block.dto.revised.serialization.PublicKeyDeserializer;
import ch.post.it.evoting.verifier.common.block.dto.revised.serialization.UuidDeserializer;
import ch.post.it.evoting.verifier.common.block.dto.revised.serialization.X509Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.path.PathHelper;

public final class Deserializer {

	public static final Function<String[], CredentialDataElement> toCredentialDataElement = array -> {
		if (array == null || array.length != 2) {
			throw new IllegalArgumentException("Wrong array input format");
		}

		return new CredentialDataElement(array[0], array[1]);
	};

	private Deserializer() {
		//private constructor, use static
	}

	public static ObjectMapper initObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		SimpleModule typesModule = new SimpleModule();
		typesModule.addDeserializer(BigInteger.class, new Base64BigIntegerDeserializer());
		typesModule.addDeserializer(UUID.class, new UuidDeserializer());
		typesModule.addDeserializer(List.class, new ListDeserializer());
		typesModule.addDeserializer(PublicKey.class, new PublicKeyDeserializer());
		typesModule.addDeserializer(X509Certificate.class, new X509Deserializer());
		typesModule.addDeserializer(AuthenticationToken.class, new AuthenticationTokenDeserializer());
		typesModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
		typesModule.addDeserializer(PreImageProof.class, new PreImageProofDeserializer());
		typesModule.addDeserializer(PlaintextEqualityProof.class, new PlaintextEqualityProofDeserializer());
		typesModule.addDeserializer(ElectionEvent.class, new ElectionEventDeserializer());
		typesModule.addDeserializer(Ciphertext.class, new CiphertextDeserializer());
		mapper.registerModule(typesModule);
		return mapper;
	}

	public static <T> Stream<T> fromLines(File inputFile, String filenamePattern, Function<String, T> mapper) throws IOException {
		return Files.lines(getFile(inputFile, filenamePattern).toPath()).map(mapper);
	}

	public static <T> T fromJson(File inputDirectory, String filenamePattern, Class<T> targetClazz) throws IOException {
		ObjectMapper jsonMapper = initObjectMapper();
		return jsonMapper.readValue(getFile(inputDirectory, filenamePattern), targetClazz);
	}

	public static <T> T fromJson(Path filePath, Class<T> targetClazz) throws IOException {
		ObjectMapper jsonMapper = initObjectMapper();
		return jsonMapper.readValue(Files.newInputStream(filePath), targetClazz);
	}

	public static <T> T fromJson(byte[] content, Class<T> targetClazz) throws IOException {
		ObjectMapper jsonMapper = initObjectMapper();
		return jsonMapper.readValue(new String(content, StandardCharsets.UTF_8), targetClazz);
	}

	public static <T> T fromXml(File inputDirectory, String filenamePattern, Class<T> targetClazz)
			throws IOException, JAXBException, XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newFactory();
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(getFile(inputDirectory, filenamePattern)));
		return (T) JAXBContext.newInstance(targetClazz).createUnmarshaller().unmarshal(reader);
	}

	public static <T> T fromXml(Path filePath, Class<T> targetClazz) throws IOException, JAXBException, XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newFactory();
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		XMLStreamReader reader = factory.createXMLStreamReader(Files.newInputStream(filePath));
		return (T) JAXBContext.newInstance(targetClazz).createUnmarshaller().unmarshal(reader);
	}

	public static <T> Iterable<T> fromCsv(File inputDirectory, String filenamePattern, Function<String[], T> mapper) throws IOException {
		return new CsvReader<>(getFile(inputDirectory, filenamePattern).toString(), StandardCharsets.UTF_8, false, ",", mapper).process();
	}

	public static <T> Iterable<T> fromCsv(File inputDirectory, String filenamePattern, String separator, Function<String[], T> mapper)
			throws IOException {
		return new CsvReader<>(getFile(inputDirectory, filenamePattern).toString(), StandardCharsets.UTF_8, false, separator, mapper).process();
	}

	public static <T> Iterable<T> fromCsv(Path filePath, Function<String[], T> mapper) {
		return new CsvReader<>(filePath, StandardCharsets.UTF_8, false, ",", mapper).process();
	}

	public static <T> Iterable<T> fromCsv(final List<Path> filePaths, final Function<String[], T> mapper) {
		return new CsvReader<>(filePaths, StandardCharsets.UTF_8, false, ",", mapper).process();
	}

	public static <T> Iterable<T> fromCsv(Path filePath, String separator, Function<String[], T> mapper) {
		return new CsvReader<>(filePath, StandardCharsets.UTF_8, false, separator, mapper).process();
	}

	private static File getFile(File inputDirectory, String filenamePattern) throws FileNotFoundException {
		return PathHelper.getFile(inputDirectory, filenamePattern);
	}
}
