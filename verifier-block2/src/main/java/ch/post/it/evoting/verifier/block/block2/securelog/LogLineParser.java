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
package ch.post.it.evoting.verifier.block.block2.securelog;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.verifier.core.internal.exceptions.MalformedFileException;

@Service
public class LogLineParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogLineParser.class);
	private static final String LOG_LINE_REGEX = "^(.*)(\\{[^\\{\\}]+\\})$";
	private static final Pattern LOG_LINE_PATTERN = Pattern.compile(LOG_LINE_REGEX, Pattern.MULTILINE);

	private final ObjectMapper objectMapper;

	@Autowired
	public LogLineParser(@Qualifier("secureLogsObjectMapper") ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Parses a log line.
	 * @param logLine the log line as a String. It can be either a checkpoint or a regular log line.
	 * @return the log line object representing this log line.
	 * @throws MalformedFileException if the logLine does not conform to the expected format.
	 */
	public LogLine parse(final String logLine) {
		final var matcher = LOG_LINE_PATTERN.matcher(logLine);

		if (!matcher.find()) {
			throw new MalformedFileException(String.format("Line has unexpected format, line %s", logLine));
		}

		String message = checkNotNull(matcher.group(1));
		String metadataJson = checkNotNull(matcher.group(2));

		List<String> fieldNames = getFieldNames(metadataJson);
		if (fieldNames.containsAll(CheckpointMetadata.requiredFields())) {
			var checkpointMetadata = deserializeCheckpointMetadata(metadataJson);
			return new CheckpointLogLine(message, checkpointMetadata);
		} else if (fieldNames.containsAll(RegularLogLineMetadata.requiredFields())) {
			RegularLogLineMetadata metadata = deserializeRegularMetadata(metadataJson);
			return new RegularLogLine(message, metadata);
		} else {
			throw new MalformedFileException(String.format("Metadata does not have either checkpoint or regular required fields in %s", metadataJson));
		}
	}

	private List<String> getFieldNames(String metadataJson) {
		final JsonNode node;
		try {
			node = objectMapper.readTree(metadataJson);
		} catch (JsonProcessingException e) {
			throw new MalformedFileException(String.format("Metadata does not seem to be valid Json in %s", metadataJson));
		}
		List<String> fieldNames = new ArrayList<>();
		node.fieldNames().forEachRemaining(fieldNames::add);
		return fieldNames;
	}

	private RegularLogLineMetadata deserializeRegularMetadata(String metadataJson) {
		try{
			return objectMapper.readValue(metadataJson, RegularLogLineMetadata.class);
		} catch (JsonProcessingException e) {
			throw new MalformedFileException(String.format("Cannot parse the regular log line metadata in %s", metadataJson), e);
		}
	}

	private CheckpointMetadata deserializeCheckpointMetadata(String metadataJson) {
		try {
			return objectMapper.readValue(metadataJson, CheckpointMetadata.class);
		} catch (JsonProcessingException e) {
			throw new MalformedFileException(String.format("Cannot parse the checkpoint metadata in %s", metadataJson), e);
		}
	}
}
