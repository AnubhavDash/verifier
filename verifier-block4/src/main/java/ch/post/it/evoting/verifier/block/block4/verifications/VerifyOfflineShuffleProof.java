/*
 * Copyright 2021 Post CH Ltd
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.GroupVector;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetShufflePayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.mixnet.Mixnet;
import ch.post.it.evoting.cryptoprimitives.mixnet.ShuffleArgument;
import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.AbstractVerification;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.dto.revised.BallotBox;
import ch.post.it.evoting.verifier.common.block.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;

@Component
public class VerifyOfflineShuffleProof extends AbstractVerification {

	private final PathService pathService;
	private final ElectionDataExtractionService extractionService;
	private final Mixnet mixnet;

	@Autowired
	public VerifyOfflineShuffleProof(final PathService pathService, final ElectionDataExtractionService extractionService, final Mixnet mixnet,
			final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
		this.extractionService = extractionService;
		this.mixnet = mixnet;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(4);
		definition.setCategory(Category.EVIDENCE);
		definition.setId(11);
		definition.setName("verifyOfflineShuffleProofBlock4");
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification11.description"));
		definition.addVerificationTrait(VerificationTrait.BLOCK_4);

		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		final boolean bbDecryptVerif = deserializeVerificationInput(inputDirectoryPath)
				.parallel()
				.filter(input -> !input.isEmpty)
				.allMatch(this::verifyOfflineShuffleProofBallotBox);

		if (bbDecryptVerif) {
			return VerificationResultEvent.success(this, getVerificationDefinition());
		} else {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification11.nok.message"));
		}
	}

	/**
	 * Get all inputs for the verification from the relevant files.
	 *
	 * @param inputDirectoryPath the root directory of files
	 */
	private Stream<VerifyOfflineShuffleProofInput> deserializeVerificationInput(Path inputDirectoryPath) {
		final var electionEvent = extractionService.getElectionEvent(inputDirectoryPath);
		final List<BallotBox> ballotBoxList = electionEvent.getBallotBoxes();
		final var ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxList.stream()
				.map(bb -> {
					final var ballotBoxId = TypeConverter.UUIDToStringWithoutDash(bb.getId());
					final var ballotBoxDirectoryPath = ballotIdsPathNode.getRegexPath(ballotBoxId);
					final var minNumberOfVotesForShuffle = 2;

					if (extractionService.getNumberOfVotes(ballotBoxDirectoryPath) < minNumberOfVotesForShuffle) {
						return new VerifyOfflineShuffleProofInput();
					} else {
						final var finalPayload = extractionService.getMixnetFinalPayload(ballotBoxDirectoryPath);
						final List<MixnetShufflePayload> shufflePayloads = extractionService.getMixnetShufflePayloads(ballotBoxDirectoryPath);
						shufflePayloads.sort(Comparator.comparingInt(MixnetShufflePayload::getNodeId));
						final MixnetShufflePayload lastShufflePayload = shufflePayloads.get(shufflePayloads.size() - 1);

						// Currently, we do not support write-ins, therefore, we set this value to 1.
						final var allowedWriteInsPlusOne = 1;
						final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> partiallyDecryptedVotes = lastShufflePayload.getVerifiableDecryptions()
								.getCiphertexts();
						final ElGamalMultiRecipientPublicKey electoralBoardPublicKey = lastShufflePayload.getRemainingElectionPublicKey();
						// Since we have checked that we have at least two votes, this should never throw
						final var verifiableShuffle = finalPayload.getVerifiableShuffle().orElseThrow();
						final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> shuffledVotes = verifiableShuffle.getShuffledCiphertexts();
						final var shuffleProof = verifiableShuffle.getShuffleArgument();

						return new VerifyOfflineShuffleProofInput(allowedWriteInsPlusOne, partiallyDecryptedVotes, electoralBoardPublicKey,
								shuffledVotes, shuffleProof);
					}
				});
	}

	/**
	 * Verifies the offline shuffle proof for a ballot box.
	 *
	 * @param input contains:
	 *              <dl>
	 *              	<dt>delta_hat</dt><dd>- number of allowed write-ins + 1</dd>
	 *              	<dt>c<sub>dec,3</sub></dt><dd>- the vector of partially decrypted votes</dd>
	 *              	<dt>EB<sub>pk</sub></dt><dd>- the electoral board public key</dd>
	 *              	<dt>c<sub>mix,4</sub></dt><dd>- the vector of shuffled ciphertexts</dd>
	 *              	<dt>pi<sub>mix,4</sub></dt><dd>- the shuffle argument</dd>
	 *              </dl>
	 * @return true if the shuffle argument verifies correctly, false otherwise
	 * @throws NullPointerException     if any of the input's fields are null
	 * @throws IllegalArgumentException if
	 *                                  <ul>
	 *                                      <li></li>
	 *                                  </ul>
	 */
	@SuppressWarnings("java:S117")
	@VisibleForTesting
	boolean verifyOfflineShuffleProofBallotBox(final VerifyOfflineShuffleProofInput input) {
		final int allowedWriteInsPlusOne = input.allowedWriteInsPlusOne;
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> partiallyDecryptedVotes = input.partiallyDecryptedVotes;
		final ElGamalMultiRecipientPublicKey electoralBoardPublicKey = input.electionPublicKeys;
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> shuffledVotes = input.shuffledVotes;
		final ShuffleArgument shuffleProof = input.shuffleProof;

		checkNotNull(partiallyDecryptedVotes);
		checkNotNull(electoralBoardPublicKey);
		checkNotNull(shuffledVotes);
		checkNotNull(shuffleProof);

		// Cross-dimension checks
		checkArgument(partiallyDecryptedVotes.size() == shuffledVotes.size(),
				"There must be as many partially decrypted votes as there are shuffled votes.");
		checkArgument(partiallyDecryptedVotes.getElementSize() == shuffledVotes.getElementSize(),
				"The partially decrypted votes and the shuffled votes must have the same element size.");

		// Cross-group checks
		checkArgument(partiallyDecryptedVotes.getGroup().equals(electoralBoardPublicKey.getGroup()),
				"The partially decrypted votes and the electoral board public key must have the same group.");
		checkArgument(partiallyDecryptedVotes.getGroup().equals(shuffledVotes.getGroup()),
				"The partially decrypted votes and the shuffled votes must have the same group.");

		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_dec_3 = partiallyDecryptedVotes;
		final ElGamalMultiRecipientPublicKey EB_pk = electoralBoardPublicKey;
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_mix_4 = shuffledVotes;
		final ShuffleArgument pi_mix_4 = shuffleProof;

		final int delta_hat = allowedWriteInsPlusOne;
		final int l = c_dec_3.getElementSize();
		final int N_c = c_dec_3.size();
		final int delta = EB_pk.size();

		checkArgument(l == delta_hat, "The partially decrypted votes must have one more element than the number of allowed write-ins.");
		checkArgument(N_c >= 2, "There must be at least two votes.");
		checkArgument(0 < l, "The votes must have at least one phi element.");
		checkArgument(l <= delta, "The votes must not have more phi elements than electoral board public key elements.");

		return mixnet.verifyShuffle(c_dec_3, c_mix_4, pi_mix_4, EB_pk).isVerified();
	}

	//Data class for the input to the verify offline shuffle proof
	@VisibleForTesting
	static class VerifyOfflineShuffleProofInput {
		final int allowedWriteInsPlusOne;
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> partiallyDecryptedVotes;
		final ElGamalMultiRecipientPublicKey electionPublicKeys;
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> shuffledVotes;
		final ShuffleArgument shuffleProof;
		final boolean isEmpty;

		//Non empty input
		VerifyOfflineShuffleProofInput(final int allowedWriteInsPlusOne,
				final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> partiallyDecryptedVotes,
				final ElGamalMultiRecipientPublicKey electionPublicKeys,
				final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> shuffledVotes,
				final ShuffleArgument shuffleProof) {
			this.allowedWriteInsPlusOne = allowedWriteInsPlusOne;
			this.partiallyDecryptedVotes = partiallyDecryptedVotes;
			this.electionPublicKeys = electionPublicKeys;
			this.shuffledVotes = shuffledVotes;
			this.shuffleProof = shuffleProof;

			this.isEmpty = false;
		}

		//Empty input
		VerifyOfflineShuffleProofInput() {
			this.allowedWriteInsPlusOne = 1;
			this.partiallyDecryptedVotes = null;
			this.electionPublicKeys = null;
			this.shuffledVotes = null;
			this.shuffleProof = null;

			this.isEmpty = true;
		}
	}
}
