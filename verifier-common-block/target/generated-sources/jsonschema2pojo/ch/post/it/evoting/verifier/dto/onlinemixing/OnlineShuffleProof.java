
package ch.post.it.evoting.verifier.dto.onlinemixing;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "initialMessage",
    "firstAnswer",
    "secondAnswer"
})
public class OnlineShuffleProof {

    @JsonProperty("initialMessage")
    private List<InitialMessage> initialMessage = new ArrayList<InitialMessage>();
    @JsonProperty("firstAnswer")
    private List<FirstAnswer> firstAnswer = new ArrayList<FirstAnswer>();
    @JsonProperty("secondAnswer")
    private SecondAnswer secondAnswer;

    @JsonProperty("initialMessage")
    public List<InitialMessage> getInitialMessage() {
        return initialMessage;
    }

    @JsonProperty("initialMessage")
    public void setInitialMessage(List<InitialMessage> initialMessage) {
        this.initialMessage = initialMessage;
    }

    @JsonProperty("firstAnswer")
    public List<FirstAnswer> getFirstAnswer() {
        return firstAnswer;
    }

    @JsonProperty("firstAnswer")
    public void setFirstAnswer(List<FirstAnswer> firstAnswer) {
        this.firstAnswer = firstAnswer;
    }

    @JsonProperty("secondAnswer")
    public SecondAnswer getSecondAnswer() {
        return secondAnswer;
    }

    @JsonProperty("secondAnswer")
    public void setSecondAnswer(SecondAnswer secondAnswer) {
        this.secondAnswer = secondAnswer;
    }

}
