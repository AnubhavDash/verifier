package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class HadamardProductArgumentInitMessage {

    private List<Commitment> commitmentsB;

    public HadamardProductArgumentInitMessage(@JsonProperty("commitmentPublicB") Commitment[] commitmentPublicB) {
        this.commitmentsB = ImmutableList.copyOf(commitmentPublicB);
    }
}
