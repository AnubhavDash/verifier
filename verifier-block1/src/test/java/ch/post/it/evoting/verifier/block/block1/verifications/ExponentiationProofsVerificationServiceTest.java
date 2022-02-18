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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ReturnCodeGenerationRequestPayload;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ReturnCodeGenerationResponsePayload;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathNode;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathService;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;

class ExponentiationProofsVerificationServiceTest {

	@ParameterizedTest
	@ValueSource(strings = {
			"/ExponentiationProofsVerificationService/chunking-incorrect-count/",
			"/ExponentiationProofsVerificationService/chunking-invalid-ids/",
	})
	void invalidChunkingScenarios(String path) throws URISyntaxException {
		final var mapper = DomainObjectMapper.getNewInstance();
		final var pathService = new PathService();

		ElectionDataExtractionService electionDataExtractionService = new ElectionDataExtractionService(pathService, mapper);
		ExponentiationProofsVerificationService exponentiationProofsVerificationService = new ExponentiationProofsVerificationService(
				electionDataExtractionService, pathService);

		final Path inputDirectory = Paths.get(getClass().getResource(path).toURI());
		final PathNode verificationCardSetIDPaths = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectory);

		Path verificationCardSetIDPath = verificationCardSetIDPaths.getPath();
		List<ReturnCodeGenerationRequestPayload> allReturnCodeGenerationRequestPayloads = electionDataExtractionService.deserializeReturnCodeGenerationRequestPayload(
				verificationCardSetIDPath);
		List<List<ReturnCodeGenerationResponsePayload>> allControlComponentContributions = electionDataExtractionService.deserializeControlComponentContributions(
				verificationCardSetIDPath);

		assertThrows(java.lang.IllegalStateException.class,
				() -> exponentiationProofsVerificationService.assembleRequestResponseChunks(allReturnCodeGenerationRequestPayloads, allControlComponentContributions));

	}
}