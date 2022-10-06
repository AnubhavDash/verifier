/*
 * Copyright 2022 Post CH Ltd
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
package ch.post.it.evoting.verifier.protocol.domain.tally;

import static ch.post.it.evoting.cryptoprimitives.math.GroupVector.toGroupVector;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.cryptoprimitives.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

public class TallyComponentVotesPayloadDeserializer extends JsonDeserializer<TallyComponentVotesPayload> {

	@Override
	public TallyComponentVotesPayload deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
		final ObjectMapper mapper = DomainObjectMapper.getNewInstance();

		final JsonNode node = mapper.readTree(parser);
		final String electionEventId = mapper.readValue(node.get("electionEventId").toString(), String.class);
		final String ballotId = mapper.readValue(node.get("ballotId").toString(), String.class);
		final String ballotBoxId = mapper.readValue(node.get("ballotBoxId").toString(), String.class);

		final JsonNode groupNode = node.get("encryptionGroup");
		final GqGroup encryptionGroup = mapper.readValue(groupNode.toString(), GqGroup.class);

		final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> votes = Arrays.stream(mapper.reader()
						.withAttribute("group", encryptionGroup)
						.readValue(node.get("votes"), PrimeGqElement[][].class))
				.map(GroupVector::of)
				.collect(toGroupVector());

		final List<List<String>> actualSelectedVotingOptions = Arrays.stream(mapper.reader()
						.readValue(node.get("actualSelectedVotingOptions"), String[][].class))
				.map(Arrays::stream)
				.map(Stream::toList)
				.toList();

		final CryptoPrimitivesSignature signature = mapper.readValue(node.get("signature").toString(), CryptoPrimitivesSignature.class);

		return new TallyComponentVotesPayload(electionEventId, ballotId, ballotBoxId, encryptionGroup, votes, actualSelectedVotingOptions, signature);
	}
}
