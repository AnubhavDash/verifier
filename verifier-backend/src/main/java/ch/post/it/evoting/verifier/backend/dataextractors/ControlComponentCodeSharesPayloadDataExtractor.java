/*
 * Copyright 2023 Post CH Ltd
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
package ch.post.it.evoting.verifier.backend.dataextractors;

import java.util.Collection;
import java.util.List;

import org.jsfr.json.JsonSurfer;
import org.jsfr.json.ValueBox;
import org.jsfr.json.compiler.JsonPathCompiler;
import org.jsfr.json.path.JsonPath;
import org.springframework.stereotype.Component;

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
						configuration.chunkIdsValueBox().get(),
						configuration.electionEventIdsValueBox().get(),
						configuration.nodeIdsValueBox().get(),
						configuration.verificationCardSetIdsValueBox().get(),
						configuration.verificationCardIdsNode1ValueBox().get(),
						configuration.verificationCardIdsNode2ValueBox().get(),
						configuration.verificationCardIdsNode3ValueBox().get(),
						configuration.verificationCardIdsNode4ValueBox().get(),
						configuration.pValueVox().get(),
						configuration.qValueVox().get(),
						configuration.gValueVox().get()
				)
		);
	}

	public record DataExtraction(Collection<Integer> chunkIds,
								 Collection<String> electionEventIds,
								 Collection<Integer> nodeIds,
								 Collection<String> verificationCardSetIds,
								 Collection<String> verificationCardIdsNode1,
								 Collection<String> verificationCardIdsNode2,
								 Collection<String> verificationCardIdsNode3,
								 Collection<String> verificationCardIdsNode4,
								 Collection<String> p,
								 Collection<String> q,
								 Collection<String> g) {

		public DataExtraction(final Collection<Integer> chunkIds,
				final Collection<String> electionEventIds,
				final Collection<Integer> nodeIds,
				final Collection<String> verificationCardSetIds,
				final Collection<String> verificationCardIdsNode1,
				final Collection<String> verificationCardIdsNode2,
				final Collection<String> verificationCardIdsNode3,
				final Collection<String> verificationCardIdsNode4,
				final Collection<String> p,
				final Collection<String> q,
				final Collection<String> g) {
			this.chunkIds = List.copyOf(chunkIds);
			this.electionEventIds = List.copyOf(electionEventIds);
			this.nodeIds = List.copyOf(nodeIds);
			this.verificationCardSetIds = List.copyOf(verificationCardSetIds);
			this.verificationCardIdsNode1 = List.copyOf(verificationCardIdsNode1);
			this.verificationCardIdsNode2 = List.copyOf(verificationCardIdsNode2);
			this.verificationCardIdsNode3 = List.copyOf(verificationCardIdsNode3);
			this.verificationCardIdsNode4 = List.copyOf(verificationCardIdsNode4);
			this.p = List.copyOf(p);
			this.q = List.copyOf(q);
			this.g = List.copyOf(g);
		}
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
