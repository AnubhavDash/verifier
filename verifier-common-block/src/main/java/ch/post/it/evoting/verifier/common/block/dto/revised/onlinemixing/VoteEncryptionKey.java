package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;

import lombok.Getter;

@Getter
public class VoteEncryptionKey {

	private final EncryptionGroup zpSubgroup;
	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private final List<BigInteger> elements;

	public VoteEncryptionKey(
			@JsonProperty("zpSubgroup")
					EncryptionGroup zpSubgroup,
			@JsonProperty("elements")
					List<BigInteger> elements) {
		this.zpSubgroup = zpSubgroup;
		this.elements = elements;
	}
}
