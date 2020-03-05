package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class EncryptedBallotWrapper {
    private EncryptedBallot element;

    public EncryptedBallotWrapper(@JsonProperty("element") EncryptedBallot element) {
        this.element = element;
    }
}
