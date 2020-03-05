package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class IniMEBasic {

    private EncryptedBallotWrapper commitmentPublicA0;
    private List<EncryptedBallotWrapper> commitmentPublicB;
    private List<CipherText> ciphertextsE;

    public IniMEBasic(@JsonProperty("commitmentPublicA0") EncryptedBallotWrapper commitmentPublicA0,
                      @JsonProperty("commitmentPublicB") EncryptedBallotWrapper[] commitmentPublicB,
                      @JsonProperty("ciphertextsE") CipherText[] ciphertextsE) {
        this.commitmentPublicA0 = commitmentPublicA0;
        this.commitmentPublicB = ImmutableList.copyOf(commitmentPublicB);
        this.ciphertextsE = ImmutableList.copyOf(ciphertextsE);
    }
}
