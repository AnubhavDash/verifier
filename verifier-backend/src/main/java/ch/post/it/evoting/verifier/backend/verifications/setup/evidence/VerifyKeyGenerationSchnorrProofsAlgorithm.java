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

import static ch.post.it.evoting.cryptoprimitives.domain.ControlComponentConstants.NODE_IDS;
import static ch.post.it.evoting.cryptoprimitives.utils.Conversions.integerToString;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.ZqGroup;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.SchnorrProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProof;

@Service
public class VerifyKeyGenerationSchnorrProofsAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyKeyGenerationSchnorrProofsAlgorithm.class);

	private final ZeroKnowledgeProof zeroKnowledgeProof;

	public VerifyKeyGenerationSchnorrProofsAlgorithm(final ZeroKnowledgeProof zeroKnowledgeProof) {
		this.zeroKnowledgeProof = zeroKnowledgeProof;
	}

	/**
	 * Verifies the CCRj, CCMj and Electoral Board Schnorr proofs of knowledge.
	 *
	 * @param context the context for the VerifyKeyGenerationSchnorrProofs algorithm. Must be non-null.
	 * @param input   the input for the VerifyKeyGenerationSchnorrProofs algorithm. Must be non-null.
	 * @return true if all proofs are valid, false otherwise.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyKeyGenerationSchnorrProofs(final VerifyKeyGenerationSchnorrProofsContext context,
			final VerifyKeyGenerationSchnorrProofsInput input) {
		checkNotNull(context);
		checkNotNull(input);

		// Context.
		final GqGroup group = context.getEncryptionGroup();
		final int phi = context.getMaximumNumberOfSelections();
		final int mu = context.getMaximumNumberOfWriteInOptionsPlusOne();
		final int delta = context.getNumberOfWriteInsPlusOne();
		final String ee = context.getElectionEventId();

		// Input.
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> pk_CCR = input.getCcrjChoiceReturnCodesEncryptionKeys();
		final GroupVector<GroupVector<SchnorrProof, ZqGroup>, ZqGroup> pi_pkCRR = input.getCcrjSchnorrProofs();
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> EL_pk = input.getCcmjElectionPublicKeys();
		final GroupVector<GroupVector<SchnorrProof, ZqGroup>, ZqGroup> pi_ELpk = input.getCcmjSchnorrProofs();
		final ElGamalMultiRecipientPublicKey EB_pk = input.getElectoralBoardPublicKey();
		final GroupVector<SchnorrProof, ZqGroup> pi_EB = input.getElectoralBoardSchnorrProofs();

		// Cross-checks.
		checkArgument(pk_CCR.getGroup().equals(group), "The context and input's public keys must have the same encryption group.");
		checkArgument(pk_CCR.getElementSize() == phi && pi_pkCRR.getElementSize() == phi,
				"The size of the CCRj Choice Return Codes encryption keys and Schnorr proofs must be equal to the maximum number of selections.");
		checkArgument(EL_pk.getElementSize() == mu && pi_ELpk.getElementSize() == mu,
				"The size of the CCMj election public keys and Schnorr proofs must be equal to the maximum number of write-in options + 1.");
		checkArgument(EB_pk.size() == delta && pi_EB.size() == delta,
				"The size of the electoral board public key and Schnorr proofs must be equal to the number of write-ins + 1.");

		// Require.
		checkArgument(delta <= mu && mu <= phi, "The maximum supported number of write-ins + 1 must be between the number of "
				+ "write-ins + 1 and the maximum number of selections. [mu: %s, delta: %s, phi: %s]", mu, delta, phi);

		// Operation.
		final boolean verifSchnorrCCR = NODE_IDS.stream()
				.parallel()
				.flatMap(j -> {
					final List<String> i_aux_CCR_j = List.of(ee, "GenKeysCCR", integerToString(j));
					return IntStream.range(0, phi)
							.parallel()
							.mapToObj(i -> {
								final boolean verifSchnorrCCR_j_i = zeroKnowledgeProof.verifySchnorrProof(pi_pkCRR.get(j - 1).get(i),
										pk_CCR.get(j - 1).get(i), i_aux_CCR_j); // Due to zero indexing.
								if (!verifSchnorrCCR_j_i) {
									LOGGER.error("The CCRj Schnorr proofs of knowledge are invalid. [j: {}, i:  {}]", j, i);
								}
								return verifSchnorrCCR_j_i;
							});
				}).reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		final boolean verifSchnorrCCM = NODE_IDS.stream()
				.parallel()
				.flatMap(j -> {
					final List<String> i_aux_CCM_j = List.of(ee, "SetupTallyCCM", integerToString(j));
					return IntStream.range(0, mu)
							.parallel()
							.mapToObj(i -> {
								final boolean verifSchnorrCCM_j_i = zeroKnowledgeProof.verifySchnorrProof(pi_ELpk.get(j - 1).get(i),
										EL_pk.get(j - 1).get(i), i_aux_CCM_j); // Due to zero indexing.
								if (!verifSchnorrCCM_j_i) {
									LOGGER.error("The CCMj Schnorr proofs of knowledge are invalid. [j: {}, i:  {}]", j, i);
								}
								return verifSchnorrCCM_j_i;
							});
				}).reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		final List<String> i_aux_EB = List.of(ee, "SetupTallyEB");
		final boolean verifSchnorrEB = IntStream.range(0, delta)
				.parallel()
				.mapToObj(i -> {
					final boolean verifSchnorrEB_i = zeroKnowledgeProof.verifySchnorrProof(pi_EB.get(i), EB_pk.get(i), i_aux_EB);
					if (!verifSchnorrEB_i) {
						LOGGER.error("The electoral board Schnorr proofs of knowledge are invalid. [i:  {}]", i);
					}
					return verifSchnorrEB_i;
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		return verifSchnorrCCR && verifSchnorrCCM && verifSchnorrEB;
	}
}