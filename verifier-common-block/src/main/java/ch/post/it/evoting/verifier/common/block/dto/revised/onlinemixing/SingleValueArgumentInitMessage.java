package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SingleValueArgumentInitMessage {

    private Commitment commitmentD;
    private Commitment commitmentLowDelta;
    private Commitment commitmentHighDelta;

    public SingleValueArgumentInitMessage(@JsonProperty("commitmentPublicD") Commitment commitmentPublicD,
                                          @JsonProperty("commitmentPublicLowDelta") Commitment commitmentPublicLowDelta,
                                          @JsonProperty("commitmentPublicHighDelta") Commitment commitmentPublicHighDelta) {
        this.commitmentD = commitmentPublicD;
        this.commitmentLowDelta = commitmentPublicLowDelta;
        this.commitmentHighDelta = commitmentPublicHighDelta;
    }
}
