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

import static com.google.common.base.Preconditions.checkNotNull;

import ch.post.it.evoting.cryptoprimitives.domain.election.ControlComponentPublicKeys;
import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.ZqGroup;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.SchnorrProof;

/**
 * Regroups the input values needed by the VerifyKeyGenerationSchnorrProofs algorithm.
 *
 * <ul>
 * <li>pk<sub>CCR</sub>, the CCR<sub>j</sub> Choice Return Codes encryption keys. Not null.</li>
 * <li>π<sub>pkCCR</sub>, the CCR<sub>j</sub> Schnorr proofs of knowledge. Not null.</li>
 * <li>EL<sub>pk</sub>, the CCM<sub>j</sub> election public keys. Not null.</li>
 * <li>π<sub>ELpk</sub>, the CCM<sub>j</sub> Schnorr proofs of knowledge. Not null.</li>
 * <li>EB<sub>pk</sub>, the electoral board public key. Not Null.</li>
 * <li>π<sub>EB</sub>, the electoral board Schnorr proofs of knowledge. Not Null.</li>
 * </ul>
 */
public class VerifyKeyGenerationSchnorrProofsInput {

	private final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> ccrjChoiceReturnCodesEncryptionKeys;
	private final GroupVector<GroupVector<SchnorrProof, ZqGroup>, ZqGroup> ccrjSchnorrProofs;
	private final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> ccmjElectionPublicKeys;
	private final GroupVector<GroupVector<SchnorrProof, ZqGroup>, ZqGroup> ccmjSchnorrProofs;
	private final ElGamalMultiRecipientPublicKey electoralBoardPublicKey;
	private final GroupVector<SchnorrProof, ZqGroup> electoralBoardSchnorrProofs;

	public VerifyKeyGenerationSchnorrProofsInput(final ElectionEventContext electionEventContext) {
		checkNotNull(electionEventContext);

		this.ccrjChoiceReturnCodesEncryptionKeys = electionEventContext.combinedControlComponentPublicKeys().stream()
				.map(ControlComponentPublicKeys::ccrjChoiceReturnCodesEncryptionPublicKey)
				.collect(GroupVector.toGroupVector());
		this.ccrjSchnorrProofs = electionEventContext.combinedControlComponentPublicKeys().stream()
				.map(ControlComponentPublicKeys::ccrjSchnorrProofs)
				.collect(GroupVector.toGroupVector());
		this.ccmjElectionPublicKeys = electionEventContext.combinedControlComponentPublicKeys().stream()
				.map(ControlComponentPublicKeys::ccmjElectionPublicKey)
				.collect(GroupVector.toGroupVector());
		this.ccmjSchnorrProofs = electionEventContext.combinedControlComponentPublicKeys().stream()
				.map(ControlComponentPublicKeys::ccmjSchnorrProofs)
				.collect(GroupVector.toGroupVector());
		this.electoralBoardPublicKey = electionEventContext.electoralBoardPublicKey();
		this.electoralBoardSchnorrProofs = electionEventContext.electoralBoardSchnorrProofs();

		// the cross-checks are done in the ElectionEventContext and CombineControlComponentPublicKeys constructor.
	}

	public GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> getCcrjChoiceReturnCodesEncryptionKeys() {
		return ccrjChoiceReturnCodesEncryptionKeys;
	}

	public GroupVector<GroupVector<SchnorrProof, ZqGroup>, ZqGroup> getCcrjSchnorrProofs() {
		return ccrjSchnorrProofs;
	}

	public GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> getCcmjElectionPublicKeys() {
		return ccmjElectionPublicKeys;
	}

	public GroupVector<GroupVector<SchnorrProof, ZqGroup>, ZqGroup> getCcmjSchnorrProofs() {
		return ccmjSchnorrProofs;
	}

	public ElGamalMultiRecipientPublicKey getElectoralBoardPublicKey() {
		return electoralBoardPublicKey;
	}

	public GroupVector<SchnorrProof, ZqGroup> getElectoralBoardSchnorrProofs() {
		return electoralBoardSchnorrProofs;
	}
}
