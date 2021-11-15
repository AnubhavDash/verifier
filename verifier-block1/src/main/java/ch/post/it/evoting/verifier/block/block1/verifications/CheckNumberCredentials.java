/*
 * Copyright 2021 Post CH Ltd
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
import java.nio.file.Path;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.evoting.xmlns.config._4.Configuration;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.AbstractVerification;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.dto.CredentialDataElement;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationPreconditionException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;

@Component
public class CheckNumberCredentials extends AbstractVerification {

	private final PathService pathService;

	public CheckNumberCredentials(final PathService pathService, final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(1);
		definition.setCategory(Category.COMPLETENESS);
		definition.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification31.description"));
		definition.setId(31);
		definition.setName("checkNumberCredentials()");
		definition.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		definition.addVerificationTrait(VerificationTrait.BLOCK_1);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		// Number of voters.
		final var configPathNode = pathService.buildFromRootPath(StructureKey.CONFIG_ANONYMIZED, inputDirectoryPath);
		final Configuration configuration;
		try {
			configuration = Deserializer.fromXml(configPathNode.getPath(), Configuration.class);
		} catch (IOException | JAXBException | XMLStreamException e) {
			throw new VerificationPreconditionException("Failed to deserialize anonymized configuration.", e);
		}
		final int votersCount = configuration.getRegister().getVoter().size();

		// Number of lines.
		long linesCount = 0;
		final var votingCardIdPathNode = pathService.buildFromRootPath(StructureKey.VOTING_CARD_SETS_ID_DIR, inputDirectoryPath);
		for (final Path path : votingCardIdPathNode.getRegexPaths()) {
			final var credDataPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.CREDENTIAL_DATA, path);
			final Iterable<CredentialDataElement> iterable = Deserializer.fromCsv(credDataPathNode.getRegexPaths(),
					Deserializer.toCredentialDataElement);
			for (final CredentialDataElement ignored : iterable) {
				linesCount++;
			}
		}

		if (votersCount == linesCount) {
			return VerificationResultEvent.success(this, getVerificationDefinition());
		} else {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification31.nok.message",
							String.valueOf(linesCount), String.valueOf(votersCount)));
		}
	}
}
