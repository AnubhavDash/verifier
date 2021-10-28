/*
 * Copyright 2021 Post CH Ltd
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
package ch.post.it.evoting.verifier.common.block.tools.path;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PathService {

	private final Map<StructureKey, StructureNode> structureMap = new HashMap<>();

	public PathService() {
		try {

			ObjectMapper mapper = new ObjectMapper();
			final JsonNode rootNode = mapper.readTree(getClass().getResource("/dataset_structure.json"));

			StructureChecker.process(rootNode);

			addMapEntry(rootNode, Paths.get(""), false);

		} catch (IOException e) {
			throw new RuntimeException("Impossible to find/read structure file.");
		}
	}

	/**
	 * Obtain file or directory path from the specified {@link StructureKey}.
	 *
	 * @param structureKey The file or directory to obtain.
	 * @param rootPath     The root path where the dataset lies.
	 * @return The path for the {@code datasetKey}.
	 * @throws IOException If the path can not be obtained.
	 */
	public PathNode buildFromRootPath(StructureKey structureKey, Path rootPath) throws IOException {
		final StructureNode structureNode = getStructureNode(structureKey);

		// If the file/folder has a dynamic part in its path, the dynamic part has to be specified.
		if (structureNode.isDynamicAncestor()) {
			throw new RuntimeException(String.format("The file/directory %s is contained in a folder with a dynamic name. Please use " +
							"PathService.buildFromDynamicAncestorPath(FileTreeKey fileTreeKey, Path dynamicPath, Path inputDirectoryPath).",
					structureNode.getQualifier()));
		}

		// Combine input path with file/directory parent path.
		final Path combined = rootPath.resolve(structureNode.getParentPath());

		return new PathNode(resolve(combined, structureNode), structureNode);
	}

	/**
	 * Obtain file or directory path from the specified {@link StructureKey}.
	 *
	 * @param structureKey The file or directory to obtain.
	 * @param dynamicPath  The dynamic part of the path.
	 * @return The path for the {@code datasetKey}.
	 * @throws IOException If the path can not be obtained.
	 */
	public PathNode buildFromDynamicAncestorPath(StructureKey structureKey, Path dynamicPath) throws IOException {
		final StructureNode structureNode = getStructureNode(structureKey);

		// Check if asked key is really dynamic.
		if (!structureNode.isDynamicAncestor()) {
			throw new RuntimeException(String.format("The file/directory %s is not contained in a folder with a dynamic name. Please use " +
					"PathService.buildFromRootPath(FileTreeKey fileTreeKey, Path rootPath).", structureNode.getQualifier()));
		}

		// dynamicPath is already absolute
		return new PathNode(resolve(dynamicPath, structureNode), structureNode);
	}

	/**
	 * Provide a {@link StructureNode} form the build structure map.
	 *
	 * @param structureKey The key of the map.
	 * @return the {@code structureNode}.
	 */
	public StructureNode getStructureNode(StructureKey structureKey) {
		return structureMap.get(structureKey);
	}

	/**
	 * Recursive method that populate the internal structureMap from the dataset tree description. All the checks for missing nodes, wrong structure,
	 * etc... are already done when calling this method.
	 */
	private void addMapEntry(JsonNode currentNode, Path parentPath, boolean dynamicAncestor) {
		for (JsonNode node : currentNode) {
			// Get the name which can be a regex.
			String currentName = node.path("name").asText();

			// Register current node as long as it is not a dynamic name folder.
			final PathType type = PathType.valueOf(node.path("type").asText());
			if (PathType.FILE.equals(type)) {
				// Check if there are defined relations and add them.
				final JsonNode relationsNode = node.path("relations");
				List<RelationType> relations = getRelations(relationsNode);
				// Add PathNode to the map
				structureMap.put(StructureKey.valueOf(node.path("key").asText()), new StructureNode(type, parentPath, currentName,
						dynamicAncestor, relations));
			} else {
				structureMap.put(StructureKey.valueOf(node.path("key").asText()), new StructureNode(type, parentPath, currentName,
						dynamicAncestor, null));
			}

			// If the current node is a folder or dynamic folder, recursively continue.
			if (PathType.DIRECTORY.equals(type) || PathType.DYNAMIC_DIRECTORY.equals(type)) {
				addMapEntry(node.path("content"), parentPath.resolve(currentName),
						dynamicAncestor || PathType.DYNAMIC_DIRECTORY.equals(type));
			}
		}
	}

	/**
	 * Provide the list of the {@link RelationType}.
	 */
	private List<RelationType> getRelations(JsonNode relationsNode) {
		List<RelationType> relations = new ArrayList<>();
		if (!relationsNode.isMissingNode()) {
			for (JsonNode relationNode : relationsNode) {
				relations.add(RelationType.valueOf(relationNode.asText()));
			}
		}
		return relations;
	}

	/**
	 * Provide a list of {@link Path} by resolving them from a starting path.
	 *
	 * @throws IOException if an I/O error is thrown when accessing the starting file
	 * @throws NoSuchFileException if no file or directory match the name or pattern
	 */
	private List<Path> resolve(Path startingPath, StructureNode structureNode) throws IOException {
		// Get the escaped (to work in regex) file system separator.
		final String quotedSeparator = Pattern.quote(startingPath.getFileSystem().getSeparator());
		// It is assumed that in dataset_structure file the file separators are /. Now we need to replace them with file system separators
		// which are escaped to work in the regex.
		final String escapedSeparatorQualifier = structureNode.getQualifier().replaceAll("/", Matcher.quoteReplacement(quotedSeparator));
		// Prepend with separator to ensure the path starts with it. Add a $ to be sure the path ends exactly with this regex.
		final Pattern pattern = Pattern.compile(quotedSeparator + escapedSeparatorQualifier + "$");

		List<Path> paths = Files.find(startingPath,
				10, // Arbitrary depth value, should be enough.
				(path, attributes) -> {
					// We want to match only the part after the starting path against the provided regex because the regex can be
					// specified as multi level path (folder in folder etc...).
					String currentPath = path.toString().replace(startingPath.toString(), "");

					return pattern.matcher(currentPath).matches();
				})
				// Remove starting path itself in case it matched by accident.
				.filter(path -> !startingPath.equals(path))
				.filter(path -> PathType.FILE.equals(structureNode.getType()) ? Files.isRegularFile(path) : Files.isDirectory(path))
				.collect(Collectors.toList());

		if (paths.isEmpty()) {
			throw new NoSuchFileException(String.format("No file or directory found with given name/pattern. Starting path: %s " +
					"namePattern:%s ", startingPath, structureNode.getQualifier()));
		} else {
			return paths;
		}
	}

}
