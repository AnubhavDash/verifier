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

import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.hasNoDuplicates;
import static ch.post.it.evoting.cryptoprimitives.utils.Validations.allEqual;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;

import ch.post.it.evoting.cryptoprimitives.domain.validations.Validations;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ExponentiationProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.PlaintextEqualityProof;
import ch.post.it.evoting.verifier.protocol.domain.ContextIds;
import ch.post.it.evoting.verifier.protocol.domain.EncryptedVerifiableVote;

/**
 * Regroups the input values needed by the VerifyVotingClientProofs algorithm.
 *
 * <ul>
 * <li>vc<sub>1</sub>, the control component’s list of confirmed verification card IDs. Not null.</li>
 * <li>E1<sub>1</sub>, the control component’s list of  encrypted, confirmed votes. Not null.</li>
 * <li>E1<sub>tilde</sub><sub>1</sub>, the control component’s list of exponentiated, encrypted, confirmed votes. Not null.</li>
 * <li>E2<sub>1</sub>, the control component’s list of encrypted, partial Choice Return Codes. Not null.</li>
 * <li>π<sub>Exp, 1</sub>, the control component’s list of exponentiation proofs. Not null.</li>
 * <li>π<sub>EqEnc, 1</sub>, the control component’s list of plaintext equality proofs. Not null.</li>
 * <li>KMap, the key-value map of the verification card public keys. Not null.</li>
 * <li>EL<sub>pk</sub>, the election public key. Not null.</li>
 * <li>pk<sub>CCR</sub>, the Choice Return Codes encryption public key. Not null.</li>
 * </ul>
 */
public class VerifyVotingClientProofsInput {

	private final List<String> confirmedVerificationCardIds;
	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> encryptedConfirmedVotes;
	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> exponentiatedEncryptedVotes;
	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> encryptedPartialChoiceReturnCodes;
	private final List<ExponentiationProof> exponentiationProofs;
	private final List<PlaintextEqualityProof> plaintextEqualityProofs;
	private final Map<String, ElGamalMultiRecipientPublicKey> verificationCardPublicKeys;
	private final ElGamalMultiRecipientPublicKey electionPublicKey;
	private final ElGamalMultiRecipientPublicKey choiceReturnCodesEncryptionPublicKey;

	private VerifyVotingClientProofsInput(final List<EncryptedVerifiableVote> encryptedVerifiableVotes,
			final Map<String, ElGamalMultiRecipientPublicKey> verificationCardPublicKeys,
			final ElGamalMultiRecipientPublicKey electionPublicKey,
			final ElGamalMultiRecipientPublicKey choiceReturnCodesEncryptionPublicKey) {
		this.confirmedVerificationCardIds = encryptedVerifiableVotes.stream()
				.map(encryptedVerifiableVote -> encryptedVerifiableVote.contextIds().verificationCardId()).toList();
		this.encryptedConfirmedVotes = encryptedVerifiableVotes.stream()
				.map(EncryptedVerifiableVote::encryptedVote).collect(GroupVector.toGroupVector());
		this.exponentiatedEncryptedVotes = encryptedVerifiableVotes.stream()
				.map(EncryptedVerifiableVote::exponentiatedEncryptedVote).collect(GroupVector.toGroupVector());
		this.encryptedPartialChoiceReturnCodes = encryptedVerifiableVotes.stream()
				.map(EncryptedVerifiableVote::encryptedPartialChoiceReturnCodes).collect(GroupVector.toGroupVector());
		this.exponentiationProofs = encryptedVerifiableVotes.stream()
				.map(EncryptedVerifiableVote::exponentiationProof).toList();
		this.plaintextEqualityProofs = encryptedVerifiableVotes.stream()
				.map(EncryptedVerifiableVote::plaintextEqualityProof).toList();
		this.verificationCardPublicKeys = verificationCardPublicKeys;
		this.electionPublicKey = electionPublicKey;
		this.choiceReturnCodesEncryptionPublicKey = choiceReturnCodesEncryptionPublicKey;
	}

	public List<String> getConfirmedVerificationCardIds() {
		return confirmedVerificationCardIds;
	}

	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getEncryptedConfirmedVotes() {
		return encryptedConfirmedVotes;
	}

	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getExponentiatedEncryptedVotes() {
		return exponentiatedEncryptedVotes;
	}

	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getEncryptedPartialChoiceReturnCodes() {
		return encryptedPartialChoiceReturnCodes;
	}

	public List<ExponentiationProof> getExponentiationProofs() {
		return exponentiationProofs;
	}

	public List<PlaintextEqualityProof> getPlaintextEqualityProofs() {
		return plaintextEqualityProofs;
	}

	public Map<String, ElGamalMultiRecipientPublicKey> getVerificationCardPublicKeys() {
		return verificationCardPublicKeys;
	}

