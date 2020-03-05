package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class IniSVA {

    private EncryptedBallotWrapper commitmentPublicD;
    private EncryptedBallotWrapper commitmentPublicLowDelta;
    private EncryptedBallotWrapper commitmentPublicHighDelta;

    public IniSVA(@JsonProperty("commitmentPublicD") EncryptedBallotWrapper commitmentPublicD,
                  @JsonProperty("commitmentPublicLowDelta") EncryptedBallotWrapper commitmentPublicLowDelta,
                  @JsonProperty("commitmentPublicHighDelta") EncryptedBallotWrapper commitmentPublicHighDelta) {
        this.commitmentPublicD = commitmentPublicD;
        this.commitmentPublicLowDelta = commitmentPublicLowDelta;
        this.commitmentPublicHighDelta = commitmentPublicHighDelta;
    }
}
