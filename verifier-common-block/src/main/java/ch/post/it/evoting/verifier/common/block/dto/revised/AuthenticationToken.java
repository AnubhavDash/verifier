package ch.post.it.evoting.verifier.common.block.dto.revised;

import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class AuthenticationToken {
    public final String id;
    public final VoterInformation voterInformation;
    public final LocalDateTime timestamp;
    public final String signature;

    @JsonCreator
    public AuthenticationToken(@JsonProperty("id") String id,
                               @JsonProperty("voterInformation") VoterInformation voterInformation,
                               @JsonProperty("timestamp") LocalDateTime timestamp,
                               @JsonProperty("signature") String signature) {
        this.id = id;
        this.voterInformation = voterInformation;
        this.timestamp = timestamp;
        this.signature = signature;
    }

    public byte[] getSignature() {
        return TypeConverter.base64ToByte(signature);
    }
}
