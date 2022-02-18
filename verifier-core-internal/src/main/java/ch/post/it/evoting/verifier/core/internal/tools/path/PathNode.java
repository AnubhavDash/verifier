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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathNode {

	private final List<Path> paths;
	private final Path firstPath;
	private final StructureNode structureNode;

	PathNode(List<Path> path, StructureNode structureNode) {
		this.paths = path;
		this.firstPath = path.get(0);
		this.structureNode = structureNode;
	}

	/**
	 * Provide the first {@link Path} of the path node.
	 *
	 * @return The first path
	 */
	public Path getPath() {
		return firstPath;
	}

	/**
	 * Provide a list of {@link Path} for path node build on regex name.
	 *
	 * @return The list of paths
	 */
	public List<Path> getRegexPaths() {
		return paths;
	}

	public Path getRegexPath(String value) {
		return paths.stream()
				.filter(path -> path.getFileName().toString().equals(value))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException(String.format("Asked directory / file does not exist: %s", value)));
	}

	public Path getRelation(RelationType relationType) {
		if (Files.isDirectory(firstPath)) {
			throw new IllegalArgumentException(String.format("PathNode is a directory: %s", firstPath.getFileName()));
		}
		for (RelationType r : structureNode.getRelations()) {
			if (r.equals(relationType)) {
				return firstPath.resolveSibling(firstPath.getFileName().toString() + r.toFileExtension());
			}
		}
		throw new IllegalArgumentException(String.format("Asked relation does not exist: %s", relationType));
	}

	/**
	 * In the case where this PathNode represents several files (determined by regex), obtain the relation for the file at the specified {@code
	 * index}.
	 *
	 * @param relationType The {@link RelationType} to get.
	 * @param regexPath    The path to get the relation.
	 * @return The relation for the path at specified index.
	 */
	public Path getRelation(RelationType relationType, Path regexPath) {
		if (paths.stream().anyMatch(Files::isDirectory)) {
			throw new IllegalArgumentException("PathNode is a directory.");
		}
		for (RelationType r : structureNode.getRelations()) {
			if (r.equals(relationType)) {
				final var relationPathString = regexPath.getFileName().toString() + relationType.toFileExtension();
				return regexPath.resolveSibling(relationPathString);
			}
		}
		throw new IllegalArgumentException(String.format("Asked relation does not exist: %s", relationType));
	}

	public List<Path> getSubDirectories() throws IOException {
		if (Files.isDirectory(firstPath)) {
			try (final Stream<Path> walk = Files.walk(firstPath, 1)) {
				return walk.filter(p -> Files.isDirectory(p) && !firstPath.equals(p)).collect(Collectors.toCollection(ArrayList::new));
			}
		} else {
			throw new IllegalArgumentException("The PathNode does not represent a directory.");
		}
	}

	public List<Path> getSubFiles() throws IOException {
		if (Files.isDirectory(firstPath)) {
			try (final Stream<Path> walk = Files.walk(firstPath, 1)) {
				return walk.filter(p -> Files.isRegularFile(p) && !firstPath.equals(p)).collect(Collectors.toCollection(ArrayList::new));
			}
		} else {
			throw new IllegalArgumentException("The PathNode does not represent a directory.");
		}
	}

	public String getQualifier() {
		return structureNode.getQualifier();
	}

}
