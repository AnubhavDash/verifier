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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ExponentiationProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.PlaintextEqualityProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProof;

/**
 * Implements the VerifyVotingClientProofs algorithm.
 */
public class VerifyVotingClientProofsAlgorithm {

	private final ZeroKnowledgeProof zeroKnowledgeProof;

	public VerifyVotingClientProofsAlgorithm(final ZeroKnowledgeProof zeroKnowledgeProof) {
		this.zeroKnowledgeProof = zeroKnowledgeProof;
	}

	/**
	 * Verifies the voting client proofs.
	 *
	 * @param context the context data.
	 * @param input   the input data.
	 * @return true if the proofs are valid, false otherwise.
	 * @throws NullPointerException if {@code VerifyVotingClientProofsContext} or {@code VerifyVotingClientProofsInput} is null.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyVotingClientProofs(final VerifyVotingClientProofsContext context, final VerifyVotingClientProofsInput input) {
		checkNotNull(context);
		checkNotNull(input);

		// Variables.
		final GqGroup gqGroup = input.getElectionPublicKey().getGroup();
		final GqElement g = gqGroup.getGenerator();
		final String ee = context.getElectionEventId();

		final List<String> vc_1 = input.getConfirmedVerificationCardIds();
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> E1_1 = input.getEncryptedConfirmedVotes();
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> E1_tilde_1 = input.getExponentiatedEncryptedVotes();
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> E2_1 = input.getEncryptedPartialChoiceReturnCodes();
		final List<ExponentiationProof> pi_Exp_1 = input.getExponentiationProofs();
		final List<PlaintextEqualityProof> pi_EqEnc_1 = input.getPlaintextEqualityProofs();
		final Map<String, ElGamalMultiRecipientPublicKey> KMap = input.getVerificationCardPublicKeys();
		final ElGamalMultiRecipientPublicKey EL_pk = input.getElectionPublicKey();
		final ElGamalMultiRecipientPublicKey pk_CCR = input.getChoiceReturnCodesEncryptionPublicKey();

		final int psi = context.getNumberOfSelectableVotingOptions();
		checkArgument(E2_1.get(0).size() == psi, "The size of each encrypted, partial Choice Return Code should be equal to psi. [psi: %s]", psi);

		final int delta_hat = context.getNumberOfAllowedWriteInsPlusOne();
		checkArgument(E1_1.get(0).size() == delta_hat, "The size of each encrypted, confirmed vote should be equal to delta_hat. [delta_hat: %s]",
				delta_hat);

		// N_E >= N_C >= 1 is ensured by the input builder.
		// psi <= phi is ensured by the input builder.
		// 0 < delta_hat <= delta <= phi is ensured by the input builder.
		// Disinctness of all verification card IDS ensured by the input builder.

		final List<Boolean> verifExp = new ArrayList<>();
		final List<Boolean> verifEqEnc = new ArrayList<>();

		// Operation.
		final int N_C = vc_1.size();
		final GqElement identity = GqElement.GqElementFactory.fromValue(BigInteger.ONE, gqGroup);
		final GqElement pk_CCR_tilde = pk_CCR.stream().sequential().limit(psi).reduce(identity, GqElement::multiply);

		for (int i = 0; i < N_C; i++) {

			final String vc_1_i = vc_1.get(i);
			final GqElement K_id = KMap.get(vc_1_i).getKeyElements().get(0);

			final ElGamalMultiRecipientCiphertext E1_1_i = E1_1.get(i);

			final ElGamalMultiRecipientCiphertext E1_tilde_1_i = E1_tilde_1.get(i);

			final ElGamalMultiRecipientCiphertext E2_1_i = E2_1.get(i);
			final GqElement gamma_2 = E2_1_i.getGamma();
			final ElGamalMultiRecipientCiphertext E2_tilde_i = ElGamalMultiRecipientCiphertext.create(gamma_2,
					List.of(E2_1_i.getPhis().stream().sequential().limit(psi).reduce(identity, GqElement::multiply)));

			final List<String> i_aux = Streams.concat(
					Stream.of("CreateVote"),
					Stream.of(ee, vc_1_i),
					EL_pk.stream().map(EL_pk_i -> integerToString(EL_pk_i.getValue())),
					E1_1_i.getPhis().stream().map(phi_1_i -> integerToString(phi_1_i.getValue()))
			).toList();

			final GqElement gamma_1 = E1_1_i.getGamma();
			final GqElement Phi_1_0 = E1_1_i.get(0);
			final GroupVector<GqElement, GqGroup> bases = GroupVector.of(g, gamma_1, Phi_1_0);

			final GqElement gamma_1_k_id = E1_tilde_1_i.getGamma();
			final GqElement Phi_1_0_k_id = E1_tilde_1_i.get(0);
			final GroupVector<GqElement, GqGroup> exponentiations = GroupVector.of(K_id, gamma_1_k_id, Phi_1_0_k_id);

			final ExponentiationProof pi_Exp_1_i = pi_Exp_1.get(i);
			verifExp.add(zeroKnowledgeProof.verifyExponentiation(bases, exponentiations, pi_Exp_1_i, i_aux));

			final GqElement EL_pk_0 = EL_pk.get(0);
			final PlaintextEqualityProof pi_EqEnc_1_i = pi_EqEnc_1.get(i);
			verifEqEnc.add(zeroKnowledgeProof.verifyPlaintextEquality(E1_tilde_1_i, E2_tilde_i, EL_pk_0, pk_CCR_tilde, pi_EqEnc_1_i, i_aux));
		}

		return IntStream.range(0, N_C).allMatch(i -> verifExp.get(i) && verifEqEnc.get(i));
	}

}
