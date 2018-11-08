
package ch.post.it.evoting.verifier.dto.onlinemixing;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "commitmentPublicA0",
    "commitmentPublicBM",
    "commitmentPublicD"
})
public class Initial {

    @JsonProperty("commitmentPublicA0")
    private CommitmentPublicA0 commitmentPublicA0;
    @JsonProperty("commitmentPublicBM")
    private CommitmentPublicBM commitmentPublicBM;
    @JsonProperty("commitmentPublicD")
    private List<CommitmentPublicD__1> commitmentPublicD = new ArrayList<CommitmentPublicD__1>();

    @JsonProperty("commitmentPublicA0")
    public CommitmentPublicA0 getCommitmentPublicA0() {
        return commitmentPublicA0;
    }

    @JsonProperty("commitmentPublicA0")
    public void setCommitmentPublicA0(CommitmentPublicA0 commitmentPublicA0) {
        this.commitmentPublicA0 = commitmentPublicA0;
    }

    @JsonProperty("commitmentPublicBM")
    public CommitmentPublicBM getCommitmentPublicBM() {
        return commitmentPublicBM;
    }

    @JsonProperty("commitmentPublicBM")
    public void setCommitmentPublicBM(CommitmentPublicBM commitmentPublicBM) {
        this.commitmentPublicBM = commitmentPublicBM;
    }

    @JsonProperty("commitmentPublicD")
    public List<CommitmentPublicD__1> getCommitmentPublicD() {
        return commitmentPublicD;
    }

    @JsonProperty("commitmentPublicD")
    public void setCommitmentPublicD(List<CommitmentPublicD__1> commitmentPublicD) {
        this.commitmentPublicD = commitmentPublicD;
    }

}
