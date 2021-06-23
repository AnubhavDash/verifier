package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;

import lombok.Getter;

@Getter
public class VoteSetId {

	private final BallotBoxId ballotBoxId;
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger index;

	public VoteSetId(
			@JsonProperty("ballotBoxId")
					BallotBoxId ballotBoxId,
			@JsonProperty("index")
					BigInteger index) {
		this.ballotBoxId = ballotBoxId;
		this.index = index;
	}
}
