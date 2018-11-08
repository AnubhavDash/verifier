
package ch.post.it.evoting.verifier.dto.onlinemixing;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "commitmentPublicA0",
    "commitmentPublicB",
    "ciphertextsE"
})
public class IniMEBasic {

    @JsonProperty("commitmentPublicA0")
    private CommitmentPublicA0__1 commitmentPublicA0;
    @JsonProperty("commitmentPublicB")
    private List<CommitmentPublicB__2> commitmentPublicB = new ArrayList<CommitmentPublicB__2>();
    @JsonProperty("ciphertextsE")
    private List<CiphertextsE> ciphertextsE = new ArrayList<CiphertextsE>();

    @JsonProperty("commitmentPublicA0")
    public CommitmentPublicA0__1 getCommitmentPublicA0() {
        return commitmentPublicA0;
    }

    @JsonProperty("commitmentPublicA0")
    public void setCommitmentPublicA0(CommitmentPublicA0__1 commitmentPublicA0) {
        this.commitmentPublicA0 = commitmentPublicA0;
    }

    @JsonProperty("commitmentPublicB")
    public List<CommitmentPublicB__2> getCommitmentPublicB() {
        return commitmentPublicB;
    }

    @JsonProperty("commitmentPublicB")
    public void setCommitmentPublicB(List<CommitmentPublicB__2> commitmentPublicB) {
        this.commitmentPublicB = commitmentPublicB;
    }

    @JsonProperty("ciphertextsE")
    public List<CiphertextsE> getCiphertextsE() {
        return ciphertextsE;
    }

    @JsonProperty("ciphertextsE")
    public void setCiphertextsE(List<CiphertextsE> ciphertextsE) {
        this.ciphertextsE = ciphertextsE;
    }

}