	public ElGamalMultiRecipientPublicKey getElectionPublicKey() {
		return electionPublicKey;
	}

	public ElGamalMultiRecipientPublicKey getChoiceReturnCodesEncryptionPublicKey() {
		return choiceReturnCodesEncryptionPublicKey;
	}

	/**
	 * Builder performing input validations before constructing a {@link VerifyVotingClientProofsInput}.
	 */
	public static class Builder {

		private List<EncryptedVerifiableVote> encryptedVerifiableVotes;
		private Map<String, ElGamalMultiRecipientPublicKey> verificationCardPublicKeys;
		private ElGamalMultiRecipientPublicKey electionPublicKey;
		private ElGamalMultiRecipientPublicKey choiceReturnCodesEncryptionPublicKey;

		public Builder setEncryptedVerifiableVotes(final List<EncryptedVerifiableVote> encryptedVerifiableVotes) {
			this.encryptedVerifiableVotes = encryptedVerifiableVotes;
			return this;
		}

		public Builder setVerificationCardPublicKeys(final Map<String, ElGamalMultiRecipientPublicKey> verificationCardPublicKeys) {
			this.verificationCardPublicKeys = verificationCardPublicKeys;
			return this;
		}

		public Builder setElectionPublicKey(final ElGamalMultiRecipientPublicKey electionPublicKey) {
			this.electionPublicKey = electionPublicKey;
			return this;
		}

		public Builder setChoiceReturnCodesEncryptionPublicKey(
				final ElGamalMultiRecipientPublicKey choiceReturnCodesEncryptionPublicKey) {
			this.choiceReturnCodesEncryptionPublicKey = choiceReturnCodesEncryptionPublicKey;
			return this;
		}

