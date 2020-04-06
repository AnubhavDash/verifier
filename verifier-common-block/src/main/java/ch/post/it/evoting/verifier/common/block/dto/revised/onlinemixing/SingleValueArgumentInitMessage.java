package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SingleValueArgumentInitMessage {

    private final Commitment commitmentD;
    private final Commitment commitmentLowerDelta;
    private final Commitment commitmentUpperDelta;

    public SingleValueArgumentInitMessage(@JsonProperty("commitmentPublicD") Commitment commitmentPublicD,
                                          @JsonProperty("commitmentPublicLowDelta") Commitment commitmentPublicLowDelta,
                                          @JsonProperty("commitmentPublicHighDelta") Commitment commitmentPublicHighDelta) {
        this.commitmentD = commitmentPublicD;
        this.commitmentLowerDelta = commitmentPublicLowDelta;
        this.commitmentUpperDelta = commitmentPublicHighDelta;
    }
}
