package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ShuffleArgumentSecondAnswer {

    private final ProductArgumentMessage productArgumentMessage;
    private final MultiExponentiationArgumentInitMessage multiExponentiationArgumentInitMessage;
    private final MultiExponentiationArgumentAnswer multiExponentiationArgumentAnswer;

    public ShuffleArgumentSecondAnswer(@JsonProperty("msgPA") ProductArgumentMessage msgPA,
                                       @JsonProperty("iniMEBasic") MultiExponentiationArgumentInitMessage iniMEBasic,
                                       @JsonProperty("ansMEBasic") MultiExponentiationArgumentAnswer ansMEBasic) {
        this.productArgumentMessage = msgPA;
        this.multiExponentiationArgumentInitMessage = iniMEBasic;
        this.multiExponentiationArgumentAnswer = ansMEBasic;
    }
}
