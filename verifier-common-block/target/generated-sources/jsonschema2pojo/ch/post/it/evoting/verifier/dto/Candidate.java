
package ch.post.it.evoting.verifier.dto;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "alias",
    "primeNumber",
    "blank"
})
public class Candidate {

    @JsonProperty("alias")
    private String alias;
    @JsonProperty("primeNumber")
    private List<Integer> primeNumber = new ArrayList<Integer>();
    @JsonProperty("blank")
    private Boolean blank;

    @JsonProperty("alias")
    public String getAlias() {
        return alias;
    }

    @JsonProperty("alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @JsonProperty("primeNumber")
    public List<Integer> getPrimeNumber() {
        return primeNumber;
    }

    @JsonProperty("primeNumber")
    public void setPrimeNumber(List<Integer> primeNumber) {
        this.primeNumber = primeNumber;
    }

    @JsonProperty("blank")
    public Boolean getBlank() {
        return blank;
    }

    @JsonProperty("blank")
    public void setBlank(Boolean blank) {
        this.blank = blank;
    }

}
