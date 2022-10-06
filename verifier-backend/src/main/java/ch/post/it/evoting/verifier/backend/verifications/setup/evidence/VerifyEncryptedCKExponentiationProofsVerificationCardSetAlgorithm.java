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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static ch.post.it.evoting.cryptoprimitives.utils.Conversions.integerToString;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

		// Context.
		final GqGroup group = context.getEncryptionGroup();
		final int j = context.getJ();
		final String ee = context.getElectionEventId();
		final String vcs = context.getVerificationCardSetId();
		final int N_E = context.getNumberOfVoters();

		// Input.
		final List<String> vc = input.getVerificationCardIds();
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_ck = input.getEncryptedHashedConfirmationKey();
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> Kc_j = input.getVoterVoteCastReturnCodeGenerationPublicKeys();
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_expCK_j = input.getExponentiatedEncryptedHashedConfirmationKey();
		final GroupVector<ExponentiationProof, ZqGroup> pi_expCK_j = input.getProofsOfCorrectCKExponentiation();

		// Cross-group check.
		checkArgument(c_ck.getGroup().equals(group), "The context and input must have the same encryption group.");

		// Cross-size validations.
		checkArgument(vc.size() == N_E, "The size of each input must be equal to the number of voters.");

		// Operation.
		return IntStream.range(0, N_E)
				.parallel()
				.mapToObj(id -> {
					final GroupVector<GqElement, GqGroup> g = Stream.concat(Stream.of(group.getGenerator()), c_ck.get(id).stream())
							.collect(GroupVector.toGroupVector());

					final GroupVector<GqElement, GqGroup> y = Stream.concat(Kc_j.get(id).getKeyElements().stream(), c_expCK_j.get(id).stream())
							.collect(GroupVector.toGroupVector());

					final List<String> i_aux = List.of(ee, vc.get(id), "GenEncLongCodeShares", integerToString(j));

					final boolean exponentiationVerif_id = zeroKnowledgeProof.verifyExponentiation(g, y, pi_expCK_j.get(id), i_aux);

					if (exponentiationVerif_id) {
						LOGGER.debug("The encrypted CK exponentiation proof is valid. [ee: {}, vcs: {}, j: {}, vc_id: {}]", ee, vcs, j, vc.get(id));
					} else {
						LOGGER.error("The encrypted CK exponentiation proof is invalid. [ee: {}, vcs: {}, j: {}, vc_id: {}]", ee, vcs, j, vc.get(id));
					}
					return exponentiationVerif_id;
				})
				.reduce(Boolean::logicalAnd)
				.orElseThrow();
	}
}
