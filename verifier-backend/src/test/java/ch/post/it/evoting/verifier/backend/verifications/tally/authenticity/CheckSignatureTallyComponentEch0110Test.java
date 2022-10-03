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

package ch.post.it.evoting.verifier.backend.verifications.tally.authenticity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.ech.xmlns.ech_0155._4.ExtensionType;
import ch.post.it.evoting.cryptoprimitives.domain.signature.Alias;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.verifier.backend.hashable.HashableEch0110Factory;
import ch.post.it.evoting.verifier.backend.tools.XmlFileRepository;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;
import ch.post.it.evoting.verifier.protocol.domain.ChannelSecurityContextData;

class CheckSignatureTallyComponentEch0110Test extends TallyVerificationTest {

	private final XmlFileRepository<Delivery> xmlFileRepository = new XmlFileRepository<>();
	private final String schemaResourceName = "xsd/eCH-0110-4-0.xsd";

	@BeforeEach
	void setUpAll() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
		final SignatureVerification testSignatureVerification = signatureFactory.getTestSignatureVerification();
		verification = new CheckSignatureTallyComponentEch0110(applicationEventPublisherMock, electionDataExtractionService,
				testSignatureVerification);
	}

	@Test
	void testOK(
			@TempDir
			final Path tempDirectory) throws SignatureException, JsonProcessingException {
		Delivery delivery = electionDataExtractionService.getTallyComponentEch0110(datasetPath);

		final byte[] signature = generateSignature(delivery);
		final String signatureWithQuotes = new ObjectMapper().writeValueAsString(signature);
		delivery.getResultDelivery().setExtension(new ExtensionType()
				.withAny(List.of(new JAXBElement<>(new QName("signature"), String.class,
						signatureWithQuotes.substring(1, signatureWithQuotes.length() - 1)))));

		final Path eCH0110 = tempDirectory.resolve("eCH-0110.xml");

		xmlFileRepository.write(delivery, schemaResourceName, eCH0110);
		delivery = xmlFileRepository.read(eCH0110, schemaResourceName, Delivery.class);

		assertTrue(((CheckSignatureTallyComponentEch0110) verification).verifySignature(delivery));
	}

	@Test
	void testNOK(
			@TempDir
			final Path tempDirectory) throws SignatureException, JsonProcessingException {
		Delivery delivery = electionDataExtractionService.getTallyComponentEch0110(datasetPath);

		final byte[] signature = generateSignature(delivery);
		final String signatureWithQuotes = new ObjectMapper().writeValueAsString(signature);
		delivery.getResultDelivery().setExtension(new ExtensionType()
				.withAny(List.of(new JAXBElement<>(new QName("signature"), String.class,
						signatureWithQuotes.substring(1, signatureWithQuotes.length() - 1)))));

		delivery.getDeliveryHeader().setSenderId("");

		final Path eCH0110 = tempDirectory.resolve("eCH-0110.xml");

		xmlFileRepository.write(delivery, schemaResourceName, eCH0110);
		delivery = xmlFileRepository.read(eCH0110, schemaResourceName, Delivery.class);

		assertFalse(((CheckSignatureTallyComponentEch0110) verification).verifySignature(delivery));
	}

	private byte[] generateSignature(final Delivery delivery) throws SignatureException {
		final Hashable hash = HashableEch0110Factory.fromDelivery(delivery);
		final Hashable additionalContextData = ChannelSecurityContextData.tallyComponentEch0110();

		return signatureFactory.getTestSignatureGeneration(Alias.SDM_TALLY).genSignature(hash, additionalContextData);
	}
}