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
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.EncryptedVerifiableVote;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

@Component
public class VerifyCiphertextsConsistency extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;

	public VerifyCiphertextsConsistency(final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService extractionService) {
		super(resultPublisherService);
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification301.description"));
		definition.setId(301);
		definition.setName("VerifyCiphertextsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final List<VerificationCardSetContext> verificationCardSetContexts = extractionService.getElectionEventContextPayload(inputDirectoryPath)
				.getElectionEventContext().verificationCardSetContexts();
		final List<Ciphertexts> ciphertexts = verificationCardSetContexts.stream()
				.map(vcsContext -> {
					final String ballotBoxId = vcsContext.ballotBoxId();
					final int numberWriteInsPlusOne = vcsContext.numberOfWriteInFields() + 1;
					final List<ControlComponentBallotBoxPayload> ballotBoxPayloads = extractionService.getControlComponentBallotBoxPayloadsOrderedByNodeId(
							inputDirectoryPath, ballotBoxId);
					final List<ControlComponentShufflePayload> shufflePayloads = extractionService.getControlComponentShufflePayloadsOrderedByNodeId(
							inputDirectoryPath, ballotBoxId);
					final TallyComponentShufflePayload tallyComponentShufflePayload = extractionService.getTallyComponentShufflePayload(
							inputDirectoryPath, ballotBoxId);
					return new Ciphertexts(ballotBoxPayloads, shufflePayloads, tallyComponentShufflePayload, numberWriteInsPlusOne);
				})
				.toList();
		if (ciphertextsConsistent(ciphertexts)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification301.nok.message"));
		}
	}

	private boolean ciphertextsConsistent(final List<Ciphertexts> ciphertexts) {
		return ciphertexts.stream()
				.map(information -> {
					final int numberWriteInsPlusOne = information.numberWriteInsPlusOne;
					final boolean ballotBoxPayloadsCiphertextsConsistent = information.ballotBoxPayloads.stream()
							.flatMap(payload -> payload.getConfirmedEncryptedVotes().stream())
							.map(EncryptedVerifiableVote::encryptedVote)
							.map(ElGamalMultiRecipientCiphertext::size)
							.filter(ciphertextSize -> ciphertextSize != numberWriteInsPlusOne)
							.toList().isEmpty();
					final boolean shufflePayloadsCiphertextsConsistent = information.shufflePayloads.stream()
							.flatMap(payload -> payload.getVerifiableDecryptions().getCiphertexts().stream())
							.map(ElGamalMultiRecipientCiphertext::size)
							.filter(ciphertextSize -> ciphertextSize != numberWriteInsPlusOne)
							.toList().isEmpty();
					final boolean tallyShufflePayloadCiphertextsConsistent = information.tallyComponentShufflePayload.getVerifiableShuffle()
							.shuffledCiphertexts().getElementSize() == numberWriteInsPlusOne;
					return ballotBoxPayloadsCiphertextsConsistent && shufflePayloadsCiphertextsConsistent && tallyShufflePayloadCiphertextsConsistent;
				})
				.reduce(Boolean::logicalAnd).orElse(Boolean.FALSE);
	}

	private record Ciphertexts(List<ControlComponentBallotBoxPayload> ballotBoxPayloads, List<ControlComponentShufflePayload> shufflePayloads,
							   TallyComponentShufflePayload tallyComponentShufflePayload, int numberWriteInsPlusOne) {
	}
}
