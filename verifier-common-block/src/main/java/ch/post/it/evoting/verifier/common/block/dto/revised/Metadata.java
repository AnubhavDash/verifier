package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Base64;
import java.util.List;

public class Metadata {
    public final String version;
    public final List<SignedItem> signedItems;
    public final String algorithm;
    public final String base64Signature;

    @JsonCreator
    public Metadata(@JsonProperty("version") String version,
                    @JsonProperty("signed") List<SignedItem> signedItems,
                    @JsonProperty("alg") String algorithm,
                    @JsonProperty("signature") String base64Signature) {
        this.version = version;
        this.signedItems = signedItems;
        this.algorithm = algorithm;
        this.base64Signature = base64Signature;
    }

    public byte[] getSignature() {
        return Base64.getDecoder().decode(base64Signature);
    }
}
