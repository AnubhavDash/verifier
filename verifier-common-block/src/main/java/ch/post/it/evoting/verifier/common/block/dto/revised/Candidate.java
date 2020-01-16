package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Getter
public class Candidate {
    public final UUID alias;
    public final List<BigInteger> primeNumbers;
    public final boolean blank;

    @JsonCreator
    public Candidate(@JsonProperty("alias") UUID alias,
                     @JsonProperty("primeNumber") List<BigInteger> primeNumber,
                     @JsonProperty("blank") boolean blank) {
        this.alias = alias;
        this.primeNumbers = primeNumber;
        this.blank = blank;
    }
}
