package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class EncryptionParameters {

    private EncryptionGroup encryptionGroup;
    private String seed;
    private int pCounter;
    private int qCounter;

    public EncryptionParameters(@JsonProperty("eg") EncryptionGroup encryptionGroup,
                                @JsonProperty("seed") String seed,
                                @JsonProperty("pCounter") int pCounter,
                                @JsonProperty("qCounter") int qCounter) {
        this.encryptionGroup = encryptionGroup;
        this.seed = seed;
        this.pCounter = pCounter;
        this.qCounter = qCounter;
    }
}
