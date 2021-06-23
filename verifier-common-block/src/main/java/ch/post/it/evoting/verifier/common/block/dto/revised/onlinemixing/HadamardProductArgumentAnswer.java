package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class HadamardProductArgumentAnswer {

	private final ZeroArgumentInitMessage zeroArgumentInitMessage;
	private final ZeroArgumentAnswer zeroArgumentAnswer;

	public HadamardProductArgumentAnswer(
			@JsonProperty("initial")
					ZeroArgumentInitMessage initial,
			@JsonProperty("answer")
					ZeroArgumentAnswer answer) {
		this.zeroArgumentInitMessage = initial;
		this.zeroArgumentAnswer = answer;
	}
}
