package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SignedItem {
    public final String field;
    public final String value;

    @JsonCreator
    public SignedItem(@JsonProperty("field") String field,
                      @JsonProperty("value") String value) {
        this.field = field;
        this.value = value;
    }
}
