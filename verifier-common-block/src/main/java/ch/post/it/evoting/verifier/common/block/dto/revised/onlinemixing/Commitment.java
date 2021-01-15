package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class Commitment {

	private final GroupElement element;

	public Commitment(
			@JsonProperty("element")
					GroupElement element) {
		this.element = element;
	}
}
