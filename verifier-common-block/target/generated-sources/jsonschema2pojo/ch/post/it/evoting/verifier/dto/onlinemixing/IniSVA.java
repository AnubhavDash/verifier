
package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "commitmentPublicD",
    "commitmentPublicLowDelta",
    "commitmentPublicHighDelta"
})
public class IniSVA {

    @JsonProperty("commitmentPublicD")
    private CommitmentPublicD commitmentPublicD;
    @JsonProperty("commitmentPublicLowDelta")
    private CommitmentPublicLowDelta commitmentPublicLowDelta;
    @JsonProperty("commitmentPublicHighDelta")
    private CommitmentPublicHighDelta commitmentPublicHighDelta;

    @JsonProperty("commitmentPublicD")
    public CommitmentPublicD getCommitmentPublicD() {
        return commitmentPublicD;
    }

    @JsonProperty("commitmentPublicD")
    public void setCommitmentPublicD(CommitmentPublicD commitmentPublicD) {
        this.commitmentPublicD = commitmentPublicD;
    }

    @JsonProperty("commitmentPublicLowDelta")
    public CommitmentPublicLowDelta getCommitmentPublicLowDelta() {
        return commitmentPublicLowDelta;
    }

    @JsonProperty("commitmentPublicLowDelta")
    public void setCommitmentPublicLowDelta(CommitmentPublicLowDelta commitmentPublicLowDelta) {
        this.commitmentPublicLowDelta = commitmentPublicLowDelta;
    }

    @JsonProperty("commitmentPublicHighDelta")
    public CommitmentPublicHighDelta getCommitmentPublicHighDelta() {
        return commitmentPublicHighDelta;
    }

    @JsonProperty("commitmentPublicHighDelta")
    public void setCommitmentPublicHighDelta(CommitmentPublicHighDelta commitmentPublicHighDelta) {
        this.commitmentPublicHighDelta = commitmentPublicHighDelta;
    }

}
