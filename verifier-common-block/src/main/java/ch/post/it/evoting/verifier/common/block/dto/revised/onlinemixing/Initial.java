package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class Initial {

    private EncryptedBallotWrapper commitmentPublicA0;
    private EncryptedBallotWrapper commitmentPublicBM;
    private List<EncryptedBallotWrapper> commitmentPublicD;

    public Initial(@JsonProperty("commitmentPublicA0") EncryptedBallotWrapper commitmentPublicA0,
                   @JsonProperty("commitmentPublicBM") EncryptedBallotWrapper commitmentPublicBM,
                   @JsonProperty("commitmentPublicD") EncryptedBallotWrapper[] commitmentPublicD) {
        this.commitmentPublicA0 = commitmentPublicA0;
        this.commitmentPublicBM = commitmentPublicBM;
        this.commitmentPublicD = ImmutableList.copyOf(commitmentPublicD);
    }
}
