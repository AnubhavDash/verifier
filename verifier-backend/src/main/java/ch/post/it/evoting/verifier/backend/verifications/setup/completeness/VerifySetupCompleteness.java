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
package ch.post.it.evoting.verifier.backend.verifications.setup.completeness;

import static ch.post.it.evoting.cryptoprimitives.domain.ControlComponentConstants.NODE_IDS;
import static com.google.common.base.Preconditions.checkState;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.PreSetupEvent;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component
public class VerifySetupCompleteness extends AbstractVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifySetupCompleteness.class);
	private final PathService pathService;

	protected VerifySetupCompleteness(final PathService pathService,
			final ResultPublisherService resultPublisherService) {
		super(resultPublisherService);
		this.pathService = pathService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.COMPLETENESS);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "verification100.description"));
		definition.setId("1.01");
		definition.setName("VerifySetupCompleteness");
		definition.addVerifierEvent(PreSetupEvent.TYPE);
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		if (verifySetupCompleteness(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "verification100.nok.message"));
		}
	}

	@VisibleForTesting
	private boolean verifySetupCompleteness(final Path inputDirectoryPath) {
		try {
			pathService.buildFromRootPath(StructureKey.SETUP_DIR, inputDirectoryPath);
			pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);
			pathService.buildFromRootPath(StructureKey.CONFIGURATION_ANONYMIZED, inputDirectoryPath);
			pathService.buildFromRootPath(StructureKey.ELECTION_EVENT_CONTEXT, inputDirectoryPath);
			pathService.buildFromRootPath(StructureKey.SETUP_COMPONENT_PUBLIC_KEYS, inputDirectoryPath);
			checkState(pathService.buildFromRootPath(StructureKey.CONTROL_COMPONENT_PUBLIC_KEYS, inputDirectoryPath).getRegexPaths().size()
					== NODE_IDS.size());
			pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SETS_DIR, inputDirectoryPath);
			final List<Path> verificationCardSetIds = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath)
					.getRegexPaths();
			verificationCardSetIds.stream().parallel().forEach(vcs -> pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_CODE_SHARES, vcs));
			verificationCardSetIds.stream().parallel().forEach(vcs -> pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_TALLY_DATA, vcs));
			verificationCardSetIds.stream().parallel().forEach(vcs -> pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_VERIFICATION_DATA, vcs));
			return true;
		} catch (final UncheckedIOException | IllegalStateException e) {
			LOGGER.error("Setup completeness failed.", e);
			return false;
		}
	}
}
