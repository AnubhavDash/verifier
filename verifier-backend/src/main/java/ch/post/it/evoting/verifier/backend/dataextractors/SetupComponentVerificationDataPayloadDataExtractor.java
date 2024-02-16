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

import java.util.Collection;
import java.util.List;

import org.jsfr.json.JsonSurfer;
import org.jsfr.json.ValueBox;
import org.jsfr.json.compiler.JsonPathCompiler;
import org.jsfr.json.path.JsonPath;
import org.springframework.stereotype.Component;

@Component
public class SetupComponentVerificationDataPayloadDataExtractor extends
		DataExtractor<SetupComponentVerificationDataPayloadDataExtractor.ValueBoxConfiguration, SetupComponentVerificationDataPayloadDataExtractor.DataExtraction> {

	private static final JsonPath CHUNK_ID_JSON_PATH = JsonPathCompiler.compile("$.chunkId");
	private static final JsonPath ELECTION_EVENT_ID_JSON_PATH = JsonPathCompiler.compile("$.electionEventId");
	private static final JsonPath VERIFICATION_CARD_SET_ID_JSON_PATH = JsonPathCompiler.compile("$.verificationCardSetId");
	private static final JsonPath VERIFICATION_CARD_IDS_JSON_PATH = JsonPathCompiler.compile("$.setupComponentVerificationData[*].verificationCardId");

	public SetupComponentVerificationDataPayloadDataExtractor(final JsonSurfer jsonSurfer) {
		super(jsonSurfer,
				collector -> new ValueBoxConfiguration(
						collector.collectOne(CHUNK_ID_JSON_PATH, Integer.class),
						collector.collectOne(ELECTION_EVENT_ID_JSON_PATH, String.class),
						collector.collectOne(VERIFICATION_CARD_SET_ID_JSON_PATH, String.class),
						collector.collectAll(VERIFICATION_CARD_IDS_JSON_PATH, String.class)
				),
				configuration -> new SetupComponentVerificationDataPayloadDataExtractor.DataExtraction(
						configuration.chunkIdValueBox().get(),
						configuration.electionEventIdValueBox().get(),
						configuration.verificationCardSetIdValueBox().get(),
						configuration.verificationCardIdsValueBox().get()
				)
		);
	}

	public record DataExtraction(Integer chunkId,
								 String electionEventId,
								 String verificationCardSetId,
								 Collection<String> verificationCardIds) {

		public DataExtraction(final Integer chunkId, final String electionEventId, final String verificationCardSetId,
				final Collection<String> verificationCardIds) {
			this.chunkId = chunkId;
			this.electionEventId = electionEventId;
			this.verificationCardSetId = verificationCardSetId;
			this.verificationCardIds = List.copyOf(verificationCardIds);
		}
	}

	public record ValueBoxConfiguration(ValueBox<Integer> chunkIdValueBox,
										ValueBox<String> electionEventIdValueBox,
										ValueBox<String> verificationCardSetIdValueBox,
										ValueBox<Collection<String>> verificationCardIdsValueBox) {
	}
}
