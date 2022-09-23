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
package ch.post.it.evoting.verifier.backend.tools;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@Repository
public class XmlFileRepository<T> {

	private static final String XML_EXTENSION = ".xml";
	private static final String XSD_EXTENSION = ".xsd";

	/**
	 * Reads the provided source file, validates it against the provided schema file and returns the object of type {@code clazz} representing the
	 * source file.
	 *
	 * @param sourceFilePath the path to the source file.
	 * @param schemaFilePath the path to the schema file.
	 * @param clazz          the class of the object to be returned.
	 * @return the object of type {@code clazz} representing the source file.
	 * @throws NullPointerException     if any of the inputs is null.
	 * @throws IllegalArgumentException if
	 *                                  <ul>
	 *                                      <li>the source file has not the XML extension or does not exist.</li>
	 *                                      <li>the schema file has not the XSD extension or does not exist.</li>
	 *                                  </ul>
	 */
	protected T read(final String sourceFilePath, final String schemaFilePath, final Class<T> clazz) {
		checkNotNull(sourceFilePath);
		checkArgument(sourceFilePath.toLowerCase().endsWith(XML_EXTENSION),
				"The provided source file path does not target an XML file. [sourceFilePath: %s]", sourceFilePath);
		final File sourceFile = new File(sourceFilePath);
		checkArgument(sourceFile.exists(), "The provided source file does not exist. [sourceFilePath: %s]", sourceFilePath);

		checkNotNull(clazz);

		final JAXBContext jaxbContext = newJaxbContext(clazz);
		final Schema schema = loadSchema(schemaFilePath);
		final Unmarshaller jaxbUnmarshaller = createUnmarshaller(jaxbContext, schema);

		try {
			return clazz.cast(jaxbUnmarshaller.unmarshal(sourceFile));
		} catch (final JAXBException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Writes the provided object in the provided destination path while validating it against the provided schema file.
	 *
	 * @param object              the object to be written.
	 * @param schemaFilePath      the path to the schema file.
	 * @param destinationFilePath the path to the destination file.
	 * @throws NullPointerException     if any of the inputs is null.
	 * @throws IllegalArgumentException if
	 *                                  <ul>
	 *                                      <li>the schema file has not the XSD extension or does not exist.</li>
	 *                                      <li>the destination file has not the XML extension.</li>
	 *                                  </ul>
	 */
	protected void write(final T object, final String schemaFilePath, final String destinationFilePath) {
		checkNotNull(object);

		checkNotNull(destinationFilePath);
		checkArgument(destinationFilePath.toLowerCase().endsWith(XML_EXTENSION),
				"The provided destination file path does not target an XML file. [destinationFilePath: %s]", destinationFilePath);

		final JAXBContext jaxbContext = newJaxbContext(object.getClass());
		final Schema schema = loadSchema(schemaFilePath);
		final Marshaller jaxbMarshaller = createMarshaller(jaxbContext, schema);
		final Document document = newDocument();

		try {
			jaxbMarshaller.marshal(object, document);
		} catch (final JAXBException e) {
			throw new IllegalStateException(e);
		}

		final Transformer transformer = newTransformer();

		try (final FileOutputStream fileOutput = new FileOutputStream(destinationFilePath)) {
			transformer.transform(new DOMSource(document), new StreamResult(fileOutput));
		} catch (final TransformerException e) {
			throw new IllegalStateException("Unable to create XML file. Cause: " + e.getLocalizedMessage(), e);
		} catch (final IOException e) {
			throw new IllegalStateException("Filed to create xml output file. Cause: " + e.getLocalizedMessage(), e);
		}
	}

	private static Unmarshaller createUnmarshaller(final JAXBContext jaxbContext, final Schema schema) {
		final Unmarshaller jaxbUnmarshaller;
		try {
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		} catch (final JAXBException e) {
			throw new IllegalStateException(e);
		}
		jaxbUnmarshaller.setSchema(schema);
		return jaxbUnmarshaller;
	}

	private Schema loadSchema(String xsdLocation) {
		checkNotNull(xsdLocation);
		checkArgument(xsdLocation.toLowerCase().endsWith(XSD_EXTENSION),
				"The provided schema file path does not target an XSD file. [xsdLocation: %s]", xsdLocation);

		final URL schemaUrl = getClass().getClassLoader().getResource(xsdLocation);
		checkNotNull(schemaUrl, "The provided schema file does not exist. [xsdLocation: %s]", xsdLocation);

		try {
			final SchemaFactory schemaFactory = SchemaFactory.newDefaultInstance();
			schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

			return schemaFactory.newSchema(schemaUrl);
		} catch (final SAXException e) {
			throw new IllegalStateException(String.format("Could not create new schema for %s. ", xsdLocation), e);
		}

	}

	private static Transformer newTransformer() {
		final TransformerFactory transformerFactory = TransformerFactory.newDefaultInstance();
		transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

		final Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (final TransformerConfigurationException e) {
			throw new IllegalStateException(e);
		}
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		return transformer;
	}

	private static Marshaller createMarshaller(final JAXBContext jaxbContext, final Schema schema) {
		final Marshaller jaxbMarshaller;
		try {
			jaxbMarshaller = jaxbContext.createMarshaller();
		} catch (final JAXBException e) {
			throw new IllegalStateException(e);
		}
		jaxbMarshaller.setSchema(schema);
		return jaxbMarshaller;
	}

	private static JAXBContext newJaxbContext(final Class<?> clazz) {
		final JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(clazz);
		} catch (final JAXBException e) {
			throw new IllegalStateException(e);
		}
		return jaxbContext;
	}

	private static Document newDocument() {
		final Document document;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (final ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
		return document;
	}

}
