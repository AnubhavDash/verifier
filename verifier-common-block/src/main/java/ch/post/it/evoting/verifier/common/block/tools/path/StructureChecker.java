package ch.post.it.evoting.verifier.common.block.tools.path;

import com.fasterxml.jackson.databind.JsonNode;

class StructureChecker {

	static void process(JsonNode node) {
		verifyNodeIntegrity(node);
		// TODO verify business structure.
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
				throw new RuntimeException(String.format("Type does not exist: %s", type.asText()));
			}

			switch (pathType) {
			case FILE:
				checkFileNodeIntegrity(node);
				break;
			case DIRECTORY:
				checkFolderIntegrity(node);
				verifyNodeIntegrity(node.path("content"));
				break;
			case DYNAMIC_DIRECTORY:
				checkFolderLoopIntegrity(node);
				verifyNodeIntegrity(node.path("content"));
				break;
			}
		}
	}

	private static void checkFolderLoopIntegrity(JsonNode node) {
		final JsonNode content = node.path("content");
		checkNodeMissing(content, "content", node.path("type").asText());
	}

	private static void checkFolderIntegrity(JsonNode node) {
		final JsonNode key = node.path("key");
		final JsonNode name = node.path("name");
		final JsonNode content = node.path("content");

		final String type = node.path("type").asText();
		checkNodeMissing(key, "key", type);
		checkNodeMissing(name, "name", type);
		checkNodeMissing(content, "content", type);

	}

	private static void checkFileNodeIntegrity(JsonNode node) {
		final JsonNode key = node.path("key");
		final JsonNode name = node.path("name");
		final JsonNode relations = node.path("relations");
		for (JsonNode relation : relations) {
			try {
				RelationType.valueOf(relation.asText());
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(String.format("Type does not exist: %s", relation.asText()));
			}
		}

		final String type = node.path("type").asText();
		checkNodeMissing(key, "key", type);
		checkNodeMissing(name, "name", type);
	}

	private static void checkNodeMissing(JsonNode node, String field, String type) {
		if (node.isMissingNode()) {
			throw new RuntimeException(String.format("Mandatory %s is missing in %s node.", field, type));
		}
	}

}
