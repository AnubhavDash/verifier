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
package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import java.math.BigInteger;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import ch.post.it.evoting.verifier.common.block.dto.revised.PreImageProof;

public class PreImageProofDeserializer extends ProofDeserializer<PreImageProof> {

	@Override
	protected PreImageProof instantiateProof(BigInteger q, String hash, JsonNode values,
			JsonParser jsonParser) throws InvalidFormatException {
		Base64.Decoder decoder = Base64.getDecoder();
		if (values.size() != 1) {
			throw new InvalidFormatException(jsonParser, "wrong number of values (expects 1)", values.asText(),
					BigInteger.class);
		}
		BigInteger z = new BigInteger(decoder.decode(values.get(0).asText()));
		return new PreImageProof(q, hash, z);
	}
}

