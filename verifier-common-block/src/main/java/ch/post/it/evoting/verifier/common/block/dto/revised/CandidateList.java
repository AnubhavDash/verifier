package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
public class CandidateList {
    public final boolean isBlank;
    public final BigInteger primeNumber;
    public final List<CandidatePosition> candidatePositions;
    public final String alias;

    @JsonCreator
    public CandidateList(@JsonProperty("isBlank") boolean isBlank,
                         @JsonProperty("primeNumber") BigInteger primeNumber,
                         @JsonProperty("candidatePositions") List<CandidatePosition> candidatePositions,
                         @JsonProperty("alias") String alias) {
        this.isBlank = isBlank;
        this.primeNumber = primeNumber;
        this.candidatePositions = ImmutableList.copyOf(candidatePositions);
        this.alias = alias;
    }
}
