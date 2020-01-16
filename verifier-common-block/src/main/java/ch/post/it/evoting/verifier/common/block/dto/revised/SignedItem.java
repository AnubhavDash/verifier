package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


public class SignedItem {
    public final String field;
    public final String value;
    public final String toto;

    @JsonCreator
    public SignedItem(@JsonProperty("field") String field,
                      @JsonProperty("value") String value,
                      @JsonProperty("toto") String toto) {
        this.field = field;
        this.value = value;
        this.toto = toto;
    }
}
