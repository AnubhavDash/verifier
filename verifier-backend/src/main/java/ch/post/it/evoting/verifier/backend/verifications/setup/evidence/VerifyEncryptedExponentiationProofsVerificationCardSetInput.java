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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.Stream;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.ZqGroup;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ExponentiationProof;
import ch.post.it.evoting.evotinglibraries.domain.returncodes.ControlComponentCodeShare;
import ch.post.it.evoting.evotinglibraries.domain.returncodes.SetupComponentVerificationData;
import ch.post.it.evoting.evotinglibraries.domain.validations.Validations;

/**
 * Regroups the input values needed by the VerifyEncryptedPCCExponentiationProofsVerificationCardSet algorithm.
 * <ul>
 * <li>vc, the {@code GroupVector} of verification card IDs. Not null and valid UUIDs.</li>
 * <li>c<sub>pCC</sub>, the encrypted, hashed partial Choice Return Codes. Not null.</li>
 * <li>K<sub>j</sub>, the Voter Choice Return Code Generation public keys. Not null.</li>
 * <li>c<sub>expPCC,j</sub>, the exponentiated, encrypted, hashed partial Choice Return Codes. Not null.</li>
 * <li>pi<sub>expPCC,j</sub>, the proofs of correct exponentiation. Not null.</li>
 * </ul>
 * <p>
 * And by the VerifyEncryptedCKExponentiationProofsVerificationCardSet algorithm.
 * <ul>
 * <li>vc, the {@code GroupVector} of verification card IDs. Not null and valid UUIDs.</li>
 * <li>c<sub>ck</sub>, the encrypted, hashed Confirmation Key. Not null.</li>
 * <li>Kc<sub>j</sub>, the Voter Vote Cast Return Code Generation public keys. Not null.</li>
 * <li>c<sub>expCK,j</sub>, the exponentiated, encrypted, hashed Confirmation Key. Not null.</li>
 * <li>pi<sub>expCK,j</sub>, the proofs of correct exponentiation. Not null.</li>
 * </ul>
 */
public class VerifyEncryptedExponentiationProofsVerificationCardSetInput {

	private final List<String> verificationCardIds;
	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> encryptedHashedPartialChoiceReturnCodes;
	private final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> voterChoiceReturnCodeGenerationPublicKeys;
	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> exponentiatedEncryptedHashedPartialChoiceReturnCodes;
	private final GroupVector<ExponentiationProof, ZqGroup> proofsOfCorrectPCCExponentiation;
	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> encryptedHashedConfirmationKey;
	private final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> voterVoteCastReturnCodeGenerationPublicKeys;
	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> exponentiatedEncryptedHashedConfirmationKey;
	private final GroupVector<ExponentiationProof, ZqGroup> proofsOfCorrectCKExponentiation;

