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
package ch.post.it.evoting.verifier.backend.verifications.tally.completeness;

import static com.google.common.base.Preconditions.checkState;

import java.io.UncheckedIOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.ControlComponentNode;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.PreTallyEvent;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.VerifyContextCompletenessService;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;

@Component
public class VerifyTallyCompleteness extends AbstractVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyTallyCompleteness.class);
	private final PathService pathService;
	private final VerifyContextCompletenessService verifyContextCompletenessService;

	protected VerifyTallyCompleteness(final PathService pathService,
			final ResultPublisherService resultPublisherService,
			final VerifyContextCompletenessService verifyContextCompletenessService) {
		super(resultPublisherService);
		this.pathService = pathService;
		this.verifyContextCompletenessService = verifyContextCompletenessService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.COMPLETENESS);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "verification101.description"));
		definition.setId("06.01");
		definition.setName("VerifyTallyCompleteness");
		definition.addVerifierEvent(PreTallyEvent.TYPE);
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		if (verifyContextCompletenessService.verifyContextCompleteness(inputDirectoryPath) && verifyTallyCompleteness(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "verification101.nok.message"));
		}
	}

	private boolean verifyTallyCompleteness(final Path inputDirectoryPath) {
		try {
			pathService.buildFromRootPath(StructureKey.TALLY_COMPONENT_ECH0222, inputDirectoryPath);
			pathService.buildFromRootPath(StructureKey.BALLOT_BOXES_DIR, inputDirectoryPath);
			final ImmutableList<Path> ballotBoxIds = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath)
					.getRegexPaths();
			ballotBoxIds.forEach(bb -> checkState(
					pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_BALLOT_BOX, bb).getRegexPaths().size()
							== ControlComponentNode.ids().size()));
			ballotBoxIds.forEach(bb -> checkState(
					pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_SHUFFLE, bb).getRegexPaths().size()
							== ControlComponentNode.ids().size()));
			ballotBoxIds.forEach(bb -> pathService.buildFromDynamicAncestorPath(StructureKey.TALLY_COMPONENT_SHUFFLE, bb));
			ballotBoxIds.forEach(bb -> pathService.buildFromDynamicAncestorPath(StructureKey.TALLY_COMPONENT_VOTES, bb));
			return true;
		} catch (final UncheckedIOException | IllegalStateException e) {
			LOGGER.error("Tally completeness failed.", e);
			return false;
		}
	}
}
