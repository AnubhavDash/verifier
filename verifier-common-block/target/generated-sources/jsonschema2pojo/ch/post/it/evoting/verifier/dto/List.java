
package ch.post.it.evoting.verifier.dto;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "isBlank",
    "primeNumber",
    "candidatePositions",
    "alias"
})
public class List {

    @JsonProperty("isBlank")
    private Boolean isBlank;
    @JsonProperty("primeNumber")
    private Integer primeNumber;
    @JsonProperty("candidatePositions")
    private java.util.List<CandidatePosition> candidatePositions = new ArrayList<CandidatePosition>();
    @JsonProperty("alias")
    private String alias;

    @JsonProperty("isBlank")
    public Boolean getIsBlank() {
        return isBlank;
    }

    @JsonProperty("isBlank")
    public void setIsBlank(Boolean isBlank) {
        this.isBlank = isBlank;
    }

    @JsonProperty("primeNumber")
    public Integer getPrimeNumber() {
        return primeNumber;
    }

    @JsonProperty("primeNumber")
    public void setPrimeNumber(Integer primeNumber) {
        this.primeNumber = primeNumber;
    }

    @JsonProperty("candidatePositions")
    public java.util.List<CandidatePosition> getCandidatePositions() {
        return candidatePositions;
    }

    @JsonProperty("candidatePositions")
    public void setCandidatePositions(java.util.List<CandidatePosition> candidatePositions) {
        this.candidatePositions = candidatePositions;
    }

    @JsonProperty("alias")
    public String getAlias() {
        return alias;
    }

    @JsonProperty("alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }

}
