/*
 * Copyright 2021 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
		var decoder = Base64.getDecoder();
		if (values.size() != 1) {
			throw new InvalidFormatException(jsonParser, "wrong number of values (expects 1)", values.asText(),
					BigInteger.class);
		}
		var z = new BigInteger(decoder.decode(values.get(0).asText()));
		return new PreImageProof(q, hash, z);
	}
}

