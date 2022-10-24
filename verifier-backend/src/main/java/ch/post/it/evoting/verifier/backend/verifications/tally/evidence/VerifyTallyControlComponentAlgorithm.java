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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.google.common.collect.MoreCollectors;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.VerifiablePlaintextDecryption;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.verifier.protocol.domain.tally.TallyComponentVotesPayload;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.Results;

@Service
public class VerifyTallyControlComponentAlgorithm {

	private final VerifyTallyControlComponentBallotBoxAlgorithm verifyTallyControlComponentBallotBoxAlgorithm;
	private final VerifyTallyFilesAlgorithm verifyTallyFilesAlgorithm;

	public VerifyTallyControlComponentAlgorithm(final VerifyTallyControlComponentBallotBoxAlgorithm verifyTallyControlComponentBallotBoxAlgorithm,
			final VerifyTallyFilesAlgorithm verifyTallyFilesAlgorithm) {
		this.verifyTallyControlComponentBallotBoxAlgorithm = verifyTallyControlComponentBallotBoxAlgorithm;
		this.verifyTallyFilesAlgorithm = verifyTallyFilesAlgorithm;
	}

	/**
	 * Verifies the Tally control component’s operations.
	 *
	 * @param input                           the input for the VerifyTallyControlComponent algorithm. Not null.
	 * @param numberOfSelectableVotingOptions the number of selectable voting options grouped by ballot box id.
	 * @return true if the operations are valid for all ballot boxes, false otherwise.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyTallyControlComponent(final VerifyTallyControlComponentInput input,
			final Map<String, Integer> numberOfSelectableVotingOptions) {
		checkNotNull(input);
		checkNotNull(numberOfSelectableVotingOptions);

		// Input.
		final GqGroup encryptionGroup = input.getEncryptionGroup();
		final String ee = input.getElectionEventId();
		final List<String> bb = input.getBallotBoxIds();
		final List<ControlComponentShufflePayload> lastOnlineControlComponentShuffles = input.getLastOnlineControlComponentShuffles();
		final List<TallyComponentVotesPayload> tallyControlComponentVotes = input.getTallyControlComponentVotes();
		final List<TallyComponentShufflePayload> tallyControlComponentShuffles = input.getTallyControlComponentShuffles();
		final ElectionEventContext electionEventContext = input.getElectionEventContext();
		final Configuration electionEventConfiguration = input.getElectionEventConfiguration();
		final Results tallyControlComponentDecryptions = input.getTallyControlComponentDecryptions();
		final Delivery tallyControlComponentResults = input.getTallyControlComponentResults();
		final ch.ech.xmlns.ech_0222._1.Delivery tallyComponentEch0222 = input.getTallyComponentEch0222();
		final Map<String, List<List<String>>> allSelectedDecodedVotingOptions = input.getAllSelectedDecodedVotingOptions();

		// Cross-checks.
		checkArgument(bb.size() == numberOfSelectableVotingOptions.keySet().size(),
				"There must be a number of selectable voting options for each ballot box.");
		checkArgument(new HashSet<>(bb).equals(numberOfSelectableVotingOptions.keySet()),
				"The number of selectable voting options must be for the input's ballot boxes.");

		// Operation.
		final int N_bb = bb.size();
		final boolean tallyVerif = IntStream.range(0, N_bb)
				.parallel()
				.mapToObj(i -> {
					final String bb_i = bb.get(i);

					final VerificationCardSetContext verificationCardSetContext = electionEventContext.verificationCardSetContexts().stream()
							.filter(vcsContext -> vcsContext.ballotBoxId().equals(bb_i))
							.collect(MoreCollectors.onlyElement());
					final VerifyTallyControlComponentBallotBoxContext context_bb_i = new VerifyTallyControlComponentBallotBoxContext(encryptionGroup,
							ee, bb_i, electionEventContext.electoralBoardPublicKey(), verificationCardSetContext.primesMappingTable(),
							numberOfSelectableVotingOptions.get(bb_i), verificationCardSetContext.numberOfWriteInFields() + 1);

					final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> c_dec_4 = lastOnlineControlComponentShuffles.get(i)
							.getVerifiableDecryptions()
							.getCiphertexts();

					final TallyComponentShufflePayload tallyComponentShufflePayload = tallyControlComponentShuffles.get(i);
					final VerifiableShuffle c_mix_5_pi_mix_5 = tallyComponentShufflePayload.getVerifiableShuffle();
					final VerifiablePlaintextDecryption m_pi_dec_5 = tallyComponentShufflePayload.getVerifiablePlaintextDecryption();

					final TallyComponentVotesPayload tallyControlComponentVote = tallyControlComponentVotes.get(i);
					final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> L_votes = tallyControlComponentVote.getVotes().stream()
							.map(list -> list.stream()
									.map(p -> PrimeGqElement.PrimeGqElementFactory.fromValue(p.getValueAsInt(), encryptionGroup))
									.collect(GroupVector.toGroupVector()))
							.collect(GroupVector.toGroupVector());
					final List<List<String>> L_decodedVotes = tallyControlComponentVote.getActualSelectedVotingOptions();

					final VerifyTallyControlComponentBallotBoxInput input_bb_i = new VerifyTallyControlComponentBallotBoxInput.Builder()
							.setSelectedEncodedVotingOptions(L_votes)
							.setSelectedDecodedVotingOptions(L_decodedVotes)
							.setPreviousPartiallyDecryptedVotes(c_dec_4)
							.setVerifiableShuffle(c_mix_5_pi_mix_5)
							.setVerifiablePlaintextDecryption(m_pi_dec_5)
							.build();

					return verifyTallyControlComponentBallotBoxAlgorithm.verifyTallyControlComponentBallotBox(context_bb_i, input_bb_i);
				})
				.reduce(Boolean::logicalAnd)
				.orElseThrow();

		final VerifyTallyFilesInput verifyTallyFilesInput = new VerifyTallyFilesInput.Builder()
				.setupComponentConfig(electionEventConfiguration)
				.setTallyComponentDecrypt(tallyControlComponentDecryptions)
				.setTallyComponentEch0110(tallyControlComponentResults)
				.setTallyComponentEch0222(tallyComponentEch0222)
				.setAllSelectedDecodedVotingOptions(allSelectedDecodedVotingOptions)
				.build();
		final boolean tallyFilesVerif = verifyTallyFilesAlgorithm.verifyTallyFiles(ee, verifyTallyFilesInput);

		return tallyVerif && tallyFilesVerif;
	}
}
