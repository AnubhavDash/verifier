/*
 * (c) Copyright 2025 Swiss Post Ltd.
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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.electoralmodel.PrimesMappingTableAlgorithms;
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
	private final PrimesMappingTableAlgorithms primesMappingTableAlgorithms;

	protected VerifyPlaintextsConsistency(final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService extractionService,
			final PrimesMappingTableAlgorithms primesMappingTableAlgorithms) {
		super(resultPublisherService);
		this.extractionService = extractionService;
		this.primesMappingTableAlgorithms = primesMappingTableAlgorithms;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME,
				"tally.verification803.description"));
		definition.setId("08.03");
		definition.setName("VerifyPlaintextsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final ImmutableList<VerificationCardSetContext> verificationCardSetContexts = extractionService.getElectionEventContext(inputDirectoryPath)
				.verificationCardSetContexts();
		final ImmutableList<Plaintexts> plaintexts = verificationCardSetContexts.stream()
				.parallel()
				.map(vcsContext -> {
					final String ballotBoxId = vcsContext.getBallotBoxId();
					final int numberWriteInsPlusOne = primesMappingTableAlgorithms.getDelta(vcsContext.getPrimesMappingTable());
					final TallyComponentShufflePayload tallyComponentShufflePayload = extractionService.getTallyComponentShufflePayload(
							inputDirectoryPath, ballotBoxId);
					return new Plaintexts(tallyComponentShufflePayload, numberWriteInsPlusOne);
				})
				.collect(toImmutableList());
		if (plaintextsConsistent(plaintexts)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification803.nok.message"));
		}
	}

	private boolean plaintextsConsistent(final ImmutableList<Plaintexts> plaintexts) {
		return plaintexts.stream()
				.parallel()
				.map(information -> information.tallyComponentShufflePayload.getVerifiablePlaintextDecryption()
						.getDecryptedVotes().getElementSize() == information.numberWriteInsPlusOne)
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	private record Plaintexts(TallyComponentShufflePayload tallyComponentShufflePayload, int numberWriteInsPlusOne) {
	}
}
