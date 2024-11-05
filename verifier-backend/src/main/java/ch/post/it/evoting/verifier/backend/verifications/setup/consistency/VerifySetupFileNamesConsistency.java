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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.dataextractors.ControlComponentPublicKeysPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component
public class VerifySetupFileNamesConsistency extends AbstractVerification {

	private final PathService pathService;

	private final ControlComponentPublicKeysPayloadDataExtractor controlComponentPublicKeysPayloadDataExtractor;

	protected VerifySetupFileNamesConsistency(
			final ResultPublisherService resultPublisherService,
			final PathService pathService,
			final ControlComponentPublicKeysPayloadDataExtractor controlComponentPublicKeysPayloadDataExtractor) {
		super(resultPublisherService);
		this.pathService = pathService;
		this.controlComponentPublicKeysPayloadDataExtractor = controlComponentPublicKeysPayloadDataExtractor;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification302.description"));
		definition.setId("03.02");
		definition.setName("VerifySetupFileNamesConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final boolean fileNamesConsistent = verifyControlComponentPublicKeyFileNameConsistency(inputDirectoryPath);

		if (fileNamesConsistent) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification302.nok.message"));
		}
	}

	private boolean verifyControlComponentPublicKeyFileNameConsistency(final Path inputDirectoryPath) {
		final PathNode controlComponentPublicKeyNodes = pathService.buildFromRootPath(StructureKey.CONTROL_COMPONENT_PUBLIC_KEYS, inputDirectoryPath);

		return controlComponentPublicKeyNodes.getRegexPaths().stream()
				.parallel()
				.map(path -> {
					final String fileName = path.getFileName().toString();
					final String nodeIdGroup = pathService.getRegexGroup(StructureKey.CONTROL_COMPONENT_PUBLIC_KEYS, fileName, 1);
					final int fileNodeId = Integer.parseInt(nodeIdGroup);

					final int payloadNodeId = controlComponentPublicKeysPayloadDataExtractor.load(path).nodeId();

					return fileNodeId == payloadNodeId;
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}
}
