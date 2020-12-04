package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import org.bouncycastle.util.StringList;

import java.math.BigInteger;
import java.util.List;

@Getter
public class VoteEncryptionKey {

    private final EncryptionGroup zpSubgroup;
    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private final List<BigInteger> elements;

    public VoteEncryptionKey(@JsonProperty("zpSubgroup") EncryptionGroup zpSubgroup,
                             @JsonProperty("elements") List<BigInteger> elements) {
        this.zpSubgroup = zpSubgroup;
        this.elements = elements;
    }
}
