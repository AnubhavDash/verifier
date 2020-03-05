package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.List;

@Getter
public class OnlineShuffleProof {
    private List<EncryptedBallotWrapper> initialMessage;
    private List<EncryptedBallotWrapper> firstAnswer;
    private SecondAnswer secondAnswer;

    public OnlineShuffleProof(@JsonProperty("initialMessage") EncryptedBallotWrapper[] initialMessage,
                              @JsonProperty("firstAnswer") EncryptedBallotWrapper[] firstAnswer,
                              @JsonProperty("secondAnswer") SecondAnswer secondAnswer) {
        this.initialMessage = ImmutableList.copyOf(initialMessage);
        this.firstAnswer = ImmutableList.copyOf(firstAnswer);
        this.secondAnswer = secondAnswer;
    }
}
