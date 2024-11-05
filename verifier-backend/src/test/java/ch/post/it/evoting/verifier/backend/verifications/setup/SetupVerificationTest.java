/*
 * (c) Copyright 2024 Swiss Post Ltd.
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
package ch.post.it.evoting.verifier.backend.verifications.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsfr.json.JsonSurferJackson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ech.xmlns.ech_0222._1.Delivery;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.evotinglibraries.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.evotinglibraries.xml.XmlFileRepository;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.dataextractors.ControlComponentPublicKeysPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.dataextractors.ElectionEventContextPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.dataextractors.EncryptionGroupParametersDataExtractor;
import ch.post.it.evoting.verifier.backend.dataextractors.SetupComponentTallyDataPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.verifications.authenticity.DatasetSignatureFactory;

public abstract class SetupVerificationTest {

	protected static DatasetSignatureFactory datasetSignatureFactory;
	protected static SignatureVerification datasetSignatureVerification;
	protected static AbstractVerification verification;
	protected static Path datasetPath;
	protected static PathService pathService;
	protected static ResultPublisherService resultPublisherServiceMock;
	protected static ObjectMapper objectMapper;
	protected static XmlFileRepository<Delivery> ech0222XmlFileRepository;
	protected static XmlFileRepository<Configuration> configurationXmlFileRepository;
	protected static ElectionDataExtractionService electionDataExtractionService;
	protected static ElectionEventContextPayloadDataExtractor electionEventContextPayloadDataExtractor;
	protected static SetupComponentTallyDataPayloadDataExtractor setupComponentTallyDataPayloadDataExtractor;
	protected static ControlComponentPublicKeysPayloadDataExtractor controlComponentPublicKeysPayloadDataExtractor;
	protected static EncryptionGroupParametersDataExtractor encryptionGroupParametersDataExtractor;

	@BeforeAll
	static void baseSetUpAll() {
		pathService = new PathService();
		resultPublisherServiceMock = mock(ResultPublisherService.class);
		objectMapper = DomainObjectMapper.getNewInstance();
		datasetPath = Paths.get("").toAbsolutePath().getParent().resolve("datasets");
		datasetSignatureFactory = new DatasetSignatureFactory();
		datasetSignatureVerification = datasetSignatureFactory.getSignatureVerification();
		ech0222XmlFileRepository = new XmlFileRepository<>();
		configurationXmlFileRepository = new XmlFileRepository<>();
		electionEventContextPayloadDataExtractor = new ElectionEventContextPayloadDataExtractor(JsonSurferJackson.INSTANCE);
		setupComponentTallyDataPayloadDataExtractor = new SetupComponentTallyDataPayloadDataExtractor(JsonSurferJackson.INSTANCE);
		controlComponentPublicKeysPayloadDataExtractor = new ControlComponentPublicKeysPayloadDataExtractor(JsonSurferJackson.INSTANCE);
		encryptionGroupParametersDataExtractor = new EncryptionGroupParametersDataExtractor(JsonSurferJackson.INSTANCE);
		electionDataExtractionService = new ElectionDataExtractionService(pathService, objectMapper,
				ech0222XmlFileRepository, configurationXmlFileRepository, electionEventContextPayloadDataExtractor,
				controlComponentPublicKeysPayloadDataExtractor, setupComponentTallyDataPayloadDataExtractor);
	}

	@BeforeEach
	void baseSetUp() {
		reset(resultPublisherServiceMock);
	}

	@Test
	public void verificationDefinitionTestOK() {
		assertNotNull(verification);

		final VerificationDefinition verificationDefinition = verification.getVerificationDefinition();
		// Minimum required definition
		assertNotNull(verificationDefinition);
		assertEquals(SetupVerificationSuite.BLOCK_NAME, verificationDefinition.getBlock());
		assertNotNull(verificationDefinition.getCategory());
		assertNotNull(verificationDefinition.getName());
		assertNotNull(verificationDefinition.getDescription());
		assertFalse(verificationDefinition.getVerifierEvents().isEmpty());
	}
}
