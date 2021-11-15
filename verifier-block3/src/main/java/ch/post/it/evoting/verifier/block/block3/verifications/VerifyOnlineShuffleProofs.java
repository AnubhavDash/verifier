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
package ch.post.it.evoting.verifier.block.block3.verifications;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.GroupMatrix;
import ch.post.it.evoting.cryptoprimitives.GroupVector;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetInitialPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetShufflePayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.mixnet.Mixnet;
import ch.post.it.evoting.cryptoprimitives.mixnet.ShuffleArgument;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
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
public class VerifyOnlineShuffleProofs extends AbstractVerification {

	// Currently, we do not support write-ins, we set this value to 1.
	private static final int NUMBER_WRITE_INS_PLUS_ONE = 1;
	private static final int MIN_NUMBER_OF_VOTES_FOR_SHUFFLE = 2;

	private final Mixnet mixnet;
	private final PathService pathService;
	private final ElectionDataExtractionService deserializer;

	public VerifyOnlineShuffleProofs(final Mixnet mixnet, final PathService pathService, final ElectionDataExtractionService deserializer,
			final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.mixnet = mixnet;
		this.pathService = pathService;
		this.deserializer = deserializer;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(3);
		definition.setCategory(Category.EVIDENCE);
		definition.setId(1);
		definition.setName("verifyOnlineShuffleProofs");
		definition.setDescription(TranslationHelper
				.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification01.description"));
		definition.addVerificationTrait(VerificationTrait.BLOCK_3);

		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		final boolean bbDecryptVerif = getVerificationInputs(inputDirectoryPath)
				.map(input -> input.numberOfVotes >= MIN_NUMBER_OF_VOTES_FOR_SHUFFLE ? verifyOnlineShuffleProofsBallotBox(input) : true)
				.reduce(Boolean.TRUE, Boolean::logicalAnd);

		if (bbDecryptVerif) {
			return VerificationResultEvent.success(this, getVerificationDefinition());
		} else {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification01.nok.message"));
		}
	}

	/**
	 * Gets all inputs for the verification from the relevant files.
	 *
	 * @param inputDirectoryPath the root directory of files
	 * @return a Stream of verification input, one per ballot box
	 */
	private Stream<VerifyOnlineShuffleProofInput> getVerificationInputs(final Path inputDirectoryPath) {
		final var electionEvent = deserializer.getElectionEvent(inputDirectoryPath);
		final var electionEventId = TypeConverter.UUIDToStringWithoutDash(electionEvent.getId());
		final List<BallotBox> ballotBoxList = electionEvent.getBallotBoxes();
		final var ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxList.stream()
				.map(bb -> {
					final var ballotBoxId = TypeConverter.UUIDToStringWithoutDash(bb.getId());
					final var ballotBoxDirectoryPath = ballotIdsPathNode.getRegexPath(ballotBoxId);
					final int numberOfVotes = deserializer.getNumberOfVotes(ballotBoxDirectoryPath);

					if (numberOfVotes < MIN_NUMBER_OF_VOTES_FOR_SHUFFLE) {
						return new VerifyOnlineShuffleProofInput(numberOfVotes);
					} else {
						final var initialPayload = deserializer.getMixnetInitialPayload(ballotBoxDirectoryPath);
						final List<MixnetShufflePayload> shufflePayloads = deserializer.getMixnetShufflePayloads(ballotBoxDirectoryPath);

						return new VerifyOnlineShuffleProofInput(ballotBoxId, electionEventId, shufflePayloads, initialPayload, numberOfVotes);
					}
				});
	}

	/**
	 * Verifies the given online shuffle proofs
	 *
	 * @param verifyOnlineShuffleProofInput contains:
	 *                                      <ul>
	 *                                      	<li>delta_hat - number of allowed write-ins + 1 </li>
	 *                                      	<li>c<sub>dec</sub> - the vector of partially decrypted votes </li>
	 *                                      	<li>EL<sub>pk</sub> - the remaining election public key </li>
	 *                                      	<li>c<sub>mix</sub> - the vector of shuffled ciphertexts </li>
	 *                                      	<li>pi<sub>mix</sub> - the vector of shuffle arguments </li>
	 *                                      </ul>
	 * @return true if all verifications pass, false otherwise
	 * @throws NullPointerException     if any of the given arguments is null
	 * @throws IllegalArgumentException if any of the following is true:
	 *                                  <ul>
	 *                                      <li>the number of partially decrypted votes vectors is different from 3</li>
	 *                                      <li>the number of public keys is different from 3</li>
	 *                                      <li>the number of shuffled votes vectors is different from 3</li>
	 *                                      <li>the number of shuffle arguments is different from 3</li>
	 *                                      <li>there are less than 2 votes</li>
	 *                                      <li>the numbers of shuffled votes, partially decrypted votes and decryption proofs are not all the same</li>
	 *                                      <li>the number of elements of the votes is strictly greater than the public key size</li>
	 *                                      <li>the sizes of the decryption proofs are different from the partially decrypted votes' sizes</li>
	 *                                      <li>the votes and the public keys have different groups</li>
	 *                                      <li>the decryption proofs do not have the same group order as the votes</li>
	 *                                  </ul>
	 */
	@SuppressWarnings("java:S117")
	@VisibleForTesting
	boolean verifyOnlineShuffleProofsBallotBox(VerifyOnlineShuffleProofInput verifyOnlineShuffleProofInput) {
		checkNotNull(verifyOnlineShuffleProofInput);

		final int delta_hat = NUMBER_WRITE_INS_PLUS_ONE;
		final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> c_dec = verifyOnlineShuffleProofInput.partiallyDecryptedVotes;
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> EL_pk = verifyOnlineShuffleProofInput.electionPublicKeys;
		final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> c_mix = verifyOnlineShuffleProofInput.shuffledVotes;
		final GroupVector<ShuffleArgument, GqGroup> pi_mix = verifyOnlineShuffleProofInput.shuffleArguments;

		checkNotNull(c_dec);
		checkNotNull(EL_pk);
		checkNotNull(c_mix);
		checkNotNull(pi_mix);

		// Check sizes
		checkArgument(c_dec.numColumns() == 3, "There must be exactly 3 vectors of shuffled votes.");
		checkArgument(EL_pk.size() == 3, "There must be exactly 3 public keys.");
		checkArgument(c_mix.numColumns() == 3, "There must be exactly 3 vectors of partially decrypted votes.");
		checkArgument(pi_mix.size() == 3, "There must be exactly 3 vectors of decryption proof.");

		// Check groups
		checkArgument(c_dec.getGroup().equals(EL_pk.getGroup()),
				"The partially decrypted votes and the election public keys must have the same group.");
		checkArgument(c_dec.getGroup().equals(c_mix.getGroup()), "The shuffled votes and the partially decrypted votes must have the same group.");
		checkArgument(c_mix.getGroup().equals(pi_mix.getGroup()), "The shuffle arguments must have the same group as the shuffled votes.");

		final int N_c = c_dec.numRows();
		checkArgument(c_mix.numRows() == N_c, "There must be as many partially decrypted votes as there are shuffled votes.");

		final int l = c_dec.getElementSize();
		final int delta = EL_pk.getElementSize();
		checkArgument(l == delta_hat, "The partially decrypted votes must have one more element than the number of allowed write-ins.");
		checkArgument(N_c >= 2, "The algorithm requires at least 2 votes.");
		checkArgument(0 < l, "The votes must have at least one phi element.");
		checkArgument(l <= delta, "The votes must not have more phi elements than electoral board public key elements.");

		// Operation
		// Due to 0 indexing, the indexes are slightly different from the specification
		return IntStream.range(0, 3)
				.mapToObj(j -> {
					final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_dec_j_minus_one = c_dec.getColumn(j);
					final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_mix_j = c_mix.getColumn(j);
					final ShuffleArgument pi_mix_j = pi_mix.get(j);
					final ElGamalMultiRecipientPublicKey EL_pk_j = EL_pk.get(j);
					return mixnet.verifyShuffle(c_dec_j_minus_one, c_mix_j, pi_mix_j, EL_pk_j);
				})
				.map(ch.post.it.evoting.cryptoprimitives.VerificationResult::isVerified)
				.reduce(Boolean.TRUE, Boolean::logicalAnd);
	}

	// Data class for the input to verify online shuffle proofs
	@VisibleForTesting
	static class VerifyOnlineShuffleProofInput {
		final String bb;
		final String ee;
		final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> partiallyDecryptedVotes;
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> electionPublicKeys;
		final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> shuffledVotes;
		final GroupVector<ShuffleArgument, GqGroup> shuffleArguments;
		final int numberOfVotes;

		// Construct an empty input object with the given number of votes
		VerifyOnlineShuffleProofInput(final int numberOfVotes) {
			this.bb = null;
			this.ee = null;
			this.partiallyDecryptedVotes = null;
			this.electionPublicKeys = null;
			this.shuffledVotes = null;
			this.shuffleArguments = null;

			this.numberOfVotes = numberOfVotes;
		}

		// Massage the data to get it into the expected format for the verification algorithm
		VerifyOnlineShuffleProofInput(final String ballotBoxId, final String electionEventId,
				final List<MixnetShufflePayload> shufflePayloads,
				final MixnetInitialPayload initialPayload, final int numberOfVotes) {
			shufflePayloads.sort(Comparator.comparingInt(MixnetShufflePayload::getNodeId));

			this.bb = ballotBoxId;
			this.ee = electionEventId;
			this.partiallyDecryptedVotes = Stream.concat(
							Stream.of(initialPayload),
							shufflePayloads.stream()
									.filter(mixnetShufflePayload -> mixnetShufflePayload.getNodeId() < 3))
					.map(MixnetPayload::getEncryptedVotes)
					.collect(Collectors.collectingAndThen(Collectors.toList(), GroupMatrix::fromColumns));
			this.electionPublicKeys = Stream.concat(
							Stream.of(initialPayload),
							shufflePayloads.stream()
									.filter(mixnetShufflePayload -> mixnetShufflePayload.getNodeId() < 3))
					.map(MixnetPayload::getRemainingElectionPublicKey)
					.collect(GroupVector.toGroupVector());
			this.shuffledVotes = shufflePayloads.stream()
					.map(MixnetShufflePayload::getVerifiableShuffle)
					.map(VerifiableShuffle::getShuffledCiphertexts)
					.collect(Collectors.collectingAndThen(Collectors.toList(), GroupMatrix::fromColumns));
			this.shuffleArguments = shufflePayloads.stream()
					.map(MixnetShufflePayload::getVerifiableShuffle)
					.map(VerifiableShuffle::getShuffleArgument)
					.collect(GroupVector.toGroupVector());

			this.numberOfVotes = numberOfVotes;

		}
	}
}