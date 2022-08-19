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
package ch.post.it.evoting.verifier.backend.tools;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.Optional;

public class Dataset {
	private final byte[] zip;
	private Optional<Path> unpackFolder;

	public Dataset(byte[] zip) {
		checkNotNull(zip, "the zip file containing the dataset must not be null");

		this.zip = zip;
		this.unpackFolder = Optional.empty();
	}

	public byte[] getZip() {
		return zip;
	}

	public Optional<Path> getUnpackFolder() {
		return unpackFolder;
	}

	public void setUnpackFolder(Path unpackFolder) {
		checkNotNull(unpackFolder, "unpackFolder cannot be null");
		this.unpackFolder = Optional.ofNullable(unpackFolder);
	}

	public void removeUnpackFolder() {
		this.unpackFolder = Optional.empty();
	}
}
