package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
public class VoteEncryptionKey {

    private EncryptionGroup zpSubgroup;
    private List<BigInteger> elements;

    public VoteEncryptionKey(@JsonProperty("zpSubgroup") EncryptionGroup zpSubgroup,
                             @JsonProperty("elements") List<BigInteger> elements) {
        this.zpSubgroup = zpSubgroup;
        this.elements = elements;
    }
}
