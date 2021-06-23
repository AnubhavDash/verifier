package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import lombok.Getter;

@Getter
public class ShuffleArgumentMessage {

	private final List<Commitment> initialMessage;
	private final List<Commitment> firstAnswer;
	private final ShuffleArgumentSecondAnswer shuffleArgumentSecondAnswer;

	public ShuffleArgumentMessage(
			@JsonProperty("initialMessage")
					Commitment[] initialMessage,
			@JsonProperty("firstAnswer")
					Commitment[] firstAnswer,
			@JsonProperty("secondAnswer")
					ShuffleArgumentSecondAnswer secondAnswer) {
		this.initialMessage = ImmutableList.copyOf(initialMessage);
		this.firstAnswer = ImmutableList.copyOf(firstAnswer);
		this.shuffleArgumentSecondAnswer = secondAnswer;
	}
}
