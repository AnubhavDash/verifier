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
package ch.post.it.evoting.verifier.backend.verifications.setup.completeness;

import java.io.UncheckedIOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.PreSetupEvent;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.VerifyContextCompletenessService;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component
public class VerifySetupCompleteness extends AbstractVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifySetupCompleteness.class);
	private final PathService pathService;
	private final VerifyContextCompletenessService verifyContextCompletenessService;

	protected VerifySetupCompleteness(final PathService pathService,
			final ResultPublisherService resultPublisherService,
			final VerifyContextCompletenessService verifyContextCompletenessService) {
		super(resultPublisherService);
		this.pathService = pathService;
		this.verifyContextCompletenessService = verifyContextCompletenessService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.COMPLETENESS);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "verification101.description"));
		definition.setId("01.01");
		definition.setName("VerifySetupCompleteness");
		definition.addVerifierEvent(PreSetupEvent.TYPE);
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		if (verifyContextCompletenessService.verifyContextCompleteness(inputDirectoryPath) && verifySetupCompleteness(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "verification101.nok.message"));
		}
	}

	private boolean verifySetupCompleteness(final Path inputDirectoryPath) {
		try {
			pathService.buildFromRootPath(StructureKey.SETUP_DIR, inputDirectoryPath);
			pathService.buildFromRootPath(StructureKey.SETUP_VERIFICATION_CARD_SETS_DIR, inputDirectoryPath);
			final ImmutableList<Path> verificationCardSetIds = pathService.buildFromRootPath(StructureKey.SETUP_VERIFICATION_CARD_SET_ID_DIR,
							inputDirectoryPath)
					.getRegexPaths();
			verificationCardSetIds.stream().parallel()
					.forEach(vcs -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_CODE_SHARES, vcs));
			verificationCardSetIds.stream().parallel()
					.forEach(vcs -> pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_VERIFICATION_DATA, vcs));
			return true;
		} catch (final UncheckedIOException | IllegalStateException e) {
			LOGGER.error("Setup completeness failed.", e);
			return false;
		}
	}
}
