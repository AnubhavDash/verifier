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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import ch.post.it.evoting.cryptoprimitives.GroupMatrix;
import ch.post.it.evoting.cryptoprimitives.GroupVector;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.ZqGroup;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.DecryptionProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.VerifiableDecryptions;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProofService;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetShufflePayload;
import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.BallotBox;
import ch.post.it.evoting.verifier.common.block.exceptions.MissingFileException;
import ch.post.it.evoting.verifier.common.block.serialization.DownloadedBallotSerialization;
import ch.post.it.evoting.verifier.common.block.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.dto.DownloadedBallot;

@SuppressWarnings("java:S117")
public class VerifyOnlineDecryptionProofs extends AbstractVerification {

	@Autowired
	ElectionDataExtractionService deserializer;

	@Autowired
	private final ZeroKnowledgeProof zeroKnowledgeProof = new ZeroKnowledgeProofService();

	@Override
	public VerificationDefinition getVerificationDefinition() {
		var verificationDefinition = new VerificationDefinition();
		verificationDefinition.setBlockId(3);
		verificationDefinition.setCategory(Category.EVIDENCE);
		verificationDefinition.setId(2);
		verificationDefinition.setName("onlineDecryptionProofsVerificationBlock3");
		verificationDefinition.setDescription(TranslationHelper
				.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification02.description"));

		return verificationDefinition;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		final var result = new VerificationResult();

		final boolean bbDecryptVerif = deserializeVerificationInput(inputDirectoryPath)
				.parallel()
				.filter(input -> !input.isEmpty)
				.allMatch(this::verifyOnlineDecryptionProofsBallotBox);

		if (bbDecryptVerif) {
			result.setStatus(Status.OK);
		} else {
			result.setStatus(Status.NOK);
			result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification02.failure"));
		}
		return result;
	}

	/**
	 * Gets all inputs for the verification from the relevant files.
	 *
	 * @param inputDirectoryPath the root directory of files
	 * @throws IOException if there's a problem while deserializing from file
	 */
	private Stream<VerifyOnlineDecryptionProofInput> deserializeVerificationInput(Path inputDirectoryPath) throws IOException {
		final var electionEvent = deserializer.getElectionEvent(inputDirectoryPath);
		final var electionEventId = TypeConverter.UUIDToStringWithoutDash(electionEvent.getId());
		final List<BallotBox> ballotBoxList = electionEvent.getBallotBoxes();
		final var ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxList.stream()
				.parallel()
				.map(bb_i -> {
					final var ballotBoxId = TypeConverter.UUIDToStringWithoutDash(bb_i.getId());
					final var ballotBoxDirectoryPath = ballotIdsPathNode.getRegexPath(ballotBoxId);

					if (deserializer.getNumberOfVotes(ballotBoxDirectoryPath) == 0) {
						return new VerifyOnlineDecryptionProofInput();
					} else {
						final List<MixnetShufflePayload> shufflePayloads = deserializer.getMixnetShufflePayloads(ballotBoxDirectoryPath);
						if (shufflePayloads.size() < 3) {
							throw new MissingFileException("Missing shufflePayload file(s)");
						}
						shufflePayloads.sort(Comparator.comparingInt(MixnetShufflePayload::getNodeId));

						final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> initialEncryptedVotes = deserializeEncryptedVotes(
								ballotBoxDirectoryPath, shufflePayloads.get(0).getEncryptionGroup());

						return extractVerificationInput(ballotBoxId, electionEventId, shufflePayloads, initialEncryptedVotes);
					}
				});
	}

	// Massage the data to get it into the expected format for the verification algorithm
	private VerifyOnlineDecryptionProofInput extractVerificationInput(final String ballotBoxId, final String electionEventId,
			final List<MixnetShufflePayload> shufflePayloads,
			final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> initialEncryptedVotes) {
		final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> encryptedVotes = extractEncryptedVotes(shufflePayloads,
				initialEncryptedVotes);
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> electionPublicKeys = shufflePayloads.stream()
				.map(MixnetShufflePayload::getNodeElectionPublicKey)
				.collect(GroupVector.toGroupVector());
		final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> partiallyDecryptedVotes = shufflePayloads.stream()
				.map(MixnetShufflePayload::getVerifiableDecryptions)
				.map(VerifiableDecryptions::getCiphertexts)
				.collect(Collectors.collectingAndThen(Collectors.toList(), GroupMatrix::fromColumns));
		final GroupMatrix<DecryptionProof, ZqGroup> decryptionProofs = shufflePayloads.stream()
				.map(MixnetShufflePayload::getVerifiableDecryptions)
				.map(VerifiableDecryptions::getDecryptionProofs)
				.collect(Collectors.collectingAndThen(Collectors.toList(), GroupMatrix::fromColumns));

		return new VerifyOnlineDecryptionProofInput(ballotBoxId, electionEventId, encryptedVotes, electionPublicKeys, partiallyDecryptedVotes,
				decryptionProofs);
	}

	/**
	 * Deserializes the encrypted votes from the downloadedBallotBox.csv file.
	 *
	 * @param ballotBoxDirectoryPath the path of the ballot box in which the file is stored.
	 * @param gqGroup                the GqGroup of the encrypted votes to be deserialized
	 * @return a vector of ciphertexts
	 * @throws UncheckedIOException if the downloaded ballot box could not be read correctly
	 */
	private GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> deserializeEncryptedVotes(final Path ballotBoxDirectoryPath, final GqGroup gqGroup) {
		final PathNode downloadedBallotBoxPathNode;
		try {
			downloadedBallotBoxPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.DOWNLOADED_BALLOT_BOX, ballotBoxDirectoryPath);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		try (Stream<DownloadedBallot> downloadedBallotStream = DownloadedBallotSerialization
				.deserializeDownloadedBallotBox(downloadedBallotBoxPathNode.getPath())) {
			final Stream<String> encryptedVotingOptions = downloadedBallotStream
					.map(ballot -> ballot.getVote().getEncryptedOptions());
			return encryptedVotingOptions.map(enc -> enc.split(";"))
					.map(splitted -> Arrays.stream(splitted).map(BigInteger::new).collect(Collectors.toList()))
					.map(bi -> bi.stream().map(i -> GqElement.create(i, gqGroup)).collect(Collectors.toList()))
					.map(gq -> ElGamalMultiRecipientCiphertext.create(gq.get(0), gq.subList(1, gq.size())))
					.collect(GroupVector.toGroupVector());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Choose the correct encrypted votes, which depends on the number of votes and the node id.
	 * <p>
	 * If the mixnet shuffle payload contains a verifiable shuffle, chooses the shuffled ciphertexts. Otherwise, if the mixnet shuffle payload is for
	 * the first node, chooses the initial encrypted votes. Else, chooses the previous partially decrypted votes.
	 * </p>
	 * Expects the shufflePayloads to be sorted by nodeId.
	 **/
	private GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> extractEncryptedVotes(final List<MixnetShufflePayload> shufflePayloads,
			GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> initialEncryptedVotes) {
		List<GroupVector<ElGamalMultiRecipientCiphertext, GqGroup>> encryptedVotes = new LinkedList<>();

		for (int i = 0; i < shufflePayloads.size(); i++) {
			final var verifiableShuffle = shufflePayloads.get(i).getVerifiableShuffle();
			if (verifiableShuffle != null) { // Case N > 1
				encryptedVotes.add(verifiableShuffle.getShuffledCiphertexts());
			} else { // Case N = 1
				final int nodeId = shufflePayloads.get(i).getNodeId();
				if (nodeId == 1) {
					encryptedVotes.add(initialEncryptedVotes);
				} else {
					final var previousVerifiableDecryptions = shufflePayloads.get(i - 1).getVerifiableDecryptions();
					encryptedVotes.add(previousVerifiableDecryptions.getCiphertexts());
				}
			}
		}
		return encryptedVotes.stream().collect(Collectors.collectingAndThen(Collectors.toList(), GroupMatrix::fromColumns));
	}

	/**
	 * Verifies the given online decryption proofs
	 *
	 * @param verifyOnlineDecryptionProofInput contains:
	 *                                         <ul>
	 *                                           <li>electionEventId - the identifier of the election event</li>
	 *                                           <li>ballotBoxId - the identifier of the ballot box</li>
	 *                                           <li>shuffledVotes - the ciphertexts on which the decryption was done</li>
	 *                                           <li>electionPublicKeys - the public key that was used for the decryption</li>
	 *                                           <li>partiallyDecryptedVotes - the ciphertexts that were obtained from the partial decryption</li>
	 *                                           <li>decryptionProofs - the proofs for the correctness of the decryption</li>
	 *                                         </ul>
	 * @return true if all verifications pass, false otherwise
	 * @throws NullPointerException     if any of the given arguments is null
	 * @throws IllegalArgumentException if any of the following is true:
	 *                                  <ul>
	 *                                      <li>the number of shuffled votes vectors is different from 3</li>
	 *                                      <li>the number of public keys is different from 3</li>
	 *                                      <li>the number of partially decrypted votes vectors is different from 3</li>
	 *                                      <li>the number of decryption proofs vectors is different from 3</li>
	 *                                      <li>there are no votes</li>
	 *                                      <li>the numbers of shuffled votes, partially decrypted votes and decryption proofs are not all the same</li>
	 *                                      <li>the number of elements of the votes is strictly greater than the public key size</li>
	 *                                      <li>the sizes of the decryption proofs are different from the partially decrypted votes' sizes</li>
	 *                                      <li>the votes and the public keys have different groups</li>
	 *                                      <li>the decryption proofs do not have the same group order as the votes</li>
	 *                                  </ul>
	 */
	@SuppressWarnings("java:S117")
	private boolean verifyOnlineDecryptionProofsBallotBox(VerifyOnlineDecryptionProofInput verifyOnlineDecryptionProofInput) {
		final String electionEventId = verifyOnlineDecryptionProofInput.ee;
		final String ballotBoxId = verifyOnlineDecryptionProofInput.bb;
		final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> encryptedVotes = verifyOnlineDecryptionProofInput.encryptedVotes;
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> electionPublicKeys = verifyOnlineDecryptionProofInput.electionPublicKeys;
		final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> partiallyDecryptedVotes = verifyOnlineDecryptionProofInput.partiallyDecryptedVotes;
		final GroupMatrix<DecryptionProof, ZqGroup> decryptionProofs = verifyOnlineDecryptionProofInput.decryptionProofs;

		checkNotNull(electionEventId);
		checkNotNull(ballotBoxId);
		checkNotNull(encryptedVotes);
		checkNotNull(electionPublicKeys);
		checkNotNull(partiallyDecryptedVotes);
		checkNotNull(decryptionProofs);

		final String ee = electionEventId;
		final String bb = ballotBoxId;
		final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> c_mix = encryptedVotes;
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> EL_pk = electionPublicKeys;
		final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> c_dec = partiallyDecryptedVotes;
		final GroupMatrix<DecryptionProof, ZqGroup> pi_dec = decryptionProofs;

		// Check sizes
		checkArgument(c_mix.numColumns() == 3, "There must be exactly 3 vectors of shuffled votes.");
		checkArgument(EL_pk.size() == 3, "There must be exactly 3 public keys.");
		checkArgument(c_dec.numColumns() == 3, "There must be exactly 3 vectors of partially decrypted votes.");
		checkArgument(pi_dec.numColumns() == 3, "There must be exactly 3 vectors of decryption proof.");

		final int N_c = c_mix.numRows();
		checkArgument(1 <= N_c, "The algorithm requires at least 1 vote.");
		checkArgument(c_dec.numRows() == N_c, "There must be as many partially decrypted votes as there are shuffled votes.");
		checkArgument(pi_dec.numRows() == N_c, "There must be as many decryption proofs as there are shuffled votes.");

		final int l = c_mix.getElementSize(); // Exists because c_mix contains 3 elements
		final int mu = EL_pk.getElementSize();
		checkArgument(0 < l, "The shuffled votes must contain at least one element.");
		checkArgument(l <= mu, "The shuffled votes must contain at most as many elements as the election public keys.");
		checkArgument(c_dec.getElementSize() == l, "The partially decrypted ciphertexts must all have the same size l.");
		checkArgument(pi_dec.getElementSize() == l, "The decryption proofs must all have the same size l.");

		// Check groups
		checkArgument(c_mix.getGroup().equals(EL_pk.getGroup()), "The shuffled votes and the election public keys must have the same group.");
		checkArgument(c_mix.getGroup().equals(c_dec.getGroup()), "The shuffled votes and the partially decrypted votes must have the same group.");
		checkArgument(c_dec.getGroup().hasSameOrderAs(pi_dec.getGroup()),
				"The decryption proofs must have the same group order as the partially decrypted votes.");

		// Operation
		//Due to 0 indexing, the indexes are slightly different than in the specification
		return IntStream.range(0, 3)
				.mapToObj(j -> {
					final List<String> i_aux = Arrays.asList(ee, bb, "MixDecOnline", String.valueOf(j + 1));
					final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_mix_j = c_mix.getColumn(j);
					final ElGamalMultiRecipientPublicKey EL_pk_j = EL_pk.get(j);
					final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_dec_j = c_dec.getColumn(j);
					final GroupVector<DecryptionProof, ZqGroup> pi_dec_j = pi_dec.getColumn(j);
					final var dec_j = new VerifiableDecryptions(c_dec_j, pi_dec_j);
					return zeroKnowledgeProof.verifyDecryptions(c_mix_j, EL_pk_j, dec_j, i_aux);
				}).allMatch(ch.post.it.evoting.cryptoprimitives.VerificationResult::isVerified);
	}

	//Data class for the input to the verify online decryption proofs
	private static class VerifyOnlineDecryptionProofInput {
		final String bb;
		final String ee;
		final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> encryptedVotes;
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> electionPublicKeys;
		final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> partiallyDecryptedVotes;
		final GroupMatrix<DecryptionProof, ZqGroup> decryptionProofs;
		final boolean isEmpty;

		//Non empty input
		VerifyOnlineDecryptionProofInput(final String bb, final String ee,
				final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> encryptedVotes,
				final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> electionPublicKeys,
				final GroupMatrix<ElGamalMultiRecipientCiphertext, GqGroup> partiallyDecryptedVotes,
				final GroupMatrix<DecryptionProof, ZqGroup> decryptionProofs) {
			this.bb = bb;
			this.ee = ee;
			this.encryptedVotes = encryptedVotes;
			this.electionPublicKeys = electionPublicKeys;
			this.partiallyDecryptedVotes = partiallyDecryptedVotes;
			this.decryptionProofs = decryptionProofs;

			this.isEmpty = false;
		}

		//Empty input
		VerifyOnlineDecryptionProofInput() {
			this.bb = null;
			this.ee = null;
			this.encryptedVotes = null;
			this.electionPublicKeys = null;
			this.partiallyDecryptedVotes = null;
			this.decryptionProofs = null;

			this.isEmpty = true;
		}
	}
}
