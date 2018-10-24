
package ch.post.it.evoting.verifier.dto;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "alias",
    "candidateListId",
    "primeNumber",
    "initialAccumulation",
    "isBlank"
})
public class CandidatePosition {

    @JsonProperty("alias")
    private String alias;
    @JsonProperty("candidateListId")
    private String candidateListId;
    @JsonProperty("primeNumber")
    private List<Integer> primeNumber = new ArrayList<Integer>();
    @JsonProperty("initialAccumulation")
    private Integer initialAccumulation;
    @JsonProperty("isBlank")
    private Boolean isBlank;

    @JsonProperty("alias")
    public String getAlias() {
        return alias;
    }

    @JsonProperty("alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @JsonProperty("candidateListId")
    public String getCandidateListId() {
        return candidateListId;
    }

    @JsonProperty("candidateListId")
    public void setCandidateListId(String candidateListId) {
        this.candidateListId = candidateListId;
    }

    @JsonProperty("primeNumber")
    public List<Integer> getPrimeNumber() {
        return primeNumber;
    }

    @JsonProperty("primeNumber")
    public void setPrimeNumber(List<Integer> primeNumber) {
        this.primeNumber = primeNumber;
    }

    @JsonProperty("initialAccumulation")
    public Integer getInitialAccumulation() {
        return initialAccumulation;
    }

    @JsonProperty("initialAccumulation")
    public void setInitialAccumulation(Integer initialAccumulation) {
        this.initialAccumulation = initialAccumulation;
    }

    @JsonProperty("isBlank")
    public Boolean getIsBlank() {
        return isBlank;
    }

    @JsonProperty("isBlank")
    public void setIsBlank(Boolean isBlank) {
        this.isBlank = isBlank;
    }

}
