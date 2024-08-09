/*
 * (c) Copyright 2024 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.backend.dataextractors;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;

import java.util.Collection;

import org.jsfr.json.JsonSurfer;
import org.jsfr.json.ValueBox;
import org.jsfr.json.compiler.JsonPathCompiler;
import org.jsfr.json.path.JsonPath;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;

@Component
public class ControlComponentCodeSharesPayloadDataExtractor extends
		DataExtractor<ControlComponentCodeSharesPayloadDataExtractor.ValueBoxConfiguration, ControlComponentCodeSharesPayloadDataExtractor.DataExtraction> {

	private static final JsonPath CHUNK_IDS_JSON_PATH = JsonPathCompiler.compile("$[*].chunkId");
	private static final JsonPath ELECTION_EVENT_IDS_JSON_PATH = JsonPathCompiler.compile("$[*].electionEventId");
	private static final JsonPath NODE_IDS_JSON_PATH = JsonPathCompiler.compile("$[*].nodeId");
	private static final JsonPath VERIFICATION_CARD_SET_IDS_JSON_PATH = JsonPathCompiler.compile("$[*].verificationCardSetId");
	private static final JsonPath VERIFICATION_CARD_IDS_NODE1_JSON_PATH = JsonPathCompiler.compile(
			"$[?(@.nodeId==1)].controlComponentCodeShares[*].verificationCardId");
	private static final JsonPath VERIFICATION_CARD_IDS_NODE2_JSON_PATH = JsonPathCompiler.compile(
			"$[?(@.nodeId==2)].controlComponentCodeShares[*].verificationCardId");
	private static final JsonPath VERIFICATION_CARD_IDS_NODE3_JSON_PATH = JsonPathCompiler.compile(
			"$[?(@.nodeId==3)].controlComponentCodeShares[*].verificationCardId");
	private static final JsonPath VERIFICATION_CARD_IDS_NODE4_JSON_PATH = JsonPathCompiler.compile(
			"$[?(@.nodeId==4)].controlComponentCodeShares[*].verificationCardId");
	private static final JsonPath P_JSON_PATH = JsonPathCompiler.compile("$[*].encryptionGroup.p");
	private static final JsonPath Q_JSON_PATH = JsonPathCompiler.compile("$[*].encryptionGroup.q");
	private static final JsonPath G_JSON_PATH = JsonPathCompiler.compile("$[*].encryptionGroup.g");

	public ControlComponentCodeSharesPayloadDataExtractor(final JsonSurfer jsonSurfer) {
		super(jsonSurfer,
				collector -> new ValueBoxConfiguration(
						collector.collectAll(CHUNK_IDS_JSON_PATH, Integer.class),
						collector.collectAll(ELECTION_EVENT_IDS_JSON_PATH, String.class),
						collector.collectAll(NODE_IDS_JSON_PATH, Integer.class),
						collector.collectAll(VERIFICATION_CARD_SET_IDS_JSON_PATH, String.class),
						collector.collectAll(VERIFICATION_CARD_IDS_NODE1_JSON_PATH, String.class),
						collector.collectAll(VERIFICATION_CARD_IDS_NODE2_JSON_PATH, String.class),
						collector.collectAll(VERIFICATION_CARD_IDS_NODE3_JSON_PATH, String.class),
						collector.collectAll(VERIFICATION_CARD_IDS_NODE4_JSON_PATH, String.class),
						collector.collectAll(P_JSON_PATH, String.class),
						collector.collectAll(Q_JSON_PATH, String.class),
						collector.collectAll(G_JSON_PATH, String.class)
				),
				configuration -> new ControlComponentCodeSharesPayloadDataExtractor.DataExtraction(
						configuration.chunkIdsValueBox().get().stream().collect(toImmutableList()),
						configuration.electionEventIdsValueBox().get().stream().collect(toImmutableList()),
						configuration.nodeIdsValueBox().get().stream().collect(toImmutableList()),
						configuration.verificationCardSetIdsValueBox().get().stream().collect(toImmutableList()),
						configuration.verificationCardIdsNode1ValueBox().get().stream().collect(toImmutableList()),
						configuration.verificationCardIdsNode2ValueBox().get().stream().collect(toImmutableList()),
						configuration.verificationCardIdsNode3ValueBox().get().stream().collect(toImmutableList()),
						configuration.verificationCardIdsNode4ValueBox().get().stream().collect(toImmutableList()),
						configuration.pValueVox().get().stream().collect(toImmutableList()),
						configuration.qValueVox().get().stream().collect(toImmutableList()),
						configuration.gValueVox().get().stream().collect(toImmutableList())
				)
		);
	}

	public record DataExtraction(ImmutableList<Integer> chunkIds,
								 ImmutableList<String> electionEventIds,
								 ImmutableList<Integer> nodeIds,
								 ImmutableList<String> verificationCardSetIds,
								 ImmutableList<String> verificationCardIdsNode1,
								 ImmutableList<String> verificationCardIdsNode2,
								 ImmutableList<String> verificationCardIdsNode3,
								 ImmutableList<String> verificationCardIdsNode4,
								 ImmutableList<String> p,
								 ImmutableList<String> q,
								 ImmutableList<String> g) {

	}

	public record ValueBoxConfiguration(ValueBox<Collection<Integer>> chunkIdsValueBox,
										ValueBox<Collection<String>> electionEventIdsValueBox,
										ValueBox<Collection<Integer>> nodeIdsValueBox,
										ValueBox<Collection<String>> verificationCardSetIdsValueBox,
										ValueBox<Collection<String>> verificationCardIdsNode1ValueBox,
										ValueBox<Collection<String>> verificationCardIdsNode2ValueBox,
										ValueBox<Collection<String>> verificationCardIdsNode3ValueBox,
										ValueBox<Collection<String>> verificationCardIdsNode4ValueBox,
										ValueBox<Collection<String>> pValueVox,
										ValueBox<Collection<String>> qValueVox,
										ValueBox<Collection<String>> gValueVox) {
	}
}
