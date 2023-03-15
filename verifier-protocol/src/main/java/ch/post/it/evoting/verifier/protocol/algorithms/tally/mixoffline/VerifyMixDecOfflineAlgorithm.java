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
package ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline;

import static ch.post.it.evoting.cryptoprimitives.utils.Conversions.integerToString;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Streams;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.mixnet.Mixnet;
import ch.post.it.evoting.cryptoprimitives.mixnet.ShuffleArgument;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.cryptoprimitives.utils.VerificationResult;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.VerifiableDecryptions;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProof;

/**
 * Implements the VerifyMixDecOffline algorithm.
 */
public class VerifyMixDecOfflineAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyMixDecOfflineAlgorithm.class);

	private final ElGamal elGamal;
	private final Mixnet mixnet;
	private final ZeroKnowledgeProof zeroKnowledgeProof;

	public VerifyMixDecOfflineAlgorithm(final ElGamal elGamal, final Mixnet mixnet, final ZeroKnowledgeProof zeroKnowledgeProof) {
		this.elGamal = elGamal;
		this.mixnet = mixnet;
		this.zeroKnowledgeProof = zeroKnowledgeProof;
	}

	/**
	 * Verifies the shuffle and decryption proofs of the online control components.
	 *
	 * @param context the context of the verification. Must be non-null.
	 * @param input   the input to be verified. Must be non-null.
	 * @return {@code true} if the input proofs verify, {@code false} otherwise.
	 * @throws NullPointerException if {@code context} or {@code input} is null.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyMixDecOffline(final VerifyMixDecOfflineContext context, final VerifyMixDecInput input) {
		checkNotNull(context);
		checkNotNull(input);

		// Cross-group checks
		checkArgument(context.encryptionGroup().equals(input.initialCiphertexts().getGroup()),
				"The context and input must have the same encryption group.");

		// Context
		final String ee = context.electionEventId();
		final String bb = context.ballotBoxId();
		final int delta_hat = context.numberOfAllowedWriteInsPlusOne();

		// Input
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_init_1 = input.initialCiphertexts();
		final GroupVector<GroupVector<ElGamalMultiRecipientCiphertext, GqGroup>, GqGroup> c_mix = input.precedingVerifiableShuffledVotes().stream()
				.map(VerifiableShuffle::shuffledCiphertexts)
				.collect(GroupVector.toGroupVector());
		final GroupVector<ShuffleArgument, GqGroup> pi_mix = input.precedingVerifiableShuffledVotes().stream()
				.map(VerifiableShuffle::shuffleArgument)
				.collect(GroupVector.toGroupVector());
		final List<VerifiableDecryptions> cAndPi_dec = input.precedingVerifiableDecryptedVotes();
		final ElGamalMultiRecipientPublicKey EL_pk = input.electionPublicKey();
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> EL_pk_1To4 = input.ccmElectionPublicKeys();
		final ElGamalMultiRecipientPublicKey EB_pk = input.electoralBoardPublicKey();

		// Requires
		final int l = c_init_1.getElementSize();
		final int delta = EL_pk.size();
		// N_c_hat >= 2 assured by the VerifyMixDecInput
		checkArgument(l == delta_hat, "The votes must have exactly the number of allowed write-ins + 1 elements.");
		// 0 < l assured by ElGamalCiphertext
		// l <= delta assured by VerifyMixDecInput
		// delta <= mu assured by VerifyMixDecInput

		final List<VerificationResult> shuffleVerif = new ArrayList<>();
		final List<VerificationResult> decryptVerif = new ArrayList<>();

		record Verifs(VerificationResult shuffleVerif, VerificationResult decryptVerif) {
		}

		// Operation
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> EL_pk_1To4_prime = EL_pk_1To4.stream()
				.map(EL_pk_j -> GroupVector.from(EL_pk_j.getKeyElements().subList(0, delta)))
				.map(ElGamalMultiRecipientPublicKey::new)
				.collect(GroupVector.toGroupVector());

		final VerificationResult shuffleVerif_1 = mixnet.verifyShuffle(c_init_1, c_mix.get(0), pi_mix.get(0), EL_pk);
		shuffleVerif.add(shuffleVerif_1);

		final List<String> i_aux_1 = List.of(ee, bb, "MixDecOnline", integerToString(1));

		final VerificationResult decryptVerif_1 = zeroKnowledgeProof.verifyDecryptions(c_mix.get(0), EL_pk_1To4.get(0), cAndPi_dec.get(0), i_aux_1);
		decryptVerif.add(decryptVerif_1);

		// The specification uses 1 indexing, but we are bound to 0 indexing
		final List<Verifs> verifs = IntStream.rangeClosed(1, 3).parallel().mapToObj(j -> {
			final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> publicKeysToCombine = Streams.concat(
					IntStream.range(j, 4).mapToObj(EL_pk_1To4_prime::get), Stream.of(EB_pk)).collect(GroupVector.toGroupVector());
			final ElGamalMultiRecipientPublicKey EL_pk_bar = elGamal.combinePublicKeys(publicKeysToCombine);

			final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_dec_j_minus_1 = cAndPi_dec.get(j - 1).getCiphertexts();
			final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_mix_j = c_mix.get(j);
			final ShuffleArgument pi_mix_j = pi_mix.get(j);
			final VerificationResult shuffleVerif_j = mixnet.verifyShuffle(c_dec_j_minus_1, c_mix_j, pi_mix_j, EL_pk_bar);

			final List<String> i_aux_j = List.of(ee, bb, "MixDecOnline", integerToString(j + 1)); // 0-indexing

			final ElGamalMultiRecipientPublicKey EL_pk_j = EL_pk_1To4.get(j);
			final VerifiableDecryptions cAndPi_dec_j = cAndPi_dec.get(j);
			final VerificationResult decryptVerif_j = zeroKnowledgeProof.verifyDecryptions(c_mix_j, EL_pk_j, cAndPi_dec_j, i_aux_j);

			return new Verifs(shuffleVerif_j, decryptVerif_j);
		}).toList();

		shuffleVerif.addAll(verifs.stream().map(Verifs::shuffleVerif).toList());
		decryptVerif.addAll(verifs.stream().map(Verifs::decryptVerif).toList());

		if (decryptVerif.stream().allMatch(VerificationResult::isVerified) && shuffleVerif.stream().allMatch(VerificationResult::isVerified)) {
			return true;
		} else {
			decryptVerif.stream()
					.filter(not(VerificationResult::isVerified))
					.forEach(verificationResult -> LOGGER.error(verificationResult.getErrorMessages().getFirst()));
			return false;
		}
	}
}
