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
package ch.post.it.evoting.verifier.protocol.domain.tally;

import static ch.post.it.evoting.cryptoprimitives.domain.ControlComponentConstants.NODE_IDS;
import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.hasNoDuplicates;
import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static ch.post.it.evoting.cryptoprimitives.utils.Validations.allEqual;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SignedPayload;
import ch.post.it.evoting.cryptoprimitives.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.verifier.protocol.domain.ContextIds;
import ch.post.it.evoting.verifier.protocol.domain.EncryptedVerifiableVote;

@JsonDeserialize(using = ControlComponentBallotBoxPayloadDeserializer.class)
@JsonPropertyOrder({ "encryptionGroup", "electionEventId", "ballotBoxId", "nodeId", "confirmedEncryptedVotes", "signature" })
public class ControlComponentBallotBoxPayload implements SignedPayload {

	private final GqGroup encryptionGroup;
	private final String electionEventId;
	private final String ballotBoxId;
	private final int nodeId;
	private final List<EncryptedVerifiableVote> confirmedEncryptedVotes;
	private CryptoPrimitivesSignature signature;

	public ControlComponentBallotBoxPayload(
			final GqGroup encryptionGroup,
			final String electionEventId,
			final String ballotBoxId,
			final int nodeId,
			final List<EncryptedVerifiableVote> confirmedEncryptedVotes,
			final CryptoPrimitivesSignature signature) {

		this(encryptionGroup, electionEventId, ballotBoxId, nodeId, confirmedEncryptedVotes);
		this.signature = checkNotNull(signature);
	}

	public ControlComponentBallotBoxPayload(final GqGroup encryptionGroup, final String electionEventId, final String ballotBoxId, final int nodeId,
			final List<EncryptedVerifiableVote> encryptedVerifiableVotes) {

		checkNotNull(encryptionGroup);
		validateUUID(electionEventId);
		validateUUID(ballotBoxId);
		final List<EncryptedVerifiableVote> encryptedVerifiableVotesCopy = List.copyOf(checkNotNull(encryptedVerifiableVotes)).stream()
				.sorted(Comparator.comparing(encryptedVerifiableVote -> encryptedVerifiableVote.contextIds().verificationCardId()))
				.toList(); // Ensures list is ordered by verificationCardId.

		checkArgument(NODE_IDS.contains(nodeId), "The node id must be part of the known node ids. [nodeId: %s]", nodeId);
		checkArgument(
				allEqual(encryptedVerifiableVotesCopy.stream(), encryptedVerifiableVote -> encryptedVerifiableVote.contextIds().electionEventId()),
				"All confirmed votes must have the same election event id.");
		checkArgument(
				allEqual(encryptedVerifiableVotesCopy.stream(),
						encryptedVerifiableVote -> encryptedVerifiableVote.contextIds().verificationCardSetId()),
				"All confirmed votes must have the same verification card set id.");

		checkArgument(hasNoDuplicates(encryptedVerifiableVotesCopy.stream()
				.map(EncryptedVerifiableVote::contextIds)
				.map(ContextIds::verificationCardId)
				.toList()), "All confirmation votes must have a different verification card id.");

		checkArgument(
				encryptedVerifiableVotesCopy.isEmpty() || encryptedVerifiableVotesCopy.get(0).contextIds().electionEventId().equals(electionEventId),
				"The confirmed votes must have the same election event id as the payload.");

		this.encryptionGroup = encryptionGroup;
		this.electionEventId = electionEventId;
		this.ballotBoxId = ballotBoxId;
		this.nodeId = nodeId;
		this.confirmedEncryptedVotes = encryptedVerifiableVotesCopy;
	}

	public GqGroup getEncryptionGroup() {
		return encryptionGroup;
	}

	public String getElectionEventId() {
		return electionEventId;
	}

	public String getBallotBoxId() {
		return ballotBoxId;
	}

	public int getNodeId() {
		return nodeId;
	}

	public List<EncryptedVerifiableVote> getConfirmedEncryptedVotes() {
		return List.copyOf(confirmedEncryptedVotes);
	}

	public CryptoPrimitivesSignature getSignature() {
		return signature;
	}

	@Override
	public void setSignature(final CryptoPrimitivesSignature cryptoPrimitivesPayloadSignature) {
		this.signature = checkNotNull(cryptoPrimitivesPayloadSignature);
	}

	@Override
	public List<Hashable> toHashableForm() {
		if (!confirmedEncryptedVotes.isEmpty()) {
			return List.of(encryptionGroup,
					HashableString.from(electionEventId),
					HashableString.from(ballotBoxId),
					HashableBigInteger.from(BigInteger.valueOf(nodeId)),
					HashableList.from(confirmedEncryptedVotes));
		} else {
			return List.of(encryptionGroup,
					HashableString.from(electionEventId),
					HashableString.from(ballotBoxId),
					HashableBigInteger.from(BigInteger.valueOf(nodeId)));
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ControlComponentBallotBoxPayload that = (ControlComponentBallotBoxPayload) o;
		return nodeId == that.nodeId &&
				encryptionGroup.equals(that.encryptionGroup) &&
				electionEventId.equals(that.electionEventId) &&
				ballotBoxId.equals(that.ballotBoxId) &&
				confirmedEncryptedVotes.equals(that.confirmedEncryptedVotes) &&
				Objects.equals(signature, that.signature);
	}

	@Override
	public int hashCode() {
		return Objects.hash(encryptionGroup, electionEventId, ballotBoxId, nodeId, confirmedEncryptedVotes, signature);
	}
}
