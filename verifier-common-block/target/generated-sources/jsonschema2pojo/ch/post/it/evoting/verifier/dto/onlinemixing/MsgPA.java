
package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "commitmentPublicB",
    "iniSVA",
    "ansSVA",
    "iniHPA",
    "ansHPA"
})
public class MsgPA {

    @JsonProperty("commitmentPublicB")
    private CommitmentPublicB commitmentPublicB;
    @JsonProperty("iniSVA")
    private IniSVA iniSVA;
    @JsonProperty("ansSVA")
    private AnsSVA ansSVA;
    @JsonProperty("iniHPA")
    private IniHPA iniHPA;
    @JsonProperty("ansHPA")
    private AnsHPA ansHPA;

    @JsonProperty("commitmentPublicB")
    public CommitmentPublicB getCommitmentPublicB() {
        return commitmentPublicB;
    }

    @JsonProperty("commitmentPublicB")
    public void setCommitmentPublicB(CommitmentPublicB commitmentPublicB) {
        this.commitmentPublicB = commitmentPublicB;
    }

    @JsonProperty("iniSVA")
    public IniSVA getIniSVA() {
        return iniSVA;
    }

    @JsonProperty("iniSVA")
    public void setIniSVA(IniSVA iniSVA) {
        this.iniSVA = iniSVA;
    }

    @JsonProperty("ansSVA")
    public AnsSVA getAnsSVA() {
        return ansSVA;
    }

    @JsonProperty("ansSVA")
    public void setAnsSVA(AnsSVA ansSVA) {
        this.ansSVA = ansSVA;
    }

    @JsonProperty("iniHPA")
    public IniHPA getIniHPA() {
        return iniHPA;
    }

    @JsonProperty("iniHPA")
    public void setIniHPA(IniHPA iniHPA) {
        this.iniHPA = iniHPA;
    }

    @JsonProperty("ansHPA")
    public AnsHPA getAnsHPA() {
        return ansHPA;
    }

    @JsonProperty("ansHPA")
    public void setAnsHPA(AnsHPA ansHPA) {
        this.ansHPA = ansHPA;
    }

}
