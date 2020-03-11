package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class MultiExponentiationArgumentInitMessage {

    private Commitment commitmentA0;
    private List<Commitment> commitmentsB;
    private List<Ciphertext> ciphertextsE;

    public MultiExponentiationArgumentInitMessage(@JsonProperty("commitmentPublicA0") Commitment commitmentPublicA0,
                                                  @JsonProperty("commitmentPublicB") Commitment[] commitmentPublicB,
                                                  @JsonProperty("ciphertextsE") Ciphertext[] ciphertextsE) {
        this.commitmentA0 = commitmentPublicA0;
        this.commitmentsB = ImmutableList.copyOf(commitmentPublicB);
        this.ciphertextsE = ImmutableList.copyOf(ciphertextsE);
    }
}
