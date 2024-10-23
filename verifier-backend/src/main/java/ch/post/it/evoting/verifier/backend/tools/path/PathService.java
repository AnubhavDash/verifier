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

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.verifier.backend.tools.path.StructureConstants.STRUCTURE_CONTENT;
import static ch.post.it.evoting.verifier.backend.tools.path.StructureConstants.STRUCTURE_KEY;
import static ch.post.it.evoting.verifier.backend.tools.path.StructureConstants.STRUCTURE_NAME;
import static ch.post.it.evoting.verifier.backend.tools.path.StructureConstants.STRUCTURE_TYPE;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet;

@Service
public class PathService {
	private final Map<StructureKey, StructureNode> structureMap = new EnumMap<>(StructureKey.class);
	private final JsonNode rootNode;

	public PathService() {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			rootNode = mapper.readTree(getClass().getResource("/dataset_structure.json"));

			StructureChecker.process(rootNode);

			addMapEntry(rootNode, Paths.get(""));

		} catch (final IOException e) {
			throw new IllegalArgumentException("Impossible to find/read structure file.");
		}
	}

	/**
	 * Obtain file or directory path from the specified {@link StructureKey}.
	 *
	 * @param structureKey The file or directory to obtain.
	 * @param rootPath     The root path where the dataset lies.
	 * @return The path for the {@code datasetKey}.
	 */
	public PathNode buildFromRootPath(final StructureKey structureKey, final Path rootPath) {
		checkNotNull(structureKey);
		checkNotNull(rootPath);

		final StructureNode structureNode = getStructureNode(structureKey);

		// If the file/folder has a dynamic part in its path, the dynamic part has to be specified.
		if (structureNode.dynamicAncestor()) {
			throw new IllegalArgumentException(String.format("The file/directory %s is contained in a folder with a dynamic name. Please use " +
							"PathService.buildFromDynamicAncestorPath(FileTreeKey fileTreeKey, Path dynamicPath, Path inputDirectoryPath).",
					structureNode.qualifier()));
		}

		// Combine input path with file/directory parent path.
		final Path combined = rootPath.resolve(structureNode.parentPath());

		try {
			return new PathNode(resolve(combined, structureNode));
		} catch (final IOException e) {
			throw new UncheckedIOException(
					String.format("File or directory path could not be obtained for key %s and root path %s.", structureKey, rootPath), e);
		}
	}

	/**
	 * Obtain file or directory path from the specified {@link StructureKey}.
	 *
	 * @param structureKey The file or directory to obtain.
	 * @param dynamicPath  The dynamic part of the path.
	 * @return The path for the {@code datasetKey}.
	 */
	public PathNode buildFromDynamicAncestorPath(final StructureKey structureKey, final Path dynamicPath) {
		checkNotNull(structureKey);
		checkNotNull(dynamicPath);

		final StructureNode structureNode = getStructureNode(structureKey);

		// Check if asked key is really dynamic.
		if (!structureNode.dynamicAncestor()) {
			throw new IllegalArgumentException(String.format("The file/directory %s is not contained in a folder with a dynamic name. Please use " +
					"PathService.buildFromRootPath(FileTreeKey fileTreeKey, Path rootPath).", structureNode.qualifier()));
		}

		// dynamicPath is already absolute
		try {
			return new PathNode(resolve(dynamicPath, structureNode));
		} catch (final IOException e) {
			throw new UncheckedIOException(
					String.format("File or directory path could not be obtained for key %s and dynamic path %s.", structureKey, dynamicPath), e);
		}
	}

	/**
	 * Provide a {@link StructureNode} form the build structure map.
	 *
	 * @param structureKey The key of the map.
	 * @return the {@code structureNode}.
	 */
	public StructureNode getStructureNode(final StructureKey structureKey) {
		checkNotNull(structureKey);
		return structureMap.get(structureKey);
	}

	/**
	 * @param structureKey The file to obtain.
	 * @param fileName     The file to verify.
	 * @return if the file matches the structure key pattern return true, false otherwise.
	 */
	public boolean matchesStructureKey(final StructureKey structureKey, final String fileName) {
		checkNotNull(structureKey);
		checkNotNull(fileName);

		final StructureNode structureNode = getStructureNode(structureKey);
		checkArgument(structureNode.type().equals(PathType.FILE), "The given structure key does not correspond to a file. [structureKey: %s]",
				structureKey);

		final String fileSeparatorRegex = "\\" + File.separator;
		final String parentPathRegex = structureNode.parentPath().toString().replace(File.separator, fileSeparatorRegex);
		final String filePathRegex = String.join(fileSeparatorRegex, parentPathRegex, structureNode.qualifier());
		final Pattern pattern = Pattern.compile(filePathRegex);

		return pattern.matcher(fileName.replace('/', File.separatorChar)).matches();
	}

	/**
	 * @param structureKey The file to obtain.
	 * @param fileName     The file to verify.
	 * @return the regex group from which the file matched.
	 */
	public String getRegexGroup(final StructureKey structureKey, final String fileName, final int groupIndex) {
		checkNotNull(structureKey);
		checkNotNull(fileName);

		final Pattern pattern = Pattern.compile(getStructureNode(structureKey).qualifier());
		final Matcher matcher = pattern.matcher(fileName);

		if (!matcher.matches()) {
			throw new IllegalStateException("File name not matching pattern.");
		}

		return matcher.group(groupIndex);
	}

	/**
	 * @param structureKey the start {@link StructureKey}.
	 * @return the set of dataset file's structure.
	 */
	@Cacheable("datasetStructureKeys")
	public ImmutableSet<StructureKey> getDatasetFilesStructureKeys(final StructureKey structureKey) {
		checkNotNull(structureKey);

		final Map<StructureKey, StructureNode> datasetStructureMap = new EnumMap<>(StructureKey.class);
		addMapEntry(datasetStructureMap, rootNode, Paths.get(""), false, structureKey.name());
		return datasetStructureMap.entrySet().stream()
				.filter(entry -> PathType.FILE.equals(entry.getValue().type()))
				.map(Map.Entry::getKey)
				.collect(ImmutableSet.toImmutableSet());
	}

	/**
	 * Check the given path contains all the dataset tree description's keys starting from the given {@link StructureKey}.
	 *
	 * @param structureKey the start {@link StructureKey}.
	 * @param path         the path to verify.
	 */
	public void checkStructureKeysExistence(final StructureKey structureKey, final Path path) {
		checkNotNull(structureKey);
		checkNotNull(path);

		StreamSupport.stream(rootNode.spliterator(), false)
				.filter(node -> structureKey.name().equals(node.path(STRUCTURE_KEY).asText()))
				.findFirst()
				.ifPresentOrElse(node -> recursiveCheckStructureKeysExistence(node.path(STRUCTURE_CONTENT), path),
						() -> {
							throw new IllegalStateException(
									String.format("No matching node for the given structure key. [structureKey: %s]", structureKey.name()));
						});
	}

	/**
	 * Start method that populates the internal {@code structureMap} from the dataset tree description. All the checks for missing nodes, wrong
	 * structure, etc... are already done when calling this method.
	 */
	private void addMapEntry(final JsonNode currentNode, final Path parentPath) {
		addMapEntry(structureMap, currentNode, parentPath, false, null);
	}

	/**
	 * Recursive method that populates the given {@code structureMap} from the {@code startKey} of the dataset tree description.
	 */
	private void addMapEntry(final Map<StructureKey, StructureNode> structureMap, final JsonNode currentNode, final Path parentPath,
			final boolean dynamicAncestor, final String startKey) {

		for (final JsonNode node : currentNode) {
			// Get the name which can be a regex.
			final String currentName = node.path(STRUCTURE_NAME).asText();

			// Register current node as long as it is not a dynamic name folder.
			final PathType type = PathType.valueOf(node.path(STRUCTURE_TYPE).asText());
			final String key = node.path(STRUCTURE_KEY).asText();
			if (startKey == null || startKey.equals(key)) {
				structureMap.put(StructureKey.valueOf(key), new StructureNode(type, parentPath, currentName, dynamicAncestor));
			}

			// If the current node is a folder or dynamic folder, recursively continue.
			if (PathType.DIRECTORY.equals(type) || PathType.DYNAMIC_DIRECTORY.equals(type)) {
				addMapEntry(
						structureMap,
						node.path(STRUCTURE_CONTENT),
						parentPath.resolve(currentName),
						dynamicAncestor || PathType.DYNAMIC_DIRECTORY.equals(type),
						startKey == null || startKey.equals(key) ? null : startKey
				);
			}
		}
	}

	/**
	 * Recursively checks the given path contains all the dataset tree description's keys.
	 */
	private void recursiveCheckStructureKeysExistence(final JsonNode currentNode, final Path parentPath) {
		for (final JsonNode node : currentNode) {

			final StructureKey structureKey = StructureKey.valueOf(node.path(STRUCTURE_KEY).asText());
			final StructureNode structureNode = getStructureNode(structureKey);
			// Building the node's path internally checks the existence of the file/directory.
			final PathNode pathNode = structureNode.dynamicAncestor() ?
					buildFromDynamicAncestorPath(structureKey, parentPath) :
					buildFromRootPath(structureKey, parentPath);

			final PathType type = structureNode.type();
			final JsonNode content = node.path(STRUCTURE_CONTENT);
			if (PathType.DYNAMIC_DIRECTORY.equals(type)) {
				pathNode.getRegexPaths().forEach(regexPath ->
						recursiveCheckStructureKeysExistence(content, regexPath)
				);
			}
			if (PathType.DIRECTORY.equals(type)) {
				recursiveCheckStructureKeysExistence(content, parentPath);
			}
		}
	}

	/**
	 * Provide a list of {@link Path} by resolving them from a starting path.
	 *
	 * @throws IOException         if an I/O error is thrown when accessing the starting file
	 * @throws NoSuchFileException if no file or directory match the name or pattern
	 */
	private ImmutableList<Path> resolve(final Path startingPath, final StructureNode structureNode) throws IOException {
		// Get the escaped (to work in regex) file system separator.
		final String quotedSeparator = Pattern.quote(startingPath.getFileSystem().getSeparator());
		// It is assumed that in dataset_structure file the file separators are /. Now we need to replace them with file system separators
		// which are escaped to work in the regex.
		final String escapedSeparatorQualifier = structureNode.qualifier().replace("/", Matcher.quoteReplacement(quotedSeparator));
		// Prepend with separator to ensure the path starts with it. Add a $ to be sure the path ends exactly with this regex.
		final Pattern pattern = Pattern.compile(quotedSeparator + escapedSeparatorQualifier + "$");

		final ImmutableList<Path> filteredPaths;
		try (final Stream<Path> paths = Files.find(startingPath,
				10, // Arbitrary depth value, should be enough.
				(path, attributes) -> {
					// We want to match only the part after the starting path against the provided regex because the regex can be
					// specified as multi level path (folder in folder etc...).
					String currentPath = path.toString().replace(startingPath.toString(), "");

					return pattern.matcher(currentPath).matches();
				})) {
			filteredPaths = paths
					// Remove starting path itself in case it matched by accident.
					.filter(path -> !startingPath.equals(path))
					.filter(path -> PathType.FILE.equals(structureNode.type()) ? Files.isRegularFile(path) : Files.isDirectory(path))
					.collect(toImmutableList());
		}

		if (filteredPaths.isEmpty()) {
			throw new NoSuchFileException(String.format("No file or directory found with given name/pattern. Starting path: %s " +
					"namePattern:%s ", startingPath, structureNode.qualifier()));
		} else {
			return filteredPaths;
		}
	}

}
