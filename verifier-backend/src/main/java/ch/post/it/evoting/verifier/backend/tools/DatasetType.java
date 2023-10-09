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
package ch.post.it.evoting.verifier.backend.tools;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.regex.Pattern;

public enum DatasetType {
	CONTEXT("setup\\\\configuration-anonymized\\.xml"),
	SETUP("setup\\\\verification_card_sets\\\\[a-zA-Z0-9]{32}\\\\controlComponentCodeSharesPayload\\.[0-9]+\\.json"),
	TALLY("tally\\\\ballot_boxes\\\\[a-zA-Z0-9]{32}\\\\controlComponentBallotBoxPayload_[1-4]{1}\\.json");

	private final Pattern existingFilePattern;

	DatasetType(final String existingFileRegex) {
		this.existingFilePattern = Pattern.compile(existingFileRegex);
	}

	public Pattern getExistingFilePattern() {
		return existingFilePattern;
	}

	public static DatasetType getDatasetType(final String fileName) {
		checkNotNull(fileName);

		return Arrays.stream(DatasetType.values())
				.filter(datasetType -> datasetType.getExistingFilePattern().matcher(fileName).matches())
				.findAny()
				.orElse(null);
	}
}
