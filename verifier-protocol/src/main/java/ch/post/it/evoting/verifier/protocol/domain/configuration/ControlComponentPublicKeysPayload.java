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

import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.cryptoprimitives.domain.election.ControlComponentPublicKeys;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SignedPayload;
import ch.post.it.evoting.cryptoprimitives.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;

@JsonDeserialize(using = ControlComponentPublicKeysPayloadDeserializer.class)
@JsonPropertyOrder({ "encryptionGroup", "electionEventId", "controlComponentPublicKeys", "signature" })
public class ControlComponentPublicKeysPayload implements SignedPayload {

	private final GqGroup encryptionGroup;

	private final String electionEventId;

	private final ControlComponentPublicKeys controlComponentPublicKeys;

	private CryptoPrimitivesSignature signature;

	public ControlComponentPublicKeysPayload(
			final GqGroup encryptionGroup,
			final String electionEventId,
			final ControlComponentPublicKeys controlComponentPublicKeys,
			final CryptoPrimitivesSignature signature) {
		this(encryptionGroup, electionEventId, controlComponentPublicKeys);
		this.signature = checkNotNull(signature);
	}

	public ControlComponentPublicKeysPayload(final GqGroup encryptionGroup, final String electionEventId,
			final ControlComponentPublicKeys controlComponentPublicKeys) {
		this.encryptionGroup = checkNotNull(encryptionGroup);
		this.electionEventId = validateUUID(electionEventId);
		this.controlComponentPublicKeys = checkNotNull(controlComponentPublicKeys);
		checkArgument(encryptionGroup.equals(controlComponentPublicKeys.ccmjElectionPublicKey().getGroup()),
				"The groups of the control component public keys payload and the CCMj election public key of the control component public keys must be equal.");
	}

	public GqGroup getEncryptionGroup() {
		return encryptionGroup;
	}

	public String getElectionEventId() {
		return electionEventId;
	}

	public ControlComponentPublicKeys getControlComponentPublicKeys() {
		return controlComponentPublicKeys;
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
		return List.of(encryptionGroup,
				HashableString.from(electionEventId),
				controlComponentPublicKeys);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ControlComponentPublicKeysPayload that = (ControlComponentPublicKeysPayload) o;
		return encryptionGroup.equals(that.encryptionGroup) &&
				electionEventId.equals(that.electionEventId) &&
				controlComponentPublicKeys.equals(that.controlComponentPublicKeys) &&
				Objects.equals(signature, that.signature);
	}

	@Override
	public int hashCode() {
		return Objects.hash(encryptionGroup, electionEventId, controlComponentPublicKeys, signature);
	}

}
