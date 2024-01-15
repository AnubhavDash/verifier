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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PathService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PathService.class);

	private final Map<StructureKey, StructureNode> structureMap = new EnumMap<>(StructureKey.class);

	public PathService() {
		try {

			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode rootNode = mapper.readTree(getClass().getResource("/dataset_structure.json"));

			StructureChecker.process(rootNode);

			addMapEntry(rootNode, Paths.get(""), false);

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
	 * @param structureKey The file or directory to obtain.
	 * @param rootPath     The root path where the dataset lies.
	 * @return if the file or directory to obtain exists in the dataset return true, otherwise false.
	 */
	public boolean existsFromRootPath(final StructureKey structureKey, final Path rootPath) {
		checkNotNull(structureKey);
		checkNotNull(rootPath);

		final StructureNode structureNode = getStructureNode(structureKey);

		// Combine input path with file/directory parent path.
		final Path combined = rootPath.resolve(structureNode.parentPath());

		if (!Files.exists(combined)) {
			LOGGER.debug("Parent node could not be found. [structureKey: {}, rootPath: {}]", structureKey, rootPath);
			return false;
		}

		final List<Path> pathList;
		try {
			pathList = resolve(combined, structureNode);
		} catch (final NoSuchFileException e) {
			LOGGER.debug(String.format("File could not be found. [structureKey: %s, rootPath: %s]", structureKey, rootPath), e);
			return false;
		} catch (final IOException e) {
			LOGGER.debug(String.format("An unexpected IOException occurred. [structureKey: %s, rootPath: %s]", structureKey, rootPath), e);
			return false;
		}

		return !pathList.isEmpty();
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

		return pattern.matcher(fileName).matches();
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
	 * Recursive method that populate the internal structureMap from the dataset tree description. All the checks for missing nodes, wrong structure,
	 * etc... are already done when calling this method.
	 */
	private void addMapEntry(final JsonNode currentNode, final Path parentPath, final boolean dynamicAncestor) {
		for (final JsonNode node : currentNode) {
			// Get the name which can be a regex.
			final String currentName = node.path("name").asText();

			// Register current node as long as it is not a dynamic name folder.
			final PathType type = PathType.valueOf(node.path("type").asText());
			structureMap.put(StructureKey.valueOf(node.path("key").asText()), new StructureNode(type, parentPath, currentName, dynamicAncestor));

			// If the current node is a folder or dynamic folder, recursively continue.
			if (PathType.DIRECTORY.equals(type) || PathType.DYNAMIC_DIRECTORY.equals(type)) {
				addMapEntry(node.path("content"), parentPath.resolve(currentName),
						dynamicAncestor || PathType.DYNAMIC_DIRECTORY.equals(type));
			}
		}
	}

	/**
	 * Provide a list of {@link Path} by resolving them from a starting path.
	 *
	 * @throws IOException         if an I/O error is thrown when accessing the starting file
	 * @throws NoSuchFileException if no file or directory match the name or pattern
	 */
	private List<Path> resolve(final Path startingPath, final StructureNode structureNode) throws IOException {
		// Get the escaped (to work in regex) file system separator.
		final String quotedSeparator = Pattern.quote(startingPath.getFileSystem().getSeparator());
		// It is assumed that in dataset_structure file the file separators are /. Now we need to replace them with file system separators
		// which are escaped to work in the regex.
		final String escapedSeparatorQualifier = structureNode.qualifier().replace("/", Matcher.quoteReplacement(quotedSeparator));
		// Prepend with separator to ensure the path starts with it. Add a $ to be sure the path ends exactly with this regex.
		final Pattern pattern = Pattern.compile(quotedSeparator + escapedSeparatorQualifier + "$");

		final List<Path> filteredPaths;
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
					.toList();
		}

		if (filteredPaths.isEmpty()) {
			throw new NoSuchFileException(String.format("No file or directory found with given name/pattern. Starting path: %s " +
					"namePattern:%s ", startingPath, structureNode.qualifier()));
		} else {
			return filteredPaths;
		}
	}

}
