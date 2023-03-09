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
package ch.post.it.evoting.verifier.protocol.domain.configuration;

import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.hasNoDuplicates;
import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static ch.post.it.evoting.cryptoprimitives.utils.Validations.allEqual;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SignedPayload;
import ch.post.it.evoting.cryptoprimitives.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.cryptoprimitives.domain.validations.Validations;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;

@JsonDeserialize(using = SetupComponentTallyDataPayloadDeserializer.class)
@JsonPropertyOrder({ "electionEventId", "verificationCardSetId", "ballotBoxDefaultTitle", "encryptionGroup", "verificationCardIds",
		"verificationCardPublicKeys", "signature" })
public class SetupComponentTallyDataPayload implements SignedPayload {

	@JsonProperty
	private final String electionEventId;

	@JsonProperty
	private final String verificationCardSetId;

	@JsonProperty
	private final String ballotBoxDefaultTitle;

	@JsonProperty
	private final GqGroup encryptionGroup;

	@JsonProperty
	private final List<String> verificationCardIds;

	@JsonProperty
	private final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> verificationCardPublicKeys;

	@JsonProperty
	private CryptoPrimitivesSignature signature;

	@JsonCreator
	public SetupComponentTallyDataPayload(
			@JsonProperty("electionEventId")
			final String electionEventId,

			@JsonProperty("verificationCardSetId")
			final String verificationCardSetId,

			@JsonProperty("ballotBoxDefaultTitle")
			final String ballotBoxDefaultTitle,

			@JsonProperty("encryptionGroup")
			final GqGroup encryptionGroup,

			@JsonProperty("verificationCardIds")
			final List<String> verificationCardIds,

			@JsonProperty("verificationCardPublicKeys")
			final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> verificationCardPublicKeys,

			@JsonProperty("signature")
			final CryptoPrimitivesSignature signature) {

		this(electionEventId, verificationCardSetId, ballotBoxDefaultTitle, encryptionGroup, verificationCardIds, verificationCardPublicKeys);
		this.signature = checkNotNull(signature);
	}

	public SetupComponentTallyDataPayload(
			final String electionEventId,
			final String verificationCardSetId,
			final String ballotBoxDefaultTitle,
			final GqGroup encryptionGroup,
			final List<String> verificationCardIds,
			final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> verificationCardPublicKeys) {

		this.electionEventId = validateUUID(electionEventId);
		this.verificationCardSetId = validateUUID(verificationCardSetId);
		this.ballotBoxDefaultTitle = checkNotNull(ballotBoxDefaultTitle);
		this.encryptionGroup = checkNotNull(encryptionGroup);

		checkArgument(!this.ballotBoxDefaultTitle.isBlank(), "The ballot box alias must not be blank.");

		this.verificationCardIds = List.copyOf(checkNotNull(verificationCardIds));
		checkArgument(!this.verificationCardIds.isEmpty(), "The verification card ids list must be non-empty.");
		this.verificationCardIds.stream().parallel().forEach(Validations::validateUUID);
		checkArgument(hasNoDuplicates(this.verificationCardIds), "The verification card ids list must not contain any duplicated id.");

		this.verificationCardPublicKeys = checkNotNull(verificationCardPublicKeys);
		checkArgument(!this.verificationCardPublicKeys.isEmpty(), "The verification card public keys list must be non-empty.");
		checkArgument(allEqual(verificationCardPublicKeys.stream(), ElGamalMultiRecipientPublicKey::getGroup),
				"All verification card public keys should have the same group.");
		checkArgument(this.verificationCardPublicKeys.get(0).getGroup().equals(this.encryptionGroup),
				"The encryption group must be the same as the group of the verification card public keys");

		checkArgument(this.verificationCardIds.size() == this.verificationCardPublicKeys.size(),
				"The verification card ids list size must be equal to the verification card public keys list size.");
	}

	public String getElectionEventId() {
		return electionEventId;
	}

	public String getVerificationCardSetId() {
		return verificationCardSetId;
	}

	public String getBallotBoxDefaultTitle() {
		return ballotBoxDefaultTitle;
	}

	public GqGroup getEncryptionGroup() {
		return encryptionGroup;
	}

	public List<String> getVerificationCardIds() {
		return List.copyOf(verificationCardIds);
	}

	public GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> getVerificationCardPublicKeys() {
		return verificationCardPublicKeys;
	}

	@Override
	public CryptoPrimitivesSignature getSignature() {
		return signature;
	}

	@Override
	public void setSignature(final CryptoPrimitivesSignature signature) {
		this.signature = checkNotNull(signature);
	}

	@Override
	public List<Hashable> toHashableForm() {
		return List.of(
				HashableString.from(electionEventId),
				HashableString.from(verificationCardSetId),
				HashableString.from(ballotBoxDefaultTitle),
				encryptionGroup,
				HashableList.from(verificationCardIds.stream().map(HashableString::from).toList()),
				verificationCardPublicKeys);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final SetupComponentTallyDataPayload that = (SetupComponentTallyDataPayload) o;
		return electionEventId.equals(that.electionEventId) && verificationCardSetId.equals(that.verificationCardSetId)
				&& ballotBoxDefaultTitle.equals(
				that.ballotBoxDefaultTitle) && encryptionGroup.equals(that.encryptionGroup) && verificationCardIds.equals(that.verificationCardIds)
				&& verificationCardPublicKeys.equals(that.verificationCardPublicKeys) && Objects.equals(signature, that.signature);
	}

	@Override
	public int hashCode() {
		return Objects.hash(electionEventId, verificationCardSetId, ballotBoxDefaultTitle, encryptionGroup, verificationCardIds,
				verificationCardPublicKeys,
				signature);
	}
}
