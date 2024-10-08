/*
 * (c) Copyright 2024 Swiss Post Ltd.
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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static ch.post.it.evoting.cryptoprimitives.utils.Conversions.integerToString;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.ZqGroup;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ExponentiationProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProof;

@Service
public class VerifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm.class);

	private final ZeroKnowledgeProof zeroKnowledgeProof;

	public VerifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm(final ZeroKnowledgeProof zeroKnowledgeProof) {
		this.zeroKnowledgeProof = zeroKnowledgeProof;
	}

	/**
	 * Verifies the exponentiation proofs of a list of verification card IDs.
	 *
	 * @param context the context of the algorithm. Must be non-null.
	 * @param input   the input of the algorithm. Must be non-null.
	 * @return true if the proofs are valid for all {@code id}, false otherwise.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyEncryptedCKExponentiationProofsVerificationCardSet(
			final VerifyEncryptedExponentiationProofsVerificationCardSetContext context,
			final VerifyEncryptedExponentiationProofsVerificationCardSetInput input) {
		checkNotNull(context);
		checkNotNull(input);

		// Cross-group check.
		checkArgument(context.getEncryptionGroup().equals(input.getEncryptedHashedConfirmationKey().getGroup()),
				"The context and input must have the same encryption group.");

		// Context.
		final GqGroup p_q_g = context.getEncryptionGroup();
		final int j = context.getJ();
		final String ee = context.getElectionEventId();
		final ImmutableList<String> vc = context.getVerificationCardIds();
		final int N_E = vc.size();

		// Input.
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_ck = input.getEncryptedHashedConfirmationKey();
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> Kc_j = input.getVoterVoteCastReturnCodeGenerationPublicKeys();
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_expCK_j = input.getExponentiatedEncryptedHashedConfirmationKey();
		final GroupVector<ExponentiationProof, ZqGroup> pi_expCK_j = input.getProofsOfCorrectCKExponentiation();

		// Cross-size validations.
		checkArgument(N_E == c_ck.size(), "The size of each input must be equal to the number of voters.");

		// Operation.
		final boolean verified = IntStream.range(0, N_E)
				.parallel()
				.mapToObj(id -> {
					final GroupVector<GqElement, GqGroup> g = Stream.concat(Stream.of(p_q_g.getGenerator()), c_ck.get(id).stream())
							.collect(GroupVector.toGroupVector());

					final GroupVector<GqElement, GqGroup> y = Stream.concat(Kc_j.get(id).getKeyElements().stream(), c_expCK_j.get(id).stream())
							.collect(GroupVector.toGroupVector());

					final ImmutableList<String> i_aux = ImmutableList.of(ee, vc.get(id), "GenEncLongCodeShares", integerToString(j));

					final boolean exponentiationVerif_id = zeroKnowledgeProof.verifyExponentiation(g, y, pi_expCK_j.get(id), i_aux);

					if (exponentiationVerif_id) {
						LOGGER.debug("The encrypted CK exponentiation proof is valid. [ee: {}, j: {}, vc_id: {}]", ee, j, vc.get(id));
					} else {
						LOGGER.error("The encrypted CK exponentiation proof is invalid. [ee: {}, j: {}, vc_id: {}]", ee, j, vc.get(id));
					}
					return exponentiationVerif_id;
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		LOGGER.debug("Successfully verified the CK exponentiation proofs of all verification cards in verification card set ID.");

		return verified;
	}
}
