
package ch.post.it.evoting.verifier.dto.onlinemixing;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "commitmentPublicB"
})
public class IniHPA {

    @JsonProperty("commitmentPublicB")
    private List<CommitmentPublicB__1> commitmentPublicB = new ArrayList<CommitmentPublicB__1>();

    @JsonProperty("commitmentPublicB")
    public List<CommitmentPublicB__1> getCommitmentPublicB() {
        return commitmentPublicB;
    }

    @JsonProperty("commitmentPublicB")
    public void setCommitmentPublicB(List<CommitmentPublicB__1> commitmentPublicB) {
        this.commitmentPublicB = commitmentPublicB;
    }

}
