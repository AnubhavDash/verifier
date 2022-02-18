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
package ch.post.it.evoting.verifier.core.internal.tools.path;

import com.fasterxml.jackson.databind.JsonNode;

class StructureChecker {

	private static final String CONTENT = "content";

	private StructureChecker() {
		// private constructor, use static
	}

	static void process(JsonNode node) {
		verifyNodeIntegrity(node);
	}

	private static void verifyNodeIntegrity(JsonNode currentNode) {
		for (JsonNode node : currentNode) {
			// Check type presence.
			final JsonNode type = node.path("type");
			checkNodeMissing(type, "type", type.asText());

			// Check key presence.
			final JsonNode key = node.path("key");
			checkNodeMissing(key, "key", key.asText());

			// Check is the type is valid.
			final PathType pathType;
			try {
				pathType = PathType.valueOf(type.asText());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(String.format("Type does not exist: %s", type.asText()));
			}

			switch (pathType) {
			case FILE:
				checkFileNodeIntegrity(node);
				break;
			case DIRECTORY:
				checkFolderIntegrity(node);
				verifyNodeIntegrity(node.path(CONTENT));
				break;
			case DYNAMIC_DIRECTORY:
				checkFolderLoopIntegrity(node);
				verifyNodeIntegrity(node.path(CONTENT));
				break;
			}
		}
	}

	private static void checkFolderLoopIntegrity(JsonNode node) {
		final JsonNode content = node.path(CONTENT);
		checkNodeMissing(content, CONTENT, node.path("type").asText());
	}

	private static void checkFolderIntegrity(JsonNode node) {
		final JsonNode key = node.path("key");
		final JsonNode name = node.path("name");
		final JsonNode content = node.path(CONTENT);

		final String type = node.path("type").asText();
		checkNodeMissing(key, "key", type);
		checkNodeMissing(name, "name", type);
		checkNodeMissing(content, CONTENT, type);

	}

	private static void checkFileNodeIntegrity(JsonNode node) {
		final JsonNode key = node.path("key");
		final JsonNode name = node.path("name");
		final JsonNode relations = node.path("relations");
		for (JsonNode relation : relations) {
			try {
				RelationType.valueOf(relation.asText());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(String.format("Type does not exist: %s", relation.asText()));
			}
		}

		final String type = node.path("type").asText();
		checkNodeMissing(key, "key", type);
		checkNodeMissing(name, "name", type);
	}

	private static void checkNodeMissing(JsonNode node, String field, String type) {
		if (node.isMissingNode()) {
			throw new IllegalArgumentException(String.format("Mandatory %s is missing in %s node.", field, type));
		}
	}

}
