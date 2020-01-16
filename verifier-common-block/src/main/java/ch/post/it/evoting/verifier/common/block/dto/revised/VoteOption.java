package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigInteger;
import java.util.UUID;

@Getter
public class VoteOption {
    public final UUID id;
    public final UUID alias;
    public final BigInteger primeNumber;

    @JsonCreator
    public VoteOption(@JsonProperty("id") UUID id,
                      @JsonProperty("alias") UUID alias,
                      @JsonProperty("primeNumber") BigInteger primeNumber) {
        this.id = id;
        this.alias = alias;
        this.primeNumber = primeNumber;
    }
}
