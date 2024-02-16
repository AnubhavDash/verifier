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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.hasNoDuplicates;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.VerifiableDecryptions;
import ch.post.it.evoting.evotinglibraries.domain.common.ContextIds;
import ch.post.it.evoting.evotinglibraries.domain.common.EncryptedVerifiableVote;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.votingoptions.PrimesMappingTableAlgorithms;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyMixDecInput;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyMixDecOfflineAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyMixDecOfflineContext;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyVotingClientProofsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyVotingClientProofsContext;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyVotingClientProofsInput;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixonline.GetMixnetInitialCiphertextsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixonline.GetMixnetInitialCiphertextsContext;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixonline.GetMixnetInitialCiphertextsInput;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixonline.GetMixnetInitialCiphertextsOutput;

/**
 * Implements the VerifyOnlineControlComponentsBallotBox algorithm.
 */
@Service
public class VerifyOnlineControlComponentsBallotBoxAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyOnlineControlComponentsBallotBoxAlgorithm.class);

	private final VerifyMixDecOfflineAlgorithm verifyMixDecOfflineAlgorithm;
	private final VerifyVotingClientProofsAlgorithm verifyVotingClientProofsAlgorithm;
	private final GetMixnetInitialCiphertextsAlgorithm getMixnetInitialCiphertextsAlgorithm;
	private final PrimesMappingTableAlgorithms primesMappingTableAlgorithms;

	public VerifyOnlineControlComponentsBallotBoxAlgorithm(
			final VerifyMixDecOfflineAlgorithm verifyMixDecOfflineAlgorithm,
			final VerifyVotingClientProofsAlgorithm verifyVotingClientProofsAlgorithm,
			final GetMixnetInitialCiphertextsAlgorithm getMixnetInitialCiphertextsAlgorithm,
			final PrimesMappingTableAlgorithms primesMappingTableAlgorithms) {
		this.verifyMixDecOfflineAlgorithm = verifyMixDecOfflineAlgorithm;
		this.verifyVotingClientProofsAlgorithm = verifyVotingClientProofsAlgorithm;
		this.getMixnetInitialCiphertextsAlgorithm = getMixnetInitialCiphertextsAlgorithm;
		this.primesMappingTableAlgorithms = primesMappingTableAlgorithms;
	}

	/**
	 * Verifies the proofs of the OnlineControlComponentBallotBoxes.
	 * <p>
	 * Calls the verifyVotingClientProofs and verifyMixDecOffline algorithms.
	 * </p>
	 *
	 * @param context the context as a {@link VerifyOnlineControlComponentsBallotBoxContext}. Must be non-null.
	 * @param input   the input as a {@link VerifyOnlineControlComponentsBallotBoxInput}. Must be non-null.
	 * @return {@code true} if all proofs verify, {@code false} otherwise
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyOnlineControlComponentsBallotBox(
			final VerifyOnlineControlComponentsBallotBoxContext context, final VerifyOnlineControlComponentsBallotBoxInput input) {
		checkNotNull(context);
		checkNotNull(input);

		// Cross-group check.
		checkArgument(context.getEncryptionGroup().equals(input.getEncryptionGroup()),
				"The context and input must have the same encryption group.");

		// Context.
		final GqGroup p_q_g = context.getEncryptionGroup();
		final String ee = context.getElectionEventId();
		final String vcs = context.getVerificationCardSetId();
		final String bb = context.getBallotBoxId();
		final int N_E = context.getNumberOfEligibleVoters();
		final PrimesMappingTable pTable = context.getPrimesMappingTable();
		final int psi = primesMappingTableAlgorithms.getPsi(pTable);
		final int delta = primesMappingTableAlgorithms.getDelta(pTable);
		final ElGamalMultiRecipientPublicKey EL_pk = context.getElectionPublicKey();
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> EL_pk_1_to_4 = context.getCcmElectionPublicKeys();
		final ElGamalMultiRecipientPublicKey EB_pk = context.getElectoralBoardPublicKey();
		final ElGamalMultiRecipientPublicKey pk_CCR = context.getChoiceReturnCodesEncryptionPublicKey();

		// Input.
		// controlComponentsLists corresponds to (vc_1, E1_1, E1_tilde_1, E2_1, pi_Exp_1, pi_EqEnc_1)
		final List<EncryptedVerifiableVote> controlComponentsLists = input.getControlComponentsLists();
		// c_mix_j_pi_mix_j contains both the preceding shuffled votes and shuffle proofs
		final List<VerifiableShuffle> c_mix_j_pi_mix_j = input.getPrecedingShuffle();
		// c_mix_j_pi_mix_j contains both the preceding shuffled votes and shuffle proofs
		final List<VerifiableDecryptions> c_dec_j_pi_dec_j = input.getPrecedingPartialDecryptions();
		final List<String> vc_vector = input.getVerificationCardIds();
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> K_vector = input.getVerificationCardPublicKeys();
		final int N_C = controlComponentsLists.size();
		final int N_C_hat = c_mix_j_pi_mix_j.get(0).shuffledCiphertexts().size();

		// Cross-checks.
		checkArgument(K_vector.size() == N_E,
				"The size of the verification card public keys must be equal to the number of eligible voters. [KMap_size: %s, N_E: %s]",
				K_vector.size(), N_E);
		checkArgument(c_mix_j_pi_mix_j.get(0).shuffledCiphertexts().getElementSize() == delta,
				"The element size of the shuffled and the partially decrypted votes must correspond to the number of allowed write ins + 1. [l: %s, delta: %s]",
				c_mix_j_pi_mix_j.get(0).shuffledCiphertexts().getElementSize(), delta);
		checkArgument(controlComponentsLists.isEmpty() || controlComponentsLists.get(0).encryptedPartialChoiceReturnCodes().size() == psi,
				"The size of the encrypted, partial Choice Return Codes must be equal to the number of selections. [psi: %s]", psi);

		// Require.
		checkArgument(N_E >= N_C, "The the number of eligible voters must be greater or equal to the number of confirmed votes.");
		checkArgument(N_C >= 2 ? N_C_hat == N_C : N_C_hat == N_C + 2, "The number of mixed votes must be equal to the number of processed votes, "
				+ "if the number of confirmed votes is 2 or greater. Otherwise, there must be two more mixed votes than confirmed votes (for N_C = 0 or 1).");
		final List<String> vc_1 = controlComponentsLists.stream().parallel()
				.map(EncryptedVerifiableVote::contextIds)
				.map(ContextIds::verificationCardId)
				.toList();
		checkArgument(hasNoDuplicates(vc_1), "The verification card IDs must not contain duplicates.");

		// Operation.
		final boolean vcProofsVerif;
		if (N_C >= 1) {
			final Map<String, ElGamalMultiRecipientPublicKey> KMap = IntStream.range(0, vc_vector.size())
					.boxed()
					.collect(Collectors.toMap(vc_vector::get, K_vector::get));

			final VerifyVotingClientProofsContext verifyVotingClientProofsContext = new VerifyVotingClientProofsContext.Builder()
					.setEncryptionGroup(p_q_g)
					.setElectionEventId(ee)
					.setVerificationCardSetId(vcs)
					.setPrimesMappingTable(pTable)
					.setNumberOfEligibleVoters(N_E)
					.setElectionPublicKey(EL_pk)
					.setChoiceReturnCodesEncryptionPublicKey(pk_CCR)
					.build();
			vcProofsVerif = verifyVotingClientProofsAlgorithm.verifyVotingClientProofs(verifyVotingClientProofsContext,
					new VerifyVotingClientProofsInput(controlComponentsLists, KMap));

			if (vcProofsVerif) {
				LOGGER.info("The result of the verifyVotingClientProofs is successful. [bb: {}", bb);
			} else {
				LOGGER.error("The result of the verifyVotingClientProofs is unsuccessful. [bb: {}", bb);
			}
		} else {
			vcProofsVerif = true;
		}

		final Map<String, ElGamalMultiRecipientCiphertext> vcMap_1 = controlComponentsLists.stream().parallel()
				.collect(Collectors.toMap(
						encryptedVerifiableVote -> encryptedVerifiableVote.contextIds().verificationCardId(),
						EncryptedVerifiableVote::encryptedVote)
				);

		final GetMixnetInitialCiphertextsContext getMixnetInitialCiphertextsContext = new GetMixnetInitialCiphertextsContext(p_q_g, N_E, delta,
				EL_pk);
		final GetMixnetInitialCiphertextsOutput getMixnetInitialCiphertextsOutput = getMixnetInitialCiphertextsAlgorithm.getMixnetInitialCiphertexts(
				getMixnetInitialCiphertextsContext, new GetMixnetInitialCiphertextsInput(vcMap_1));
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_init_1 = getMixnetInitialCiphertextsOutput.mixnetInitialCiphertexts();

		final VerifyMixDecOfflineContext verifyMixDecOfflineContext = new VerifyMixDecOfflineContext(p_q_g, ee, bb, delta, EL_pk, EL_pk_1_to_4,
				EB_pk);
		final boolean shuffleProofsVerif = verifyMixDecOfflineAlgorithm.verifyMixDecOffline(verifyMixDecOfflineContext,
				new VerifyMixDecInput(c_init_1, c_mix_j_pi_mix_j, c_dec_j_pi_dec_j));

		if (shuffleProofsVerif) {
			LOGGER.info("The result of the verifyMixDecOffline is successful. [ee: {}, bb: {}", ee, bb);
		} else {
			LOGGER.error("The result of the verifyMixDecOffline is unsuccessful. [ee: {}, bb: {}", ee, bb);
		}

		return vcProofsVerif && shuffleProofsVerif;
	}

}
