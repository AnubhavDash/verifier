package ch.post.it.evoting.verifier.common.block.dto;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.CommitmentKey;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import ch.post.it.evoting.verifier.common.block.dto.revised.algo.ZeroArgument;
import ch.post.it.evoting.verifier.common.block.dto.revised.algo.ZeroStatement;

import lombok.Getter;

@Getter
public class VerifyZArgumentParameters {

	@JsonProperty("eg")
	private EncryptionGroup eg;

	@JsonProperty("ck")
	private CommitmentKey ck;

	@JsonProperty("m")
	private int m;

	@JsonProperty("n")
	private int n;

	@JsonProperty("pk_mix")
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger pk_mix;

	@JsonProperty("statement")
	private ZeroStatement statement;

	@JsonProperty("argument")
	private ZeroArgument argument;

}
