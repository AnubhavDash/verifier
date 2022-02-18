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
package ch.post.it.evoting.verifier.block.block4.verifications;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.GroupVector;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetFinalPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetShufflePayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.ZqElement;
import ch.post.it.evoting.cryptoprimitives.math.ZqGroup;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.DecryptionProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.VerifiableDecryptions;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProof;
import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.plugin.contract.AbstractVerification;
import ch.post.it.evoting.verifier.plugin.contract.Category;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.plugin.contract.VerificationTrait;
import ch.post.it.evoting.verifier.core.internal.dto.revised.BallotBox;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.core.internal.tools.TypeConverter;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathService;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerifierEvent;

@Component
public class VerifyOfflineDecryptionProofs extends AbstractVerification {

	private final PathService pathService;
	private final ZeroKnowledgeProof zeroKnowledgeProof;
	private final ElectionDataExtractionService extractionService;

	@Autowired
	VerifyOfflineDecryptionProofs(final PathService pathService, final ZeroKnowledgeProof zeroKnowledgeProof,
			final ElectionDataExtractionService extractionService, final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
		this.zeroKnowledgeProof = zeroKnowledgeProof;
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(4);
		definition.setCategory(Category.EVIDENCE);
		definition.setId(12);
		definition.setName("verifyOfflineDecryptionProofsBlock4");
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification12.description"));
		definition.addVerificationTrait(VerificationTrait.FINAL_DECRYPTION);

		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		final boolean bbDecryptVerif = deserializeVerificationInput(inputDirectoryPath)
				.parallel()
				.filter(input -> !input.isEmpty)
				.allMatch(this::verifyOfflineDecryptionProofsBallotBox);

		if (bbDecryptVerif) {
			return VerificationResultEvent.success(this, getVerificationDefinition());
		} else {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification12.failure"));
		}
	}

	/**
	 * Gets all inputs for the verification from the relevant files.
	 *
	 * @param inputDirectoryPath the root directory of files
	 */
	private Stream<VerifyOfflineDecryptionProofInput> deserializeVerificationInput(final Path inputDirectoryPath) {
		final var electionEvent = extractionService.getElectionEvent(inputDirectoryPath);
		final var electionEventId = TypeConverter.UUIDToStringWithoutDash(electionEvent.getId());
		final List<BallotBox> ballotBoxList = electionEvent.getBallotBoxes();
		final var ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxList.stream()
				.parallel()
				.map(bb -> {
					final var ballotBoxId = TypeConverter.UUIDToStringWithoutDash(bb.getId());
					final var ballotBoxDirectoryPath = ballotIdsPathNode.getRegexPath(ballotBoxId);

					if (extractionService.getNumberOfVotes(ballotBoxDirectoryPath) == 0) {
						return new VerifyOfflineDecryptionProofInput();
					} else {
						final var finalPayload = extractionService.getMixnetFinalPayload(ballotBoxDirectoryPath);
						final List<MixnetShufflePayload> shufflePayloads = extractionService.getMixnetShufflePayloads(ballotBoxDirectoryPath);
						shufflePayloads.sort(Comparator.comparingInt(MixnetShufflePayload::getNodeId));
						final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> initialEncryptedVotes = GroupVector.from(
								shufflePayloads.get(shufflePayloads.size() - 1).getEncryptedVotes());

						return buildVerifyOfflineDecryptionProofInput(ballotBoxId, electionEventId, finalPayload,
								initialEncryptedVotes);
					}
				});
	}

	// Transform the data to get it into the expected format for the verification algorithm
	private VerifyOfflineDecryptionProofInput buildVerifyOfflineDecryptionProofInput(final String ballotBoxId, final String electionEventId,
			final MixnetFinalPayload finalPayload,
			final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> initialEncryptedVotes) {
		final var encryptedVotes = extractEncryptedVotes(finalPayload,
				initialEncryptedVotes);
		final var electionPublicKeys = finalPayload.getPreviousRemainingElectionPublicKey();
		final var decryptionProofs = finalPayload.getVerifiablePlaintextDecryption().getDecryptionProofs();
		final var decryptedVotes = finalPayload.getVerifiablePlaintextDecryption()
				.getDecryptedVotes().stream()
				.map(m -> ElGamalMultiRecipientCiphertext.getCiphertext(m, ZqElement.create(0, decryptionProofs.getGroup()), electionPublicKeys))
				.collect(GroupVector.toGroupVector());
		final var ALLOWED_WRITE_INS_PLUS_ONE = 1;

		return new VerifyOfflineDecryptionProofInput(electionEventId, ballotBoxId, ALLOWED_WRITE_INS_PLUS_ONE,
				encryptedVotes, electionPublicKeys, decryptedVotes, decryptionProofs);
	}

	/**
	 * Extracts the encrypted votes from the MixnetFinalPayload.
	 * <p>
	 * If the payload contains only one vote, there was no shuffling and the encrypted ciphertexts correspond to the partially decrypted votes.
	 * Otherwise, the encrypted votes correspond to the votes shuffled by the offline mixing control components.
	 * </p>
	 * Expects the shufflePayloads to be sorted by nodeId.
	 **/
	private GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> extractEncryptedVotes(final MixnetFinalPayload finalPayload,
			GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> initialEncryptedVotes) {

		final Optional<VerifiableShuffle> verifiableShuffle = finalPayload.getVerifiableShuffle();
		if (verifiableShuffle.isPresent()) { // Case N > 1
			return verifiableShuffle.get().getShuffledCiphertexts();
		} else { // Case N = 1
			return initialEncryptedVotes;
		}
	}

