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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class PathHelper {
	private PathHelper() {
		// private constructor, use static
	}

	public static File[] listDirectories(Path path) {
		if (path != null && path.toFile().isDirectory()) {
			return path.toFile().listFiles(File::isDirectory);
		} else {
			throw new IllegalArgumentException("Given path is not a directory :" + path);
		}
	}

	public static File[] getFiles(File inputDirectory, String filenamePattern) throws FileNotFoundException {
		File[] file = getFilesInternal(inputDirectory, filenamePattern, false).toArray(new File[] {});
		if (file.length == 0) {
			throw new FileNotFoundException(filenamePattern);
		} else {
			return file;
		}
	}

	public static List<File> getFiles(File inputDirectory, String filenamePattern, boolean recursive) throws FileNotFoundException {
		List<File> result = getFilesInternal(inputDirectory, filenamePattern, recursive);
		if (result.isEmpty()) {
			throw new FileNotFoundException(filenamePattern);
		}
		return result;
	}

	private static List<File> getFilesInternal(File inputDirectory, String filenamePattern, boolean recursive) {
		List<File> result = new ArrayList<>();

		File[] files = inputDirectory.listFiles((dir, name) -> name.matches(filenamePattern));
		if (files != null) {
			result.addAll(Arrays.stream(files).filter(File::isFile).collect(Collectors.toList()));
		}

		if (recursive) {
			for (File directory : listDirectories(inputDirectory.toPath())) {
				result.addAll(getFilesInternal(directory, filenamePattern, true));
			}
		}
		return result;
	}

	public static File getFile(File inputDirectory, String filenamePattern) throws FileNotFoundException {
		File[] file = getFiles(inputDirectory, filenamePattern);
		if (file.length > 1) {
			throw new InvalidParameterException(String.format("more than one file found, filename is not specific enough. Dir:%s " +
					"filenamePattern:%s ", inputDirectory, filenamePattern));
		} else {
			return file[0];
		}
	}

	/**
	 * Search for a single file matching the {@code filenamePattern} and returns its {@link Path}. If none or multiple matching files are found,
	 * exception are thrown.
	 *
	 * @param startingPath    The path at which to start the search.
	 * @param maxDepth        The maximum depth to go in the path tree.
	 * @param filenamePattern The file pattern to search for.
	 * @return The {@link Path} of the file matching the given pattern.
	 * @throws IOException              If the {@code startingPath} is not found.
	 * @throws IllegalArgumentException If more than one file matching the pattern are found.
	 * @throws NoSuchFileException      If no file matching the pattern is found.
	 */
	public static Path getPath(Path startingPath, int maxDepth, String filenamePattern) throws IOException {
		List<Path> paths = Files.find(startingPath, maxDepth,
				(path, attributes) -> Files.isRegularFile(path) && path.getFileName().toString().matches(filenamePattern))
				.collect(Collectors.toList());
		if (paths.size() > 1) {
			throw new InvalidParameterException(String.format("More than one file found, filename is not specific enough. Starting " +
					"path:%s filenamePattern:%s ", startingPath, filenamePattern));
		} else if (paths.isEmpty()) {
			throw new NoSuchFileException(String.format("No file found with given pattern. Starting path: %s " +
					"filenamePattern:%s ", startingPath, filenamePattern));
		} else {
			return paths.get(0);
		}
	}

	/**
	 * Search for a list of {@link Path} matching the {@code filenamePattern}. If no files are found, an exception is thrown.
	 *
	 * @param startingPath    The path at which to start the search.
	 * @param maxDepth        The maximum depth to go in the path tree.
	 * @param filenamePattern The file pattern to search for.
	 * @return The list of {@link Path} of the files matching the given pattern.
	 * @throws IOException         If the {@code startingPath} is not found.
	 * @throws NoSuchFileException If no files matching the pattern are found.
	 */
	public static List<Path> getPaths(Path startingPath, int maxDepth, String filenamePattern) throws IOException {
		final List<Path> paths = Files.find(startingPath, maxDepth,
				(path, attributes) -> Files.isRegularFile(path) && path.getFileName().toString().matches(filenamePattern))
				.collect(Collectors.toList());
		if (paths.isEmpty()) {
			throw new NoSuchFileException(String.format("No files found with given pattern. Starting path:%s " +
					"filenamePattern:%s ", startingPath, filenamePattern));
		}
		return paths;
	}

}
