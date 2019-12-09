package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

public class CandidatePosition {
    public final String alias;
    // TODO: why to candidate positions hold candidate list ids ?
    public final UUID candidateListId;
    public final List<BigInteger> primeNumbers;
    public final int initialAccumulation;
    public final boolean isBlank;

    @JsonCreator
    public CandidatePosition(@JsonProperty("alias") String alias,
                             @JsonProperty("candidateListId") UUID candidateListId,
                             @JsonProperty("primeNumber") List<BigInteger> primeNumber,
                             @JsonProperty("initialAccumulation") int initialAccumulation,
                             @JsonProperty("isBlank") boolean isBlank) {
        this.alias = alias;
        this.candidateListId = candidateListId;
        this.primeNumbers = ImmutableList.copyOf(primeNumber);
        this.initialAccumulation = initialAccumulation;
        this.isBlank = isBlank;
    }
}
