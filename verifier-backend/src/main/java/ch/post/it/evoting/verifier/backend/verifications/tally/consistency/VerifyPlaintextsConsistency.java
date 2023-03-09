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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;

@Component
public class VerifyPlaintextsConsistency extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;

	protected VerifyPlaintextsConsistency(final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService extractionService) {
		super(resultPublisherService);
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME,
				"tally.verification302.description"));
		definition.setId(302);
		definition.setName("VerifyPlaintextsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final List<VerificationCardSetContext> verificationCardSetContexts = extractionService.getElectionEventContextPayload(inputDirectoryPath)
				.getElectionEventContext().verificationCardSetContexts();
		final List<Plaintexts> plaintexts = verificationCardSetContexts.stream()
				.parallel()
				.map(vcsContext -> {
					final String ballotBoxId = vcsContext.ballotBoxId();
					final int numberWriteInsPlusOne = vcsContext.numberOfWriteInFields() + 1;
					final TallyComponentShufflePayload tallyComponentShufflePayload = extractionService.getTallyComponentShufflePayload(
							inputDirectoryPath, ballotBoxId);
					return new Plaintexts(tallyComponentShufflePayload, numberWriteInsPlusOne);
				})
				.toList();
		if (plaintextsConsistent(plaintexts)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification302.nok.message"));
		}
	}

	private record Plaintexts(TallyComponentShufflePayload tallyComponentShufflePayload, int numberWriteInsPlusOne){}

	private boolean plaintextsConsistent(final List<Plaintexts> plaintexts) {
		return plaintexts.stream()
				.parallel()
				.map(information -> information.tallyComponentShufflePayload.getVerifiablePlaintextDecryption()
						.getDecryptedVotes().getElementSize() == information.numberWriteInsPlusOne)
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}
}
