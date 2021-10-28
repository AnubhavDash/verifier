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

/**
 * This file is part of Verifier Swiss Post.
 * <p>
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * <p>
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post. If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block2.securelog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.path.PathHelper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostMappingElement {
	private final static Function<String[], HostMappingElement> map = array -> {
		if (array == null || array.length != 2) {
			throw new IllegalArgumentException("Wrong array input format");
		}
		HostMappingElement hm = new HostMappingElement();
		hm.setHostname(array[0]);
		hm.setCc(array[1]);
		return hm;
	};
	private String hostname;
	private String cc;

	public final static Map<String, String> loadHostMapping(Path inputDirectoryPath) throws IOException {
		File mapping = PathHelper.getFile(inputDirectoryPath.resolve(Block2VerificationSuite.PATH_SECURE_LOGS).toFile(), "mapping_cc_hosts.csv");
		Iterable<HostMappingElement> iterable = Deserializer.fromCsv(mapping.getParentFile(), mapping.getName(), ";", map);
		return StreamSupport.stream(iterable.spliterator(), false)
				.skip(1)
				.collect(Collectors.toMap(HostMappingElement::getHostname, HostMappingElement::getCc));
	}
}
