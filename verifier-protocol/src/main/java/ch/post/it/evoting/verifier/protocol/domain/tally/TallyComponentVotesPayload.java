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

import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SignedPayload;
import ch.post.it.evoting.cryptoprimitives.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

@JsonPropertyOrder({ "electionEventId", "ballotId", "ballotBoxId", "votes", "signature" })
@JsonDeserialize(using = TallyComponentVotesPayloadDeserializer.class)
public class TallyComponentVotesPayload implements SignedPayload {

	@JsonProperty
	private final String electionEventId;

	@JsonProperty
	private final String ballotId;

	@JsonProperty
	private final String ballotBoxId;

	@JsonProperty
	private final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> votes;

	@JsonProperty
	private GqGroup group;

	@JsonProperty
	private CryptoPrimitivesSignature signature;

	@JsonCreator
	public TallyComponentVotesPayload(
			@JsonProperty(value = "electionEventId", required = true)
			final String electionEventId,

			@JsonProperty(value = "ballotId", required = true)
			final String ballotId,

			@JsonProperty(value = "ballotBoxId", required = true)
			final String ballotBoxId,

			@JsonProperty(value = "votes", required = true)
			final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> votes,

			@JsonProperty(value = "signature", required = true)
			final CryptoPrimitivesSignature signature
	) {
		this(electionEventId, ballotId, ballotBoxId, votes);
		this.signature = checkNotNull(signature);
	}

	public TallyComponentVotesPayload(
			final String electionEventId,
			final String ballotId,
			final String ballotBoxId,
			final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> votes
	) {
		this.electionEventId = validateUUID(electionEventId);
		this.ballotId = validateUUID(ballotId);
		this.ballotBoxId = validateUUID(ballotBoxId);
		this.votes = checkNotNull(votes);
		if (!votes.isEmpty()) {
			this.group = votes.getGroup();
		}
	}

	public String getElectionEventId() {
		return electionEventId;
	}

	public String getBallotId() {
		return ballotId;
	}

	public String getBallotBoxId() {
		return ballotBoxId;
	}

	public GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> getVotes() {
		return votes;
	}

	@Override
	public CryptoPrimitivesSignature getSignature() {
		return signature;
	}

	@Override
	public void setSignature(final CryptoPrimitivesSignature signature) {
		this.signature = signature;
	}

	@Override
	public List<Hashable> toHashableForm() {

		if (!votes.isEmpty()) {
			return List.of(
					HashableString.from(electionEventId),
					HashableString.from(ballotId),
					HashableString.from(ballotBoxId),
					HashableList.from(votes.stream()
							.flatMap(Collection::stream)
							.map(PrimeGqElement::getValue)
							.map(HashableBigInteger::from)
							.toList()));
		} else {
			return List.of(
					HashableString.from(electionEventId),
					HashableString.from(ballotId),
					HashableString.from(ballotBoxId));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final TallyComponentVotesPayload that = (TallyComponentVotesPayload) o;
		return electionEventId.equals(that.electionEventId) &&
				ballotId.equals(that.ballotId) &&
				ballotBoxId.equals(that.ballotBoxId) &&
				votes.equals(that.votes)
				&& Objects.equals(signature, that.signature);
	}

	@Override
	public int hashCode() {
		return Objects.hash(electionEventId, ballotId, ballotBoxId, votes, signature);
	}

}
