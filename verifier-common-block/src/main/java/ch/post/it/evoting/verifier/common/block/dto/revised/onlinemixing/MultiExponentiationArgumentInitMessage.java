package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import lombok.Getter;

@Getter
public class MultiExponentiationArgumentInitMessage {

	private final Commitment commitmentA0;
	private final List<Commitment> commitmentsB;
	private final List<Ciphertext> ciphertextsE;

	public MultiExponentiationArgumentInitMessage(
			@JsonProperty("commitmentPublicA0")
					Commitment commitmentPublicA0,
			@JsonProperty("commitmentPublicB")
					Commitment[] commitmentPublicB,
			@JsonProperty("ciphertextsE")
					Ciphertext[] ciphertextsE) {
		this.commitmentA0 = commitmentPublicA0;
		this.commitmentsB = ImmutableList.copyOf(commitmentPublicB);
		this.ciphertextsE = ImmutableList.copyOf(ciphertextsE);
	}
}
