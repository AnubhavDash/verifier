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
package ch.post.it.evoting.verifier.backend.tools.path;

import java.nio.file.Path;
import java.util.List;

public class PathNode {

	private final List<Path> paths;
	private final Path firstPath;

	PathNode(List<Path> path) {
		this.paths = path;
		this.firstPath = path.get(0);
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

}
