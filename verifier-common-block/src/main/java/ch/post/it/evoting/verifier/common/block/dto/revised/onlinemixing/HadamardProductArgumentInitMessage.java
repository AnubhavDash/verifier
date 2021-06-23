package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import lombok.Getter;

@Getter
public class HadamardProductArgumentInitMessage {

	private final List<Commitment> commitmentsB;

	public HadamardProductArgumentInitMessage(
			@JsonProperty("commitmentPublicB")
					Commitment[] commitmentPublicB) {
		this.commitmentsB = ImmutableList.copyOf(commitmentPublicB);
	}
}
