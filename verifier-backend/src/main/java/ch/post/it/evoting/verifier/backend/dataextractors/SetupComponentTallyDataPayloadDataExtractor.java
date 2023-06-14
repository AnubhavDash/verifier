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

import java.util.Arrays;
import java.util.Objects;

import org.jsfr.json.JsonSurfer;
import org.jsfr.json.ValueBox;
import org.jsfr.json.compiler.JsonPathCompiler;
import org.jsfr.json.path.JsonPath;
import org.springframework.stereotype.Component;

@Component
public class SetupComponentTallyDataPayloadDataExtractor extends
		DataExtractor<SetupComponentTallyDataPayloadDataExtractor.ValueBoxConfiguration, SetupComponentTallyDataPayloadDataExtractor.DataExtraction> {

	private static final JsonPath ELECTION_EVENT_ID_JSON_PATH = JsonPathCompiler.compile("$.electionEventId");
	private static final JsonPath VERIFICATION_CARD_SET_ID_JSON_PATH = JsonPathCompiler.compile("$.verificationCardSetId");
	private static final JsonPath VERIFICATION_CARD_IDS_JSON_PATH = JsonPathCompiler.compile("$.verificationCardIds");

	public SetupComponentTallyDataPayloadDataExtractor(final JsonSurfer jsonSurfer) {
		super(jsonSurfer,
				collector -> new ValueBoxConfiguration(
						collector.collectOne(ELECTION_EVENT_ID_JSON_PATH, String.class),
						collector.collectOne(VERIFICATION_CARD_SET_ID_JSON_PATH, String.class),
						collector.collectOne(VERIFICATION_CARD_IDS_JSON_PATH, String[].class)
				),
				configuration -> new SetupComponentTallyDataPayloadDataExtractor.DataExtraction(
						configuration.electionEventIdValueBox().get(),
						configuration.verificationCardSetIdValueBox().get(),
						configuration.verificationCardIdsValueBox().get()
				)
		);
	}

	public record DataExtraction(String electionEventId,
								 String verificationCardSetId,
								 String[] verificationCardIds) {

		public DataExtraction(final String electionEventId,
				final String verificationCardSetId,
				final String[] verificationCardIds) {
			this.electionEventId = electionEventId;
			this.verificationCardSetId = verificationCardSetId;
			this.verificationCardIds = Arrays.copyOf(verificationCardIds, verificationCardIds.length);
		}

		@Override
		public String[] verificationCardIds() {
			return Arrays.copyOf(verificationCardIds, verificationCardIds.length);
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			final DataExtraction that = (DataExtraction) o;
			return Objects.equals(electionEventId, that.electionEventId) &&
					Objects.equals(verificationCardSetId, that.verificationCardSetId) &&
					Arrays.equals(verificationCardIds, that.verificationCardIds);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(electionEventId, verificationCardSetId);
			result = 31 * result + Arrays.hashCode(verificationCardIds);
			return result;
		}

		@Override
		public String toString() {
			return "DataExtraction{" +
					"electionEventId='" + electionEventId + '\'' +
					", verificationCardSetId='" + verificationCardSetId + '\'' +
					", verificationCardIds=" + Arrays.toString(verificationCardIds) +
					'}';
		}
	}

	public record ValueBoxConfiguration(ValueBox<String> electionEventIdValueBox,
										ValueBox<String> verificationCardSetIdValueBox,
										ValueBox<String[]> verificationCardIdsValueBox) {
	}
}
