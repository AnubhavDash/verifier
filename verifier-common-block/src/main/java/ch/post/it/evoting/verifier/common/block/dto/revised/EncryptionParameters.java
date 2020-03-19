package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class EncryptionParameters {

    @JsonProperty("eg")
    private EncryptionGroup encryptionGroup;

    @JsonProperty("seed")
    private String seed;

    @JsonProperty("pCounter")
    private int pCounter;

    @JsonProperty("qCounter")
    private int qCounter;

}
