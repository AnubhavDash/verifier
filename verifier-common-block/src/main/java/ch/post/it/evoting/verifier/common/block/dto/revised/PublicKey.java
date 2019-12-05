package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class PublicKey {
    public final EncryptionGroup group;
    public final BigInteger key;

    @JsonCreator
    public PublicKey(@JsonProperty("group") EncryptionGroup group, @JsonProperty("key") BigInteger key) {
        this.group = group;
        this.key = key;
    }
}
