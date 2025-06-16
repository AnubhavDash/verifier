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
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.evotinglibraries.domain.common.EncryptedVerifiableVote;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;
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
public class VerifyCiphertextsConsistency extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;
	private final PrimesMappingTableAlgorithms primesMappingTableAlgorithms;

	public VerifyCiphertextsConsistency(final ResultPublisherService resultPublisherService,
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
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification809.description"));
		definition.setId("08.09");
		definition.setName("VerifyCiphertextsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		if (verifyCiphertextsConsistency(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification809.nok.message"));
		}
	}

	private boolean verifyCiphertextsConsistency(final Path inputDirectoryPath) {

		// Input.
		final ImmutableList<VerificationCardSetContext> verificationCardSetContexts = extractionService.getElectionEventContext(inputDirectoryPath)
				.verificationCardSetContexts();
		final ImmutableList<Ciphertexts> ciphertexts = verificationCardSetContexts.stream()
				.parallel()
				.map(verificationCardSetContext -> {
					final String ballotBoxId = verificationCardSetContext.getBallotBoxId();

					final int numberWriteInsPlusOne = primesMappingTableAlgorithms.getDelta(verificationCardSetContext.getPrimesMappingTable());

					final ImmutableList<ControlComponentBallotBoxPayload> ballotBoxPayloads = extractionService.getControlComponentBallotBoxPayloadsOrderedByNodeId(
									inputDirectoryPath, ballotBoxId)
							.collect(toImmutableList());

					final ImmutableList<ControlComponentShufflePayload> shufflePayloads = extractionService.getControlComponentShufflePayloadsOrderedByNodeId(
									inputDirectoryPath, ballotBoxId)
							.collect(toImmutableList());

					final TallyComponentShufflePayload tallyComponentShufflePayload = extractionService.getTallyComponentShufflePayload(
							inputDirectoryPath, ballotBoxId);

					return new Ciphertexts(ballotBoxPayloads, shufflePayloads, tallyComponentShufflePayload, numberWriteInsPlusOne);
				})
				.collect(toImmutableList());

		// Operation.
		return ciphertexts.stream()
				.parallel()
				.map(information -> {
					final int numberOfAllowedWriteInsPlusOne = information.numberOfAllowedWriteInsPlusOne;

					final boolean isControlComponentBallotBoxCiphertextsConsistent = information.controlComponentBallotBox.stream()
							.parallel()
							.flatMap(payload -> payload.getConfirmedEncryptedVotes().stream())
							.map(EncryptedVerifiableVote::encryptedVote)
							.map(ElGamalMultiRecipientCiphertext::size)
							.filter(ciphertextSize -> ciphertextSize != numberOfAllowedWriteInsPlusOne)
							.collect(toImmutableList()).isEmpty();

					final boolean isOnlineControlComponentShuffleCiphertextsConsistent = information.onlineControlComponentShuffle.stream()
							.parallel()
							.flatMap(payload -> payload.getVerifiableDecryptions().getCiphertexts().stream())
							.map(ElGamalMultiRecipientCiphertext::size)
							.filter(ciphertextSize -> ciphertextSize != numberOfAllowedWriteInsPlusOne)
							.collect(toImmutableList()).isEmpty();

					final boolean isTallyControlShuffleCiphertextsConsistent = information.tallyControlComponentShuffle.getVerifiableShuffle()
							.shuffledCiphertexts().getElementSize() == numberOfAllowedWriteInsPlusOne;

					return isControlComponentBallotBoxCiphertextsConsistent &&
							isOnlineControlComponentShuffleCiphertextsConsistent &&
							isTallyControlShuffleCiphertextsConsistent;
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	private record Ciphertexts(ImmutableList<ControlComponentBallotBoxPayload> controlComponentBallotBox,
							   ImmutableList<ControlComponentShufflePayload> onlineControlComponentShuffle,
							   TallyComponentShufflePayload tallyControlComponentShuffle, int numberOfAllowedWriteInsPlusOne) {
	}
}
