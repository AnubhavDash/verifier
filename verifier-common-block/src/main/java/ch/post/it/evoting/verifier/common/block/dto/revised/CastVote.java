package ch.post.it.evoting.verifier.common.block.dto.revised;

import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

@Getter
public class CastVote {
    public final UUID electionEventId;
    public final UUID ballotId;
    public final UUID ballotBoxId;
    public final UUID votingCardId;
    public final List<BigInteger> encryptedOptions;
    public final List<BigInteger> encryptedPartialChoiceCodes;
    public final List<BigInteger> encryptedWriteIns;
    public final List<UUID> correctnessIds;
    public final PublicKey verificationCardPublicKey;
    public final String verificationCardPKSignature;
    public final String signature;
    public final X509Certificate certificate;
    public final UUID credentialId;
    public final AuthenticationToken authenticationToken;
    public final String authenticationTokenSignature;
    public final PreImageProof schnorrProof;
    public final List<BigInteger> cipherTextExponentiations;
    public final PreImageProof exponentiationProof;
    public final PlaintextEqualityProof plainTextEqualityProof;
    public final UUID verificationCardId;
    public final UUID verificationCardSetId;


    @JsonCreator
    public CastVote(@JsonProperty("electionEventId") UUID electionEventId, @JsonProperty("ballotId") UUID ballotId,
                    @JsonProperty("ballotBoxId") UUID ballotBoxId, @JsonProperty("votingCardId") UUID votingCardId,
                    @JsonProperty("encryptedOptions") List<BigInteger> encryptedOptions,
                    @JsonProperty("encryptedPartialChoiceCodes") List<BigInteger> encryptedPartialChoiceCodes,
                    @JsonProperty("encryptedWriteIns") List<BigInteger> encryptedWriteIns,
                    @JsonProperty("correctnessIds") List<UUID> correctnessIds,
                    @JsonProperty("verificationCardPublicKey") PublicKey verificationCardPublicKey,
                    @JsonProperty("verificationCardPKSignature") String verificationCardPKSignature,
                    @JsonProperty("signature") String signature, @JsonProperty("certificate") X509Certificate certificate,
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
