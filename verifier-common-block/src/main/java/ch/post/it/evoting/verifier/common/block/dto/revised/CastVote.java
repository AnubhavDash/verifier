/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.common.block.dto.revised;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

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

    @JsonCreator
    public CastVote(@JsonProperty("electionEventId") UUID electionEventId,
                    @JsonProperty("ballotId") UUID ballotId,
                    @JsonProperty("ballotBoxId") UUID ballotBoxId,
                    @JsonProperty("votingCardId") UUID votingCardId,
                    @JsonProperty("encryptedOptions") List<BigInteger> encryptedOptions,
                    @JsonProperty("encryptedPartialChoiceCodes") List<BigInteger> encryptedPartialChoiceCodes,
                    @JsonProperty("encryptedWriteIns") List<BigInteger> encryptedWriteIns,
                    @JsonProperty("correctnessIds") List<UUID> correctnessIds,
                    @JsonProperty("verificationCardPublicKey") PublicKey verificationCardPublicKey,
                    @JsonProperty("verificationCardPKSignature") String verificationCardPKSignature,
                    @JsonProperty("signature") String signature,
                    @JsonProperty("certificate") X509Certificate certificate,
                    @JsonProperty("credentialId") UUID credentialId,
                    @JsonProperty("authenticationToken") AuthenticationToken authenticationToken,
                    @JsonProperty("authenticationTokenSignature") String authenticationTokenSignature,
                    @JsonProperty("schnorrProof") PreImageProof schnorrProof,
                    @JsonProperty("cipherTextExponentiations") List<BigInteger> cipherTextExponentiations,
                    @JsonProperty("exponentiationProof") PreImageProof exponentiationProof,
                    @JsonProperty("plaintextEqualityProof") PlaintextEqualityProof plainTextEqualityProof,
                    @JsonProperty("verificationCardId") UUID verificationCardId,
                    @JsonProperty("verificationCardSetId") UUID verificationCardSetId) {
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
