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
package ch.post.it.evoting.verifier.backend.verifications.tally;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.evotinglibraries.xml.XmlFileRepository;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingdecrypt.Results;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.verifications.authenticity.TestDigitalSignaturesFactory;

public abstract class TallyVerificationTest {

	public static TestDigitalSignaturesFactory signatureFactory;
	protected static Path datasetPath;
	protected static ObjectMapper objectMapper;
	protected static PathService pathService;
	protected static AbstractVerification verification;
	protected static ResultPublisherService resultPublisherServiceMock;
	protected static XmlFileRepository<Delivery> ech0110XmlFileRepository;
	protected static XmlFileRepository<ch.ech.xmlns.ech_0222._1.Delivery> ech0222XmlFileRepository;
	protected static XmlFileRepository<Configuration> configurationXmlFileRepository;
	protected static XmlFileRepository<Results> resultsXmlFileRepository;
	protected static ElectionDataExtractionService electionDataExtractionService;

	@BeforeAll
	static void baseSetUpAll() {
		objectMapper = DomainObjectMapper.getNewInstance();
		pathService = new PathService();
		resultPublisherServiceMock = mock(ResultPublisherService.class);
		datasetPath = Paths.get("").toAbsolutePath().getParent().resolve("datasets").resolve("dataset1-setup-tally");
		signatureFactory = new TestDigitalSignaturesFactory();
		ech0110XmlFileRepository = new XmlFileRepository<>();
		ech0222XmlFileRepository = new XmlFileRepository<>();
		configurationXmlFileRepository = new XmlFileRepository<>();
		resultsXmlFileRepository = new XmlFileRepository<>();
		electionDataExtractionService = new ElectionDataExtractionService(pathService, objectMapper, ech0110XmlFileRepository,
				ech0222XmlFileRepository, configurationXmlFileRepository, resultsXmlFileRepository);
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
		assertEquals(TallyVerificationSuite.BLOCK_NAME, verificationDefinition.getBlock());
		assertNotNull(verificationDefinition.getCategory());
		assertNotNull(verificationDefinition.getName());
		assertNotNull(verificationDefinition.getDescription());
		assertFalse(verificationDefinition.getVerifierEvents().isEmpty());
	}

}
