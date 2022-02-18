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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.post.it.evoting.verifier.core.internal.dto.CredentialDataElement;
import ch.post.it.evoting.verifier.core.internal.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.core.internal.dto.revised.PublicKey;
import ch.post.it.evoting.verifier.core.internal.dto.revised.serialization.Base64BigIntegerDeserializer;
import ch.post.it.evoting.verifier.core.internal.dto.revised.serialization.ElectionEventDeserializer;
import ch.post.it.evoting.verifier.core.internal.dto.revised.serialization.ListDeserializer;
import ch.post.it.evoting.verifier.core.internal.dto.revised.serialization.LocalDateTimeDeserializer;
import ch.post.it.evoting.verifier.core.internal.dto.revised.serialization.PublicKeyDeserializer;
import ch.post.it.evoting.verifier.core.internal.dto.revised.serialization.UuidDeserializer;
import ch.post.it.evoting.verifier.core.internal.dto.revised.serialization.X509Deserializer;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathHelper;

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
		var mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		var simpleModule = new SimpleModule();
		simpleModule.addDeserializer(BigInteger.class, new Base64BigIntegerDeserializer());
		simpleModule.addDeserializer(UUID.class, new UuidDeserializer());
		simpleModule.addDeserializer(List.class, new ListDeserializer());
		simpleModule.addDeserializer(PublicKey.class, new PublicKeyDeserializer());
		simpleModule.addDeserializer(X509Certificate.class, new X509Deserializer());
		simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
		simpleModule.addDeserializer(ElectionEvent.class, new ElectionEventDeserializer());
		mapper.registerModule(simpleModule);
		return mapper;
	}

	public static <T> Stream<T> fromLines(File inputFile, String filenamePattern, Function<String, T> mapper) throws IOException {
		return Files.lines(getFile(inputFile, filenamePattern).toPath()).map(mapper);
	}

	public static <T> T fromJson(File inputDirectory, String filenamePattern, Class<T> targetClazz) throws IOException {
		var jsonMapper = initObjectMapper();
		return jsonMapper.readValue(getFile(inputDirectory, filenamePattern), targetClazz);
	}

	public static <T> T fromJson(Path filePath, Class<T> targetClazz) throws IOException {
		var jsonMapper = initObjectMapper();
		return jsonMapper.readValue(Files.newInputStream(filePath), targetClazz);
	}

	public static <T> T fromJson(byte[] content, Class<T> targetClazz) throws IOException {
		var jsonMapper = initObjectMapper();
		return jsonMapper.readValue(new String(content, StandardCharsets.UTF_8), targetClazz);
	}

	public static <T> T fromXml(File inputDirectory, String filenamePattern, Class<T> targetClazz)
			throws IOException, JAXBException, XMLStreamException {
		var factory = XMLInputFactory.newFactory();
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		var reader = factory.createXMLStreamReader(new FileInputStream(getFile(inputDirectory, filenamePattern)));
		return (T) JAXBContext.newInstance(targetClazz).createUnmarshaller().unmarshal(reader);
	}

	public static <T> T fromXml(Path filePath, Class<T> targetClazz) throws IOException, JAXBException, XMLStreamException {
		var factory = XMLInputFactory.newFactory();
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		var reader = factory.createXMLStreamReader(Files.newInputStream(filePath));
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
