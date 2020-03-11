package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class ShuffleArgumentMessage {

    private List<Commitment> initialMessage;
    private List<Commitment> firstAnswer;
    private ShuffleArgumentSecondAnswer shuffleArgumentSecondAnswer;

    public ShuffleArgumentMessage(@JsonProperty("initialMessage") Commitment[] initialMessage,
                                  @JsonProperty("firstAnswer") Commitment[] firstAnswer,
                                  @JsonProperty("secondAnswer") ShuffleArgumentSecondAnswer secondAnswer) {
        this.initialMessage = ImmutableList.copyOf(initialMessage);
        this.firstAnswer = ImmutableList.copyOf(firstAnswer);
        this.shuffleArgumentSecondAnswer = secondAnswer;
    }
}
