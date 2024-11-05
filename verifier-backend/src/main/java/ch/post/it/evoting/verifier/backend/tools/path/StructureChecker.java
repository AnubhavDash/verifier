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
package ch.post.it.evoting.verifier.backend.tools.path;

import static ch.post.it.evoting.verifier.backend.tools.path.StructureConstants.STRUCTURE_CONTENT;
import static ch.post.it.evoting.verifier.backend.tools.path.StructureConstants.STRUCTURE_KEY;
import static ch.post.it.evoting.verifier.backend.tools.path.StructureConstants.STRUCTURE_NAME;
import static ch.post.it.evoting.verifier.backend.tools.path.StructureConstants.STRUCTURE_TYPE;

import com.fasterxml.jackson.databind.JsonNode;

class StructureChecker {

	private StructureChecker() {
		// private constructor, use static
	}

	static void process(final JsonNode node) {
		verifyNodeIntegrity(node);
	}

	private static void verifyNodeIntegrity(final JsonNode currentNode) {
		for (final JsonNode node : currentNode) {
			// Check type presence.
			final JsonNode type = node.path(STRUCTURE_TYPE);
			checkNodeMissing(type, STRUCTURE_TYPE, type.asText());

			// Check key presence.
			final JsonNode key = node.path(STRUCTURE_KEY);
			checkNodeMissing(key, STRUCTURE_KEY, key.asText());

			// Check is the type is valid.
			final PathType pathType;
			try {
				pathType = PathType.valueOf(type.asText());
			} catch (final IllegalArgumentException e) {
				throw new IllegalArgumentException(String.format("Type does not exist: %s", type.asText()));
			}

			switch (pathType) {
			case FILE -> checkFileNodeIntegrity(node);
			case DIRECTORY -> {
				checkFolderIntegrity(node);
				verifyNodeIntegrity(node.path(STRUCTURE_CONTENT));
			}
			case DYNAMIC_DIRECTORY -> {
				checkFolderLoopIntegrity(node);
				verifyNodeIntegrity(node.path(STRUCTURE_CONTENT));
			}
			}
		}
	}

	private static void checkFolderLoopIntegrity(final JsonNode node) {
		final JsonNode content = node.path(STRUCTURE_CONTENT);
		checkNodeMissing(content, STRUCTURE_CONTENT, node.path(STRUCTURE_TYPE).asText());
	}

	private static void checkFolderIntegrity(final JsonNode node) {
		final JsonNode key = node.path(STRUCTURE_KEY);
		final JsonNode name = node.path(STRUCTURE_NAME);
		final JsonNode content = node.path(STRUCTURE_CONTENT);

		final String type = node.path(STRUCTURE_TYPE).asText();
		checkNodeMissing(key, STRUCTURE_KEY, type);
		checkNodeMissing(name, STRUCTURE_NAME, type);
		checkNodeMissing(content, STRUCTURE_CONTENT, type);

	}

	private static void checkFileNodeIntegrity(final JsonNode node) {
		final JsonNode key = node.path(STRUCTURE_KEY);
		final JsonNode name = node.path(STRUCTURE_NAME);

		final String type = node.path(STRUCTURE_TYPE).asText();
		checkNodeMissing(key, STRUCTURE_KEY, type);
		checkNodeMissing(name, STRUCTURE_NAME, type);
	}

	private static void checkNodeMissing(final JsonNode node, final String field, final String type) {
		if (node.isMissingNode()) {
			throw new IllegalArgumentException(String.format("Mandatory %s is missing in %s node.", field, type));
		}
	}

}