	public VerifyEncryptedExponentiationProofsVerificationCardSetInput(final List<SetupComponentVerificationData> setupComponentVerificationData,
			final List<ControlComponentCodeShare> controlComponentCodeShares) {
		checkNotNull(setupComponentVerificationData);
		checkNotNull(controlComponentCodeShares);

		verificationCardIds = setupComponentVerificationData
				.stream()
				.map(SetupComponentVerificationData::verificationCardId)
				.toList();
		encryptedHashedPartialChoiceReturnCodes = setupComponentVerificationData
				.stream()
				.map(SetupComponentVerificationData::encryptedHashedSquaredPartialChoiceReturnCodes)
				.collect(GroupVector.toGroupVector());
		voterChoiceReturnCodeGenerationPublicKeys = controlComponentCodeShares
				.stream()
				.map(ControlComponentCodeShare::voterChoiceReturnCodeGenerationPublicKey)
				.collect(GroupVector.toGroupVector());
		exponentiatedEncryptedHashedPartialChoiceReturnCodes = controlComponentCodeShares
				.stream()
				.map(ControlComponentCodeShare::exponentiatedEncryptedPartialChoiceReturnCodes)
				.collect(GroupVector.toGroupVector());
		proofsOfCorrectPCCExponentiation = controlComponentCodeShares
				.stream()
				.map(ControlComponentCodeShare::encryptedPartialChoiceReturnCodeExponentiationProof)
				.collect(GroupVector.toGroupVector());
		encryptedHashedConfirmationKey = setupComponentVerificationData
				.stream()
				.map(SetupComponentVerificationData::encryptedHashedSquaredConfirmationKey)
				.collect(GroupVector.toGroupVector());
		voterVoteCastReturnCodeGenerationPublicKeys = controlComponentCodeShares
				.stream()
				.map(ControlComponentCodeShare::voterVoteCastReturnCodeGenerationPublicKey)
				.collect(GroupVector.toGroupVector());
		exponentiatedEncryptedHashedConfirmationKey = controlComponentCodeShares
				.stream()
				.map(ControlComponentCodeShare::exponentiatedEncryptedConfirmationKey)
				.collect(GroupVector.toGroupVector());
		proofsOfCorrectCKExponentiation = controlComponentCodeShares
				.stream()
				.map(ControlComponentCodeShare::encryptedConfirmationKeyExponentiationProof)
				.collect(GroupVector.toGroupVector());

		checkNotNull(verificationCardIds);
		verificationCardIds.forEach(Validations::validateUUID);

		validateInput(encryptedHashedPartialChoiceReturnCodes, voterChoiceReturnCodeGenerationPublicKeys,
				exponentiatedEncryptedHashedPartialChoiceReturnCodes, proofsOfCorrectPCCExponentiation);
		validateInput(encryptedHashedConfirmationKey, voterVoteCastReturnCodeGenerationPublicKeys, exponentiatedEncryptedHashedConfirmationKey,
				proofsOfCorrectCKExponentiation);

		checkArgument(
				encryptedHashedPartialChoiceReturnCodes.getElementSize() == exponentiatedEncryptedHashedPartialChoiceReturnCodes.getElementSize(),
				"The size of each encrypted, hashed partial Choice Return Codes should be equal to the size of each "
						+ "exponentiated, encrypted, hashed partial Choice Return Codes.");
	}

	private void validateInput(final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> encryptedHashedCiphertexts,
			final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> voterReturnCodeGenerationPublicKeys,
			final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> exponentiatedEncryptedHashedCiphertexts,
			final GroupVector<ExponentiationProof, ZqGroup> proofsOfCorrectExponentiation) {
		checkNotNull(encryptedHashedCiphertexts);
		checkNotNull(voterReturnCodeGenerationPublicKeys);
		checkNotNull(exponentiatedEncryptedHashedCiphertexts);
		checkNotNull(proofsOfCorrectExponentiation);

		checkArgument(Stream.of(encryptedHashedCiphertexts.getGroup(), voterReturnCodeGenerationPublicKeys.getGroup(),
						exponentiatedEncryptedHashedCiphertexts.getGroup()).distinct().count() == 1,
				"All input elements must have the same encryption group.");
		checkArgument(proofsOfCorrectExponentiation.getGroup().hasSameOrderAs(encryptedHashedCiphertexts.getGroup()),
				"The group of the proofs of correct exponentiation must have the same order as the input's encryption group.");

		checkArgument(Stream.of(verificationCardIds.size(), encryptedHashedCiphertexts.size(), voterReturnCodeGenerationPublicKeys.size(),
						exponentiatedEncryptedHashedCiphertexts.size(), proofsOfCorrectExponentiation.size()).distinct().count() == 1,
				"All input elements must have the same size.");
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

	public GroupVector<ExponentiationProof, ZqGroup> getProofsOfCorrectPCCExponentiation() {
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

	public GroupVector<ExponentiationProof, ZqGroup> getProofsOfCorrectCKExponentiation() {
		return proofsOfCorrectCKExponentiation;
	}
}