	/**
	 * Verifies the offline decryption proofs for a ballot box.
	 *
	 * @param input contains:
	 *              <ul>
	 *                <li>electionEventId - the identifier of the election event </li>
	 *                <li>ballotBoxId - the identifier of the ballot box </li>
	 *                <li>encryptedVotes - the ciphertexts on which the decryption was done </li>
	 *                <li>electionPublicKeys - the public key that was used for the decryption </li>
	 *                <li>decryptedVotes - the ciphertexts that were obtained from the decryption </li>
	 *                <li>decryptionProofs - the proofs for the correctness of the decryption </li>
	 *              </ul>
	 * @return true if the offline decryption proofs verify, false otherwise
	 * @throws NullPointerException     if any of the inputs is null
	 * @throws IllegalArgumentException if
	 *                                  <ul>
	 *                                      <li>the vectors have different sizes</li>
	 *                                      <li>the vector elements have different sizes</li>
	 *                                      <li>the inputs have incompatible groups</li>
	 *                                  </ul>
	 */
	@SuppressWarnings("java:S117")
	@VisibleForTesting
	boolean verifyOfflineDecryptionProofsBallotBox(final VerifyOfflineDecryptionProofInput input) {
		final String electionEventId = input.ee;
		final String ballotBoxId = input.bb;
		final int allowedWriteInsPlusOne = input.allowedWriteInsPlusOne;
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> encryptedVotes = input.encryptedVotes;
		final ElGamalMultiRecipientPublicKey electoralBoardPublicKey = input.electionPublicKeys;
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> decryptedVotes = input.decryptedVotes;
		final GroupVector<DecryptionProof, ZqGroup> decryptionProofs = input.decryptionProofs;

		checkNotNull(electionEventId);
		checkNotNull(ballotBoxId);
		checkNotNull(encryptedVotes);
		checkNotNull(electoralBoardPublicKey);
		checkNotNull(decryptedVotes);
		checkNotNull(decryptionProofs);

		// Cross-dimension checks
		checkArgument(encryptedVotes.getElementSize() == decryptedVotes.getElementSize(),
				"The encrypted votes and the decrypted votes must have the same element size.");
		checkArgument(encryptedVotes.getElementSize() == decryptionProofs.getElementSize(),
				"The encrypted votes and the decryption proofs must have the same element size.");
		checkArgument(encryptedVotes.size() == decryptedVotes.size(), "There must be as many decrypted votes as there are encrypted votes.");
		checkArgument(encryptedVotes.size() == decryptionProofs.size(), "There must be as many decryption proofs as there are encrypted votes.");

		// Cross-group checks
		checkArgument(encryptedVotes.getGroup().equals(electoralBoardPublicKey.getGroup()),
				"The encrypted votes and the electoral board public key must have the same group.");
		checkArgument(encryptedVotes.getGroup().equals(decryptedVotes.getGroup()),
				"The encrypted votes and the decrypted votes must have the same group.");
		checkArgument(encryptedVotes.getGroup().hasSameOrderAs(decryptionProofs.getGroup()),
				"The encrypted votes and the decryption proofs must have the same group order.");

		final String ee = electionEventId;
		final String bb = ballotBoxId;
		final int delta_hat = allowedWriteInsPlusOne;
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_mix_4 = encryptedVotes;
		final ElGamalMultiRecipientPublicKey EB_pk = electoralBoardPublicKey;
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_dec_4 = decryptedVotes;
		final GroupVector<DecryptionProof, ZqGroup> pi_dec_4 = decryptionProofs;

		final int l = c_mix_4.getElementSize();
		final int N_c = c_mix_4.size();
		final int delta = EB_pk.size();

		checkArgument(l == delta_hat, "The encrypted votes must have one more element than the number of allowed write-ins.");
		checkArgument(N_c >= 1, "There must be at least one vote.");
		checkArgument(0 < l, "The votes must have at least one phi element.");
		checkArgument(l <= delta, "The votes must not have more phi elements than electoral board public key elements.");

		final List<String> i_aux = Arrays.asList(ee, bb, "MixDecOffline");
		final var dec_4 = new VerifiableDecryptions(c_dec_4, pi_dec_4);
		final var decryptVerif_4 = zeroKnowledgeProof.verifyDecryptions(c_mix_4, EB_pk, dec_4, i_aux);

		return decryptVerif_4.isVerified();
	}

	// Data class for the input to the verify offline decryption proofs
	static class VerifyOfflineDecryptionProofInput {
		final String bb;
		final String ee;
		final int allowedWriteInsPlusOne;
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> encryptedVotes;
		final ElGamalMultiRecipientPublicKey electionPublicKeys;
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> decryptedVotes;
		final GroupVector<DecryptionProof, ZqGroup> decryptionProofs;
		final boolean isEmpty;

		// Non-empty input
		VerifyOfflineDecryptionProofInput(final String ee, final String bb, final int allowedWriteInsPlusOne,
				final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> encryptedVotes,
				final ElGamalMultiRecipientPublicKey electionPublicKeys,
				final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> decryptedVotes,
				final GroupVector<DecryptionProof, ZqGroup> decryptionProofs) {
			this.bb = bb;
			this.ee = ee;
			this.allowedWriteInsPlusOne = allowedWriteInsPlusOne;
			this.encryptedVotes = encryptedVotes;
			this.electionPublicKeys = electionPublicKeys;
			this.decryptedVotes = decryptedVotes;
			this.decryptionProofs = decryptionProofs;

			this.isEmpty = false;
		}

		// Empty input
		VerifyOfflineDecryptionProofInput() {
			this.bb = null;
			this.ee = null;
			this.allowedWriteInsPlusOne = 1;
			this.encryptedVotes = null;
			this.electionPublicKeys = null;
			this.decryptedVotes = null;
			this.decryptionProofs = null;

			this.isEmpty = true;
		}
	}
}
