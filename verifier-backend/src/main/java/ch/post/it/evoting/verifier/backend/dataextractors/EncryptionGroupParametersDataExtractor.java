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
public class EncryptionGroupParametersDataExtractor extends
		DataExtractor<EncryptionGroupParametersDataExtractor.ValueBoxConfiguration, EncryptionGroupParametersDataExtractor.DataExtraction> {

	private static final JsonPath P_JSON_PATH = JsonPathCompiler.compile("$.encryptionGroup.p");
	private static final JsonPath Q_JSON_PATH = JsonPathCompiler.compile("$.encryptionGroup.q");
	private static final JsonPath G_JSON_PATH = JsonPathCompiler.compile("$.encryptionGroup.g");

	public EncryptionGroupParametersDataExtractor(final JsonSurfer jsonSurfer) {
		super(jsonSurfer,
				collector -> new ValueBoxConfiguration(
						collector.collectOne(P_JSON_PATH, String.class),
						collector.collectOne(Q_JSON_PATH, String.class),
						collector.collectOne(G_JSON_PATH, String.class)
				),
				configuration -> new EncryptionGroupParametersDataExtractor.DataExtraction(
						configuration.pValueBox().get(),
						configuration.qValueBox().get(),
						configuration.gValueBox().get()
				)
		);
	}

	public record DataExtraction(String p,
								 String q,
								 String g) {
	}

	public record ValueBoxConfiguration(ValueBox<String> pValueBox,
										ValueBox<String> qValueBox,
										ValueBox<String> gValueBox) {
	}
}
