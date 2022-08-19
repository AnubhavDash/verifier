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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence.algorithms;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.Stream;

import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ControlComponentCodeShare;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SetupComponentVerificationData;
import ch.post.it.evoting.cryptoprimitives.domain.validations.Validations;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ExponentiationProof;

/**
 * Regroups the input values needed by the VerifyEncryptedPCCExponentiationProofsVerificationCardSet algorithm.
 * <ul>
 * <li>vc, the {@code GroupVector} of verification card IDs. Not null and valid UUIDs.</li>
 * <li>c_pCC, the encrypted, hashed partial Choice Return Codes. Not null.</li>
 * <li>K_j, the Voter Choice Return Code Generation public keys. Not null.</li>
 * <li>c_expPCC_j, the exponentiated, encrypted, hashed partial Choice Return Codes. Not null.</li>
 * <li>pi_expPCC_j, the proofs of correct exponentiation. Not null.</li>
 * </ul>
 * <p>
 * And by the VerifyEncryptedCKExponentiationProofsVerificationCardSet algorithm.
 * <ul>
 * <li>vc, the {@code GroupVector} of verification card IDs. Not null and valid UUIDs.</li>
 * <li>c_ck, the encrypted, hashed Confirmation Key. Not null.</li>
 * <li>Kc_j, the Voter Vote Cast Return Code Generation public keys. Not null.</li>
 * <li>c_expCK_j, the exponentiated, encrypted, hashed Confirmation Key. Not null.</li>
 * <li>pi_expCK_j, the proofs of correct exponentiation. Not null.</li>
 * </ul>
 */
public class VerifyEncryptedExponentiationProofsVerificationCardSetInput {

	private final List<String> verificationCardIds;
	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> encryptedHashedPartialChoiceReturnCodes;
	private final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> voterChoiceReturnCodeGenerationPublicKeys;
	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> exponentiatedEncryptedHashedPartialChoiceReturnCodes;
	private final List<ExponentiationProof> proofsOfCorrectPCCExponentiation;
	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> encryptedHashedConfirmationKey;
	private final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> voterVoteCastReturnCodeGenerationPublicKeys;
	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> exponentiatedEncryptedHashedConfirmationKey;
	private final List<ExponentiationProof> proofsOfCorrectCKExponentiation;

