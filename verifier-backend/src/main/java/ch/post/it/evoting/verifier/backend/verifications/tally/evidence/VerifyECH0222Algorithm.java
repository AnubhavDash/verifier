/*
 * (c) Copyright 2025 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static ch.post.it.evoting.evotinglibraries.xml.XsdConstants.TALLY_COMPONENT_ECH_0222;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.ech.xmlns.ech_0222._3.Delivery;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.evotinglibraries.domain.validations.FailedValidationException;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.channelsecurity.XMLSignatureService;
import ch.post.it.evoting.evotinglibraries.toolbox.OutputToInputStreamConverter;
import ch.post.it.evoting.evotinglibraries.xml.XmlFileRepository;
import ch.post.it.evoting.evotinglibraries.xml.mapper.RawDataDeliveryMapper;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;

@Service
public class VerifyECH0222Algorithm {

	private final XMLSignatureService xmlSignatureService;
	private final XmlFileRepository<Delivery> ech0222XmlFileRepository;

	public VerifyECH0222Algorithm(final XMLSignatureService xmlSignatureService, final XmlFileRepository<Delivery> ech0222XmlFileRepository) {
		this.xmlSignatureService = xmlSignatureService;
		this.ech0222XmlFileRepository = ech0222XmlFileRepository;
	}

	/**
	 * Verifies the correctness of the eCH-0222 file.
	 *
	 * @param input the {@link VerifyECH0222Input} containing the configuration and eCH-0222 files and the decoded votes and write-ins.
	 * @return {@code true} if the eCH-0222 is correct, {@code false} otherwise.
	 * @throws NullPointerException      if any input parameter is null.
	 * @throws FailedValidationException if {@code electionEventId} is invalid.
	 */
	@SuppressWarnings({ "java:S117", "java:S4087" })
	public boolean verifyECH0222(final VerifyECH0222Input input) {
		checkNotNull(input);

		// Input.
		final Configuration configurationXML = input.getCantonConfig();
		final Delivery eCH0222XML = input.getTallyComponentEch0222();
		final ImmutableMap<String, TallyComponentVotesPayload> Map_decodedVotes_Map_writeIns = input.getTallyControlComponentVotesPerAuthorizationName();

		// Operation.
		final Delivery eCH0222XML_prime = RawDataDeliveryMapper.createECH0222(configurationXML, Map_decodedVotes_Map_writeIns);

		final PrivateKey sk = genSecretKey();

		// Remove the extension from the original eCH-0222 XML, before signing it again.
		// Otherwise, a second signature of the whole document (including the already existing signature) would be added.
		eCH0222XML.getRawDataDelivery().setExtension(null);
		final Document D_signed = genXMLSignature(eCH0222XML, sk);
		final Document D_signed_prime = genXMLSignature(eCH0222XML_prime, sk);

		final String d = extractDigest(D_signed);
		final String d_prime = extractDigest(D_signed_prime);

		return d.equals(d_prime);
	}

	/**
	 * Generates the eCH-0222 XML signature.
	 * <p>
	 * This method is wrapping the original genXMLSignature to hide the complexity of the stream generation.
	 *
	 * @param delivery   the delivery containing the eCH-0222 data to be signed.
	 * @param signingKey the private key used for signing the eCH-0222 data.
	 * @return the signed eCH-0222 XML document.
	 */
	private Document genXMLSignature(final Delivery delivery, final PrivateKey signingKey) {
		try (final OutputToInputStreamConverter converter = new OutputToInputStreamConverter();
				final InputStream eCH0222InputStream = converter.convert(
						os -> ech0222XmlFileRepository.write(os, delivery, TALLY_COMPONENT_ECH_0222));
				final OutputToInputStreamConverter converterSignature = new OutputToInputStreamConverter();
				final InputStream signedEch0222Input = converterSignature.convert(
						os -> xmlSignatureService.genXMLSignature(eCH0222InputStream, os, signingKey, "eCH-0222:rawDataDelivery",
								"eCH-0222:extension"))) {
			return getSignedDocument(signedEch0222Input);
		} catch (final IOException e) {
			throw new UncheckedIOException("Could not save tally component eCH-0222 file.", e);
		}
	}

	private Document getSignedDocument(final InputStream signedXmlDocumentStream) {
		checkNotNull(signedXmlDocumentStream);
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		// Disable DTD processing
		try {
			documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		} catch (final ParserConfigurationException e) {
			throw new IllegalStateException("Could not set features for disabling DTD processing", e);
		}
		documentBuilderFactory.setNamespaceAware(true);
		try {
			return documentBuilderFactory.newDocumentBuilder().parse(signedXmlDocumentStream);
		} catch (final SAXException | ParserConfigurationException | IOException e) {
			throw new IllegalArgumentException("Cannot parse signed XML document", e);
		}
	}

	private static String extractDigest(final Document signedDocument) {
		final NodeList nodeList = signedDocument.getElementsByTagNameNS(XMLSignature.XMLNS, "DigestValue");
		if (nodeList.getLength() == 0) {
			throw new IllegalArgumentException("Cannot find DigestValue element");
		}

		return nodeList.item(0).getTextContent();
	}

	private static PrivateKey genSecretKey() {
		try {
			return KeyPairGenerator.getInstance("RSASSA-PSS").genKeyPair().getPrivate();
		} catch (final NoSuchAlgorithmException e) {
			throw new IllegalStateException("Cannot generate key pair for RSASSA-PSS", e);
		}
	}

}
