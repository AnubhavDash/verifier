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

import lombok.Getter;

@Getter
public class StructureNode {
	private final PathType type;
	private final Path parentPath;
	private final String qualifier;
	private final boolean dynamicAncestor;
	private final List<RelationType> relations;

	StructureNode(PathType type, Path parentPath, String qualifier, boolean dynamicAncestor, List<RelationType> relations) {
		this.type = type;
		this.parentPath = parentPath;
		this.qualifier = qualifier;
		this.dynamicAncestor = dynamicAncestor;
		this.relations = relations;
	}
}