	public VerifyEncryptedExponentiationProofsVerificationCardSetInput(final List<SetupComponentVerificationData> setupComponentVerificationData,
			final List<ControlComponentCodeShare> controlComponentCodeShares) {
		checkNotNull(setupComponentVerificationData);
		checkNotNull(controlComponentCodeShares);

		this.verificationCardIds = setupComponentVerificationData
				.stream()
				.map(SetupComponentVerificationData::verificationCardId)
				.toList();

		this.encryptedHashedPartialChoiceReturnCodes = setupComponentVerificationData
				.stream()
				.map(SetupComponentVerificationData::encryptedHashedSquaredPartialChoiceReturnCodes)
				.collect(GroupVector.toGroupVector());

		this.voterChoiceReturnCodeGenerationPublicKeys = controlComponentCodeShares
				.stream()
				.map(ControlComponentCodeShare::voterChoiceReturnCodeGenerationPublicKey)
				.collect(GroupVector.toGroupVector());

		this.exponentiatedEncryptedHashedPartialChoiceReturnCodes = controlComponentCodeShares
				.stream()
				.map(ControlComponentCodeShare::exponentiatedEncryptedPartialChoiceReturnCodes)
				.collect(GroupVector.toGroupVector());

		this.proofsOfCorrectPCCExponentiation = controlComponentCodeShares
				.stream()
				.map(ControlComponentCodeShare::encryptedPartialChoiceReturnCodeExponentiationProof)
				.collect(GroupVector.toGroupVector());

		this.encryptedHashedConfirmationKey = setupComponentVerificationData
				.stream()
				.map(SetupComponentVerificationData::encryptedHashedSquaredConfirmationKey)
				.collect(GroupVector.toGroupVector());

		this.voterVoteCastReturnCodeGenerationPublicKeys = controlComponentCodeShares
				.stream()
				.map(ControlComponentCodeShare::voterVoteCastReturnCodeGenerationPublicKey)
				.collect(GroupVector.toGroupVector());

		this.exponentiatedEncryptedHashedConfirmationKey = controlComponentCodeShares
				.stream()
				.map(ControlComponentCodeShare::exponentiatedEncryptedConfirmationKey)
				.collect(GroupVector.toGroupVector());

		this.proofsOfCorrectCKExponentiation = controlComponentCodeShares
				.stream()
				.map(ControlComponentCodeShare::encryptedConfirmationKeyExponentiationProof)
				.collect(GroupVector.toGroupVector());

		checkNotNull(verificationCardIds);
		verificationCardIds.forEach(Validations::validateUUID);
		checkNotNull(encryptedHashedPartialChoiceReturnCodes);
		checkNotNull(voterChoiceReturnCodeGenerationPublicKeys);
		checkNotNull(exponentiatedEncryptedHashedPartialChoiceReturnCodes);
		checkNotNull(proofsOfCorrectPCCExponentiation);

		checkArgument(Stream.of(verificationCardIds.size(), encryptedHashedPartialChoiceReturnCodes.size(),
				voterChoiceReturnCodeGenerationPublicKeys.size(), exponentiatedEncryptedHashedPartialChoiceReturnCodes.size(),
				proofsOfCorrectPCCExponentiation.size(), encryptedHashedConfirmationKey.size(),
				voterVoteCastReturnCodeGenerationPublicKeys.size(), exponentiatedEncryptedHashedConfirmationKey.size(),
				proofsOfCorrectCKExponentiation.size()).distinct().count() == 1, "All input elements need to have the same size.");

		checkArgument(
				encryptedHashedPartialChoiceReturnCodes.getElementSize() == exponentiatedEncryptedHashedPartialChoiceReturnCodes.getElementSize(),
				"The size of each encrypted, hashed partial Choice Return Codes should be equal to the size of each exponentiated, encrypted, hashed partial Choice Return Codes.");

		checkNotNull(encryptedHashedConfirmationKey);
		checkNotNull(voterVoteCastReturnCodeGenerationPublicKeys);
		checkNotNull(exponentiatedEncryptedHashedConfirmationKey);
		checkNotNull(proofsOfCorrectPCCExponentiation);

		checkArgument(Stream.of(verificationCardIds.size(), encryptedHashedConfirmationKey.size(),
				voterVoteCastReturnCodeGenerationPublicKeys.size(), exponentiatedEncryptedHashedConfirmationKey.size(),
				proofsOfCorrectPCCExponentiation.size()).distinct().count() == 1, "All input elements need to have the same size.");

		checkArgument(
				encryptedHashedConfirmationKey.getElementSize() == exponentiatedEncryptedHashedConfirmationKey.getElementSize(),
				"The size of each encrypted, hashed Confirmation Key should be equal to the size of each exponentiated, encrypted, hashed Confirmation Key.");
	}

	public List<String> getVerificationCardIds() {
		return verificationCardIds;
	}

	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getEncryptedHashedPartialChoiceReturnCodes() {
		return encryptedHashedPartialChoiceReturnCodes;
	}

	public GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> getVoterChoiceReturnCodeGenerationPublicKeys() {
		return voterChoiceReturnCodeGenerationPublicKeys;
	}

	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getExponentiatedEncryptedHashedPartialChoiceReturnCodes() {
		return exponentiatedEncryptedHashedPartialChoiceReturnCodes;
	}

	public List<ExponentiationProof> getProofsOfCorrectPCCExponentiation() {
		return proofsOfCorrectPCCExponentiation;
	}

	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getEncryptedHashedConfirmationKey() {
		return encryptedHashedConfirmationKey;
	}

	public GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> getVoterVoteCastReturnCodeGenerationPublicKeys() {
		return voterVoteCastReturnCodeGenerationPublicKeys;
	}

	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getExponentiatedEncryptedHashedConfirmationKey() {
		return exponentiatedEncryptedHashedConfirmationKey;
	}

	public List<ExponentiationProof> getProofsOfCorrectCKExponentiation() {
		return proofsOfCorrectCKExponentiation;
	}
}
