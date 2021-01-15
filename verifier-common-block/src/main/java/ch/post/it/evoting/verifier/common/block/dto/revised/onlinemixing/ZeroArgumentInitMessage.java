package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import lombok.Getter;

@Getter
public class ZeroArgumentInitMessage {

	private final Commitment commitmentA0;
	private final Commitment commitmentBM;
	private final List<Commitment> commitmentD;

	public ZeroArgumentInitMessage(
			@JsonProperty("commitmentPublicA0")
					Commitment commitmentPublicA0,
			@JsonProperty("commitmentPublicBM")
					Commitment commitmentPublicBM,
			@JsonProperty("commitmentPublicD")
					Commitment[] commitmentPublicD) {
		this.commitmentA0 = commitmentPublicA0;
		this.commitmentBM = commitmentPublicBM;
		this.commitmentD = ImmutableList.copyOf(commitmentPublicD);
	}
}
