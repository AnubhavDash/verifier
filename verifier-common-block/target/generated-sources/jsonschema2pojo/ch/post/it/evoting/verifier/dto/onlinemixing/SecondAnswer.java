
package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "msgPA",
    "iniMEBasic",
    "ansMEBasic"
})
public class SecondAnswer {

    @JsonProperty("msgPA")
    private MsgPA msgPA;
    @JsonProperty("iniMEBasic")
    private IniMEBasic iniMEBasic;
    @JsonProperty("ansMEBasic")
    private AnsMEBasic ansMEBasic;

    @JsonProperty("msgPA")
    public MsgPA getMsgPA() {
        return msgPA;
    }

    @JsonProperty("msgPA")
    public void setMsgPA(MsgPA msgPA) {
        this.msgPA = msgPA;
    }

    @JsonProperty("iniMEBasic")
    public IniMEBasic getIniMEBasic() {
        return iniMEBasic;
    }

    @JsonProperty("iniMEBasic")
    public void setIniMEBasic(IniMEBasic iniMEBasic) {
        this.iniMEBasic = iniMEBasic;
    }

    @JsonProperty("ansMEBasic")
    public AnsMEBasic getAnsMEBasic() {
        return ansMEBasic;
    }

    @JsonProperty("ansMEBasic")
    public void setAnsMEBasic(AnsMEBasic ansMEBasic) {
        this.ansMEBasic = ansMEBasic;
    }

}
