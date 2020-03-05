package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MsgPA {

    private EncryptedBallotWrapper commitmentPublicB;
    private IniSVA iniSVA;
    private AnsSVA ansSVA;
    private IniHPA iniHPA;
    private AnsHPA ansHPA;

    public MsgPA(@JsonProperty("commitmentPublicB") EncryptedBallotWrapper commitmentPublicB,
                 @JsonProperty("iniSVA") IniSVA iniSVA,
                 @JsonProperty("ansSVA") AnsSVA ansSVA,
                 @JsonProperty("iniHPA") IniHPA iniHPA,
                 @JsonProperty("ansHPA") AnsHPA ansHPA) {
        this.commitmentPublicB = commitmentPublicB;
        this.iniSVA = iniSVA;
        this.ansSVA = ansSVA;
        this.iniHPA = iniHPA;
        this.ansHPA = ansHPA;
    }
}
