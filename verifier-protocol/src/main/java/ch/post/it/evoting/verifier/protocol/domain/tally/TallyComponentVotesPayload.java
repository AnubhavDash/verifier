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

import static ch.post.it.evoting.cryptoprimitives.domain.VotingOptionsConstants.MAXIMUM_ACTUAL_VOTING_OPTION_LENGTH;
import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;

import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SignedPayload;
import ch.post.it.evoting.cryptoprimitives.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

@JsonPropertyOrder({ "electionEventId", "ballotId", "ballotBoxId", "encryptionGroup", "votes", "actualSelectedVotingOptions", "signature" })
@JsonDeserialize(using = TallyComponentVotesPayloadDeserializer.class)
public class TallyComponentVotesPayload implements SignedPayload {

	@JsonProperty
	private final String electionEventId;

	@JsonProperty
	private final String ballotId;

	@JsonProperty
	private final String ballotBoxId;

	@JsonProperty
	private final GqGroup encryptionGroup;

	@JsonProperty
	private final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> votes;

	@JsonProperty
	private final List<List<String>> actualSelectedVotingOptions;

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

			@JsonProperty(value = "encryptionGroup", required = true)
			final GqGroup encryptionGroup,

			@JsonProperty(value = "votes", required = true)
			final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> votes,

			@JsonProperty(value = "actualSelectedVotingOptions", required = true)
			final List<List<String>> actualSelectedVotingOptions,

			@JsonProperty(value = "signature", required = true)
			final CryptoPrimitivesSignature signature
	) {
		this(electionEventId, ballotId, ballotBoxId, encryptionGroup, votes, actualSelectedVotingOptions);
		this.signature = checkNotNull(signature);
	}

	public TallyComponentVotesPayload(
			final String electionEventId,
			final String ballotId,
			final String ballotBoxId,
			final GqGroup encryptionGroup,
			final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> votes,
			final List<List<String>> actualSelectedVotingOptions
	) {
		this.electionEventId = validateUUID(electionEventId);
		this.ballotId = validateUUID(ballotId);
		this.ballotBoxId = validateUUID(ballotBoxId);
		this.encryptionGroup = checkNotNull(encryptionGroup);
		this.votes = checkNotNull(votes);

		List<List<String>> actualSelectedVotingOptionsCopy = List.copyOf(checkNotNull(actualSelectedVotingOptions));

		actualSelectedVotingOptionsCopy.forEach(Preconditions::checkNotNull);
		actualSelectedVotingOptionsCopy = actualSelectedVotingOptionsCopy.stream()
				.map(List::copyOf)
				.toList();

		actualSelectedVotingOptionsCopy.forEach(options -> options.forEach(Preconditions::checkNotNull));

		this.actualSelectedVotingOptions = actualSelectedVotingOptionsCopy;

		checkArgument(votes.size() == actualSelectedVotingOptions.size(), "There must be as many actual selected voting options as votes.");
		IntStream.range(0, votes.size()).forEach(i -> checkArgument(votes.getElementSize() == actualSelectedVotingOptions.get(i).size(),
				"There must be as many actual selected voting options as votes."));
		checkArgument(votes.isEmpty() || votes.getGroup().equals(encryptionGroup),
				"The encryption group of the votes is different from the encryption group.");

		final Predicate<String> isNotBlank = element -> !element.isBlank();
		final Predicate<String> isSmallerThanMaxLength = element -> element.length() <= MAXIMUM_ACTUAL_VOTING_OPTION_LENGTH;
		checkArgument(actualSelectedVotingOptions.stream()
						.flatMap(Collection::stream)
						.allMatch(isNotBlank.and(isSmallerThanMaxLength)),
				"The actual selected voting options must be non-blank strings and their length must not exceed %s.",
				MAXIMUM_ACTUAL_VOTING_OPTION_LENGTH);
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

	public GqGroup getEncryptionGroup() {
		return encryptionGroup;
	}

	public GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> getVotes() {
		return votes;
	}

	public List<List<String>> getActualSelectedVotingOptions() {
		return actualSelectedVotingOptions;
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
					encryptionGroup,
					HashableList.from(votes.stream()
							.map(vote -> HashableList.from(vote.stream()
									.map(PrimeGqElement::getValue)
									.map(HashableBigInteger::from)
									.toList()))
							.toList()),
					HashableList.from(actualSelectedVotingOptions.stream()
							.map(votingOption -> HashableList.from(votingOption.stream()
									.map(HashableString::from)
									.toList()))
							.toList()));
		} else {
			return List.of(
					HashableString.from(electionEventId),
					HashableString.from(ballotBoxId),
					HashableString.from(ballotId),
					encryptionGroup);
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
		final TallyComponentVotesPayload that = (TallyComponentVotesPayload) o;
		return electionEventId.equals(that.electionEventId) &&
				ballotId.equals(that.ballotId) &&
				ballotBoxId.equals(that.ballotBoxId)
				&& encryptionGroup.equals(that.encryptionGroup) &&
				votes.equals(that.votes) &&
				actualSelectedVotingOptions.equals(that.actualSelectedVotingOptions) &&
				Objects.equals(signature, that.signature);
	}

	@Override
	public int hashCode() {
		return Objects.hash(electionEventId, ballotId, ballotBoxId, encryptionGroup, votes, actualSelectedVotingOptions, signature);
	}
}

