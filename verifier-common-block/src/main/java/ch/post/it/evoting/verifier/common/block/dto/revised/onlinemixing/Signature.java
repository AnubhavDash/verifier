package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class Signature {

	private final String signatureContents;
	private final List<String> certificateChain;

	public Signature(
			@JsonProperty("signatureContents")
					String signatureContents,
			@JsonProperty("certificateChain")
					List<String> certificateChain) {
		this.signatureContents = signatureContents;
		this.certificateChain = certificateChain;
	}
}
