package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class IniHPA {

    private List<EncryptedBallotWrapper> commitmentPublicB;

    public IniHPA(@JsonProperty("commitmentPublicB") EncryptedBallotWrapper[] commitmentPublicB) {
        this.commitmentPublicB = ImmutableList.copyOf(commitmentPublicB);
    }
}