		/**
		 * Creates a VerifyVotingClientProofsInput object.
		 *
		 * @throws NullPointerException     if any of the fields are null.
		 * @throws IllegalArgumentException if
		 *                                  <ul>
		 *                                      <li>any of the lists are empty.</li>
		 *                                      <li>any of the ids are invalid UUIDs.</li></li>
		 *                                      <li>the fields do not have the same group.</li>
		 *                                      <li>not all the control component's lists have the same size.</li>
		 *                                      <li>the size of KMap is strictly smaller than the size of the control component's lists.</li>
		 *                                      <li>the number of selectable voting options is strictly greater than the size of the Choice Return
		 *                                      	Codes encryption public key.</li>
		 *                                      <li>the number of allowed write-ins + 1 is strictly greater than the size of the election public key.</li>
		 *                                      <li>the size of the election public key is strictly greater than the size of the Choice Return Codes
		 *                                      	encryption public key.</li>
		 *                                  </ul>
		 */
		public VerifyVotingClientProofsInput build() {
			// Input null checks.
			checkNotNull(encryptedVerifiableVotes);
			encryptedVerifiableVotes.forEach(Preconditions::checkNotNull);
			checkArgument(!encryptedVerifiableVotes.isEmpty(), "The list of encrypted verifiable votes must not be empty.");
			checkNotNull(verificationCardPublicKeys);
			verificationCardPublicKeys.forEach(Preconditions::checkNotNull);
			checkNotNull(electionPublicKey);
			checkNotNull(choiceReturnCodesEncryptionPublicKey);

			final List<String> confirmedVerificationCardIdsCopy = encryptedVerifiableVotes.stream()
					.map(encryptedVerifiableVote -> encryptedVerifiableVote.contextIds().verificationCardId()).toList();
			final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> encryptedConfirmedVotesCopy = encryptedVerifiableVotes.stream()
					.map(EncryptedVerifiableVote::encryptedVote).collect(GroupVector.toGroupVector());
			final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> exponentiatedEncryptedVotesCopy = encryptedVerifiableVotes.stream()
					.map(EncryptedVerifiableVote::exponentiatedEncryptedVote).collect(GroupVector.toGroupVector());
			final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> encryptedPartialChoiceReturnCodesCopy = encryptedVerifiableVotes.stream()
					.map(EncryptedVerifiableVote::encryptedPartialChoiceReturnCodes).collect(GroupVector.toGroupVector());
			final List<ExponentiationProof> exponentiationProofsCopy = encryptedVerifiableVotes.stream()
					.map(EncryptedVerifiableVote::exponentiationProof).toList();
			final List<PlaintextEqualityProof> plaintextEqualityProofsCopy = encryptedVerifiableVotes.stream()
					.map(EncryptedVerifiableVote::plaintextEqualityProof).toList();
			final Map<String, ElGamalMultiRecipientPublicKey> verificationCardPublicKeysCopy = Map.copyOf(verificationCardPublicKeys);

			// Input empty checks.
			checkArgument(!confirmedVerificationCardIdsCopy.isEmpty(), "The list of confirmed verification card IDs must not be empty.");
			checkArgument(!encryptedConfirmedVotesCopy.isEmpty(), "The list of encrypted, confirmed votes must not be empty.");
			checkArgument(!exponentiatedEncryptedVotesCopy.isEmpty(), "The list of exponentiated, encrypted, confirmed votes must not be empty.");
			checkArgument(!encryptedPartialChoiceReturnCodesCopy.isEmpty(), "The list of encrypted, partial Choice Return Codes must not be empty.");
			checkArgument(!exponentiationProofsCopy.isEmpty(), "The list of plaintext equality proofs must not be empty.");
			checkArgument(!plaintextEqualityProofsCopy.isEmpty(), "The list of plaintext equality proofs must not be empty.");
			checkArgument(verificationCardPublicKeysCopy.size() > 0, "The map of verification card public keys must not be empty.");
			checkArgument(electionPublicKey.size() > 0, "The election public key must not be empty.");
			checkArgument(choiceReturnCodesEncryptionPublicKey.size() > 0, "The Choice Return Codes encryption public key must not be empty.");

			// Input ID checks.
			confirmedVerificationCardIdsCopy.forEach(Validations::validateUUID);
			checkArgument(hasNoDuplicates(confirmedVerificationCardIdsCopy),
					"The list of confirmed verification card IDs must not contain any duplicates.");
			verificationCardPublicKeysCopy.keySet().forEach(Validations::validateUUID);
			final String electionEventId = encryptedVerifiableVotes.get(0).contextIds().electionEventId();
			checkArgument(allEqual(encryptedVerifiableVotes.stream().map(EncryptedVerifiableVote::contextIds).map(ContextIds::electionEventId),
					e -> e.equals(electionEventId)), "All encrypted verifiable votes should be part of the same election.");

			// Input size checks.
			final List<Integer> inputsSize = Arrays.asList(confirmedVerificationCardIdsCopy.size(), encryptedConfirmedVotesCopy.size(),
					exponentiatedEncryptedVotesCopy.size(), encryptedPartialChoiceReturnCodesCopy.size(), exponentiationProofsCopy.size(),
					plaintextEqualityProofsCopy.size());
			checkArgument(allEqual(inputsSize.stream(), Function.identity()), "All control component's lists must be the same size. [N_C: %s]",
					inputsSize.get(0));
			exponentiatedEncryptedVotesCopy.forEach(
					exp -> checkArgument(exp.size() == 1, "The size of each exponentiated, encrypted, confirmed vote should be 1."));

			// Input GqGroup checks.
			final GqGroup gqGroup = encryptedConfirmedVotesCopy.get(0).getGroup();

			checkArgument(allEqual(Streams.concat(
							encryptedConfirmedVotesCopy.stream(),
							exponentiatedEncryptedVotesCopy.stream(),
							encryptedPartialChoiceReturnCodesCopy.stream(),
							exponentiationProofsCopy.stream(),
							plaintextEqualityProofsCopy.stream(),
							verificationCardPublicKeysCopy.values().stream(),
							Stream.of(electionPublicKey),
							Stream.of(choiceReturnCodesEncryptionPublicKey)),
					elt -> gqGroup.hasSameOrderAs(elt.getGroup())), "All input must have the same group.");

			// Requires.
			final int N_C = confirmedVerificationCardIdsCopy.size();
			final int N_E = verificationCardPublicKeysCopy.size();
			final int psi = encryptedPartialChoiceReturnCodesCopy.get(0).size();
			final int phi = choiceReturnCodesEncryptionPublicKey.size();
			final int delta = electionPublicKey.size();
			final int delta_hat = encryptedConfirmedVotesCopy.get(0).size();

			// At least one confirmed vote is present (N_C >= 1) because confirmedVerificationCardIds is not empty.
			checkArgument(N_E >= N_C, "N_E should be greater or equal to N_C. [N_E: %s, N_C: %s]", N_E, N_C);

			checkArgument(psi <= phi, "psi should be smaller or equal to phi. [psi: %s, phi: %s]", psi, phi);
			checkArgument(delta_hat > 0, "delta_hat should be strictly greater than 0. [delta_hat: %s]", delta_hat);
			checkArgument(delta_hat <= delta, "delta_hat should be smaller or equal to delta. [delta_hat: %s, delta: %s]", delta_hat, delta);
			checkArgument(delta <= phi, "delta should be smaller or equal to phi. [delta: %s, phi: %s]", delta, phi);

			return new VerifyVotingClientProofsInput(encryptedVerifiableVotes, verificationCardPublicKeys, electionPublicKey,
					choiceReturnCodesEncryptionPublicKey);
		}
	}
}
