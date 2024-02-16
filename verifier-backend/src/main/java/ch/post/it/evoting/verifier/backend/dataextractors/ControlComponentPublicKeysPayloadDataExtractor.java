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

import org.jsfr.json.JsonSurfer;
import org.jsfr.json.ValueBox;
import org.jsfr.json.compiler.JsonPathCompiler;
import org.jsfr.json.path.JsonPath;
import org.springframework.stereotype.Service;

@Service
public class ControlComponentPublicKeysPayloadDataExtractor extends
		DataExtractor<ControlComponentPublicKeysPayloadDataExtractor.ValueBoxConfiguration, ControlComponentPublicKeysPayloadDataExtractor.DataExtraction> {

	private static final JsonPath NODE_ID_JSON_PATH = JsonPathCompiler.compile("$.controlComponentPublicKeys.nodeId");
	private static final JsonPath ELECTION_EVENT_ID_JSON_PATH = JsonPathCompiler.compile("$.electionEventId");

	public ControlComponentPublicKeysPayloadDataExtractor(final JsonSurfer jsonSurfer) {
		super(jsonSurfer,
				collector -> new ValueBoxConfiguration(
						collector.collectOne(NODE_ID_JSON_PATH, Integer.class),
						collector.collectOne(ELECTION_EVENT_ID_JSON_PATH, String.class)
				),
				configuration -> new ControlComponentPublicKeysPayloadDataExtractor.DataExtraction(
						configuration.nodeIdValueBox().get(),
						configuration.electionEventIdValueBox.get()
				)
		);
	}

	public record DataExtraction(Integer nodeId,
								 String electionEventId) {
	}

	public record ValueBoxConfiguration(ValueBox<Integer> nodeIdValueBox,
										ValueBox<String> electionEventIdValueBox) {
	}
}
