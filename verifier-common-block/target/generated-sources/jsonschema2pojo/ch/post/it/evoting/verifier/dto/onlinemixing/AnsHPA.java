
package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "initial",
    "answer"
})
public class AnsHPA {

    @JsonProperty("initial")
    private Initial initial;
    @JsonProperty("answer")
    private Answer answer;

    @JsonProperty("initial")
    public Initial getInitial() {
        return initial;
    }

    @JsonProperty("initial")
    public void setInitial(Initial initial) {
        this.initial = initial;
    }

    @JsonProperty("answer")
    public Answer getAnswer() {
        return answer;
    }

    @JsonProperty("answer")
    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

}
