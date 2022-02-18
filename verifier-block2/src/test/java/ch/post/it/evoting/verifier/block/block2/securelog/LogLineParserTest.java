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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ch.post.it.evoting.verifier.core.internal.exceptions.MalformedFileException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LogLineParser.class, SecureLogConfig.class})
class LogLineParserTest {

	@Autowired
	LogLineParser logLineParser;

	@Test
	void parseCheckpointLine() {
		final CheckpointLogLine logLine = (CheckpointLogLine) logLineParser.parse(TestData.CHECKPOINT_LOG_LINE);
		assertEquals(TestData.CHECKPOINT_LOG_LINE_OBJECT, logLine);
	}

	@Test
	void parseInitialCheckpointLine() {
		final CheckpointLogLine logLine = (CheckpointLogLine) logLineParser.parse(TestData.INITIAL_CHECKPOINT_LOG_LINE);
		assertEquals(TestData.INITIAL_CHECKPOINT_OBJECT, logLine);
	}

	@Test
	void parseRegularLogLine() {
		final RegularLogLine logLine = (RegularLogLine) logLineParser.parse(TestData.REGULAR_LOG_LINE);
		assertEquals(TestData.REGULAR_OBJECT, logLine);
	}

	@Test
	void parseNoMetadataFails() {
		String invalidLogLine = "This is a test";
		assertThrows(MalformedFileException.class, () -> logLineParser.parse(invalidLogLine));
	}

	@Test
	void parseEmptyMetadataFails() {
		String invalidLogLine = "This is a test" + "{}";
		assertThrows(MalformedFileException.class, () -> logLineParser.parse(invalidLogLine));
	}

	@Test
	void parseEmptyMessageValidates() {
		String invalidLogLine = TestData.CHECKPOINT_METADATA;
		assertDoesNotThrow(() -> logLineParser.parse(invalidLogLine));
	}

	@Test
	void parseCheckpointWithoutLSKOk() {
		parseValidMetadataDoesNotThrow("{\"ESK\":\"\",\"PHMAC\":\"\",\"LS\":\"12\",\"TS\":\"1634898376948\",\"HMAC\":\"\",\"SG\":\"\"}\n");
	}

	@Test
	void parseCheckpointWithoutPHMACIsOk() {
		parseValidMetadataDoesNotThrow("{\"LSK\":\"\",\"ESK\":\"\",\"LS\":\"12\",\"TS\":\"1634898376948\",\"HMAC\":\"\",\"SG\":\"\"}\n");
	}

	@Test
	void parseInvalidCheckpointWithoutLSFails() {
		parseInvalidLogLineMetadataThrows("{\"LSK\":\"\",\"ESK\":\"\",\"PHMAC\":\"\",\"TS\":\"1634898376948\",\"HMAC\":\"\",\"SG\":\"\"}\n");
	}

	@Test
	void parseInvalidCheckpointWithoutTSFails() {
		parseInvalidLogLineMetadataThrows("{\"LSK\":\"\",\"ESK\":\"\",\"PHMAC\":\"\",\"LS\":\"12\",\"HMAC\":\"\",\"SG\":\"\"}\n");
	}

	@Test
	void parseInvalidCheckpointWithoutHMACFails() {
		parseInvalidLogLineMetadataThrows("{\"LSK\":\"\",\"ESK\":\"\",\"PHMAC\":\"\",\"LS\":\"12\",\"TS\":\"1634898376948\",\"SG\":\"\"}\n");
	}

	@Test
	void parseInvalidCheckpointWithoutSGFails() {
		parseInvalidLogLineMetadataThrows("{\"LSK\":\"\",\"ESK\":\"\",\"PHMAC\":\"\",\"LS\":\"12\",\"TS\":\"1634898376948\",\"HMAC\":\"\"}\n");
	}

	@Test
	void parseTSNotIntegerFails() {
		parseInvalidLogLineMetadataThrows("{\"LSK\":\"\",\"ESK\":\"\",\"PHMAC\":\"\",\"LS\":\"12\",\"TS\":\"1634898376948a\",\"HMAC\":\"\",\"SG\":\"\"}\n");
	}

