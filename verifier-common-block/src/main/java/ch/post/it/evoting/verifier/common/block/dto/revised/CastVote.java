/*
 * Copyright 2021 Post CH Ltd
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
package ch.post.it.evoting.verifier.common.block.dto.revised;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;

import lombok.Getter;

@Getter
public class CastVote {

	private final UUID electionEventId;
	private final UUID ballotId;
	private final UUID ballotBoxId;
	private final UUID votingCardId;
	private final List<BigInteger> encryptedOptions;
	private final List<BigInteger> encryptedPartialChoiceCodes;
	private final List<BigInteger> encryptedWriteIns;
	private final List<UUID> correctnessIds;
	private final PublicKey verificationCardPublicKey;
	private final String verificationCardPKSignature;
	private final String signature;
	private final X509Certificate certificate;
	private final UUID credentialId;
	private final AuthenticationToken authenticationToken;
	private final String authenticationTokenSignature;
	private final PreImageProof schnorrProof;
	private final List<BigInteger> cipherTextExponentiations;
	private final PreImageProof exponentiationProof;
	private final PlaintextEqualityProof plainTextEqualityProof;
	private final UUID verificationCardId;
	private final UUID verificationCardSetId;

	public CastVote(
			@JsonProperty("electionEventId")
					UUID electionEventId,
			@JsonProperty("ballotId")
					UUID ballotId,
			@JsonProperty("ballotBoxId")
					UUID ballotBoxId,
			@JsonProperty("votingCardId")
					UUID votingCardId,
			@JsonProperty("encryptedOptions")
					List<BigInteger> encryptedOptions,
			@JsonProperty("encryptedPartialChoiceCodes")
					List<BigInteger> encryptedPartialChoiceCodes,
			@JsonProperty("encryptedWriteIns")
					List<BigInteger> encryptedWriteIns,
			@JsonProperty("correctnessIds")
					List<UUID> correctnessIds,
			@JsonProperty("verificationCardPublicKey")
					PublicKey verificationCardPublicKey,
			@JsonProperty("verificationCardPKSignature")
					String verificationCardPKSignature,
			@JsonProperty("signature")
					String signature,
			@JsonProperty("certificate")
					X509Certificate certificate,
			@JsonProperty("credentialId")
					UUID credentialId,
			@JsonProperty("authenticationToken")
					AuthenticationToken authenticationToken,
			@JsonProperty("authenticationTokenSignature")
					String authenticationTokenSignature,
			@JsonProperty("schnorrProof")
					PreImageProof schnorrProof,
			@JsonProperty("cipherTextExponentiations")
					List<BigInteger> cipherTextExponentiations,
			@JsonProperty("exponentiationProof")
					PreImageProof exponentiationProof,
			@JsonProperty("plaintextEqualityProof")
					PlaintextEqualityProof plainTextEqualityProof,
			@JsonProperty("verificationCardId")
					UUID verificationCardId,
			@JsonProperty("verificationCardSetId")
					UUID verificationCardSetId) {
		this.electionEventId = electionEventId;
		this.ballotId = ballotId;
		this.ballotBoxId = ballotBoxId;
		this.votingCardId = votingCardId;
		this.encryptedOptions = encryptedOptions;
		this.encryptedPartialChoiceCodes = encryptedPartialChoiceCodes;
		this.encryptedWriteIns = encryptedWriteIns;
		this.correctnessIds = correctnessIds;
		this.verificationCardPublicKey = verificationCardPublicKey;
		this.verificationCardPKSignature = verificationCardPKSignature;
		this.signature = signature;
		this.certificate = certificate;
		this.credentialId = credentialId;
		this.authenticationToken = authenticationToken;
		this.authenticationTokenSignature = authenticationTokenSignature;
		this.schnorrProof = schnorrProof;
		this.cipherTextExponentiations = cipherTextExponentiations;
		this.exponentiationProof = exponentiationProof;
		this.plainTextEqualityProof = plainTextEqualityProof;
		this.verificationCardId = verificationCardId;
		this.verificationCardSetId = verificationCardSetId;
	}

	public byte[] getVerificationCardPKSignature() {
		return TypeConverter.base64ToByte(verificationCardPKSignature);
	}

	public byte[] getSignature() {
		return TypeConverter.base64ToByte(signature);
	}

	public byte[] getAuthenticationTokenSignature() {
		return TypeConverter.base64ToByte(authenticationTokenSignature);
	}
}
