package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SecondAnswer {

    private MsgPA msgPA;
    private IniMEBasic iniMEBasic;
    private AnsMEBasic ansMEBasic;

    public SecondAnswer(@JsonProperty("msgPA") MsgPA msgPA,
                        @JsonProperty("iniMEBasic") IniMEBasic iniMEBasic,
                        @JsonProperty("ansMEBasic") AnsMEBasic ansMEBasic) {
        this.msgPA = msgPA;
        this.iniMEBasic = iniMEBasic;
        this.ansMEBasic = ansMEBasic;
    }
}