	@Test
	void parseLSNotIntegerFails() {
		parseInvalidLogLineMetadataThrows("{\"LSK\":\"\",\"ESK\":\"\",\"PHMAC\":\"\",\"LS\":\"12a\",\"TS\":\"1634898376948\",\"HMAC\":\"\",\"SG\":\"\"}\n");

	}

	@Test
	void parseLSKNotBase64EncodedFails() {
		parseInvalidLogLineMetadataThrows("{\"LSK\":\"a\",\"ESK\":\"\",\"PHMAC\":\"\",\"LS\":\"12\",\"TS\":\"1634898376948\",\"HMAC\":\"\",\"SG\":\"\"}\n");
	}

	@Test
	void parseESKNotBase64EncodedFails() {
		parseInvalidLogLineMetadataThrows("{\"LSK\":\"\",\"ESK\":\"a\",\"PHMAC\":\"\",\"LS\":\"12\",\"TS\":\"1634898376948\",\"HMAC\":\"\",\"SG\":\"\"}\n");
	}

	@Test
	void parsePHMACNotBase64EncodedFails() {
		parseInvalidLogLineMetadataThrows("{\"LSK\":\"\",\"ESK\":\"\",\"PHMAC\":\"a\",\"LS\":\"12\",\"TS\":\"1634898376948\",\"HMAC\":\"\",\"SG\":\"\"}\n");
	}

	@Test
	void parseHMACNotBase64EncodedFails() {
		parseInvalidLogLineMetadataThrows("{\"LSK\":\"\",\"ESK\":\"\",\"PHMAC\":\"\",\"LS\":\"12\",\"TS\":\"1634898376948\",\"HMAC\":\"a\",\"SG\":\"\"}\n");
	}

	@Test
	void parseSignatureNotBase64EncodedFails() {
		parseInvalidLogLineMetadataThrows("{\"LSK\":\"\",\"ESK\":\"\",\"PHMAC\":\"\",\"LS\":\"12\",\"TS\":\"1634898376948\",\"HMAC\":\"\",\"SG\":\"a\"}\n");
	}

	//Regular logs
	@Test
	void parseMissingTSThrows() {
		parseInvalidLogLineMetadataThrows("{\"HMAC\":\"\",\"LC\":\"0\"}\n");
	}

	@Test
	void parseMissingHMACThrows() {
		parseInvalidLogLineMetadataThrows("{\"TS\":\"1634898376926\",\"LC\":\"0\"}\n");
	}

	@Test
	void parseMissingLCOk() {
		parseValidMetadataDoesNotThrow("{\"TS\":\"1634898376926\",\"HMAC\":\"\",\"LC\":\"0\"}\n");
	}

	@Test
	void parseTSNotIntegerThrows() {
		parseInvalidLogLineMetadataThrows("{\"TS\":\"1634898376926a\",\"HMAC\":\"\",\"LC\":\"0\"}\n");
	}

	@Test
	void parseHMACNotBase64EncodedThrows() {
		parseInvalidLogLineMetadataThrows("{\"TS\":\"1634898376926\",\"HMAC\":\"a\",\"LC\":\"0\"}\n");
	}

	@Test
	void parseLCNotIntegerThrows() {
		parseInvalidLogLineMetadataThrows("{\"TS\":\"1634898376926\",\"HMAC\":\"\",\"LC\":\"a\"}\n");
	}

	@Test
	void parseLogLineWithJsonCharacterDoesntThrow() {
		String message = "{}";
		String metadata = TestData.CHECKPOINT_METADATA;
		assertDoesNotThrow(() -> logLineParser.parse(message + metadata));
	}

	@Test
	void parseLogLineWithAdditionalMetadataFieldThrows() {
		parseInvalidLogLineMetadataThrows("{\"TS\":\"1634898376926\",\"HMAC\":\"\",\"LC\":\"0\",\"TEST\":\"\"}\n");
	}

	//Utilities
	private void parseValidMetadataDoesNotThrow(String s) {
		String invalidLogLine = "This is a test" + s;
		assertDoesNotThrow(() -> logLineParser.parse(invalidLogLine));
	}

	void parseInvalidLogLineMetadataThrows(final String metadata) {
		String invalidLogLine = "This is a test" + metadata;
		assertThrows(MalformedFileException.class, () -> logLineParser.parse(invalidLogLine));
	}
}
