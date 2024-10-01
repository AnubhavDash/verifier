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
package ch.post.it.evoting.verifier.backend.tools;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.UncheckedIOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.ControlComponentNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;

@Service
public class VerifyContextCompletenessService {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyContextCompletenessService.class);
	private final PathService pathService;

	public VerifyContextCompletenessService(final PathService pathService) {
		this.pathService = pathService;
	}

	/**
	 * Verifies the completeness of the context dataset.
	 *
	 * @param inputDirectoryPath the input directory path. Must be not null.
	 * @return true if the verification is successful, false otherwise.
	 */
	public boolean verifyContextCompleteness(final Path inputDirectoryPath) {
		checkNotNull(inputDirectoryPath);

		try {
			pathService.buildFromRootPath(StructureKey.CONTEXT_DIR, inputDirectoryPath);
			pathService.buildFromRootPath(StructureKey.CONFIGURATION_ANONYMIZED, inputDirectoryPath);
			pathService.buildFromRootPath(StructureKey.ELECTION_EVENT_CONTEXT, inputDirectoryPath);
			pathService.buildFromRootPath(StructureKey.SETUP_COMPONENT_PUBLIC_KEYS, inputDirectoryPath);
			checkState(pathService.buildFromRootPath(StructureKey.CONTROL_COMPONENT_PUBLIC_KEYS, inputDirectoryPath).getRegexPaths().size()
					== ControlComponentNode.ids().size());
			pathService.buildFromRootPath(StructureKey.CONTEXT_VERIFICATION_CARD_SETS_DIR, inputDirectoryPath);
			final ImmutableList<Path> verificationCardSetIds = pathService.buildFromRootPath(StructureKey.CONTEXT_VERIFICATION_CARD_SET_ID_DIR,
							inputDirectoryPath)
					.getRegexPaths();
			verificationCardSetIds.stream().parallel()
					.forEach(vcs -> pathService.buildFromDynamicAncestorPath(StructureKey.SETUP_COMPONENT_TALLY_DATA, vcs));
			return true;
		} catch (final UncheckedIOException | IllegalStateException e) {
			LOGGER.error("Context completeness failed.", e);
			return false;
		}
	}
}
