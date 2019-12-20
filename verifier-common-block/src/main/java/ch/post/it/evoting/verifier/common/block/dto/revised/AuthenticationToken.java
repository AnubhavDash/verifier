package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Base64;

@Getter
public class AuthenticationToken {
    public final String id;
    public final VoterInformation voterInformation;
    public final LocalDateTime timestamp;
    public final String base64Signature;

    @JsonCreator
    public AuthenticationToken(@JsonProperty("id") String id,
                               @JsonProperty("voterInformation") VoterInformation voterInformation,
                               @JsonProperty("timestamp") LocalDateTime timestamp,
                               @JsonProperty("signature") String base64Signature) {
        this.id = id;
        this.voterInformation = voterInformation;
        this.timestamp = timestamp;
        this.base64Signature = base64Signature;
    }

    public byte[] getSignature() {
        return Base64.getDecoder().decode(base64Signature);
    }
}
