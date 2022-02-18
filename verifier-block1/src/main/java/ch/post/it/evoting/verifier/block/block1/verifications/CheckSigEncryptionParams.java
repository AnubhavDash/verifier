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
package ch.post.it.evoting.verifier.block.block1.verifications;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.plugin.contract.AbstractVerification;
import ch.post.it.evoting.verifier.plugin.contract.Category;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.plugin.contract.VerificationTrait;
import ch.post.it.evoting.verifier.core.internal.exceptions.JsonMissingNodeException;
import ch.post.it.evoting.verifier.core.internal.tools.CertificateLoader;
import ch.post.it.evoting.verifier.core.internal.tools.SignatureChecker;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathService;
import ch.post.it.evoting.verifier.core.internal.tools.path.RelationType;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerifierEvent;

@Component
public class CheckSigEncryptionParams extends AbstractVerification {

	private final PathService pathService;
	private final CertificateLoader certificateLoader;

	public CheckSigEncryptionParams(final PathService pathService, final CertificateLoader certificateLoader,
			final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
		this.certificateLoader = certificateLoader;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(1);
		definition.setCategory(Category.AUTHENTICITY);
		definition.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification76.description"));
		definition.setId(76);
		definition.setName("checkSigEncryptionParams");
		definition.addVerificationTrait(VerificationTrait.CONFIGURATION);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		final byte[] integrationCertificate = certificateLoader.loadBytes(StructureKey.INTEGRATION, inputDirectoryPath);
		final byte[] ca2Certificate = certificateLoader.loadBytes(StructureKey.CA2, inputDirectoryPath);
		final byte[] ca1Certificate = certificateLoader.loadBytes(StructureKey.CA1, inputDirectoryPath);
		final byte[] integrationCACertificate = certificateLoader.loadBytes(StructureKey.INTEGRATION_CA, inputDirectoryPath);
		final byte[] rootCertificate = certificateLoader.loadBytes(StructureKey.ROOT_CA, inputDirectoryPath);

		final var encryptParamsPathNode = pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);

		final var objectMapper = new ObjectMapper();
		final JsonNode encryptionParamsNode;
		final JsonNode encryptionParamsSignatureNode;
		try {
			encryptionParamsNode = objectMapper.readTree(Files.readString(encryptParamsPathNode.getPath()));
			encryptionParamsSignatureNode = objectMapper.readTree(Files.readString(encryptParamsPathNode.getRelation(RelationType.SIGN)));
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize encryption parameters or its signature.", e);
		}

		final JsonNode signature = encryptionParamsSignatureNode.path("signature");
		if (signature.isMissingNode()) {
			throw new JsonMissingNodeException("The signature is missing from the file!");
		}

		if (!SignatureChecker.verifyJsonSignature(encryptionParamsNode, signature, integrationCertificate,
				new byte[][] { integrationCACertificate, ca1Certificate, ca2Certificate }, rootCertificate)) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification76.nok.message",
							encryptParamsPathNode.getPath().toString()));
		}

		return VerificationResultEvent.success(this, getVerificationDefinition());
	}
}
