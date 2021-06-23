/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.common.block.dto.revised;

import java.math.BigInteger;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;

import lombok.Getter;

@Getter
public class VoteOption {

	private final UUID id;
	private final UUID alias;
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger primeNumber;

	public VoteOption(
			@JsonProperty("id")
					UUID id,
			@JsonProperty("alias")
					UUID alias,
			@JsonProperty("primeNumber")
					BigInteger primeNumber) {
		this.id = id;
		this.alias = alias;
		this.primeNumber = primeNumber;
	}
}
