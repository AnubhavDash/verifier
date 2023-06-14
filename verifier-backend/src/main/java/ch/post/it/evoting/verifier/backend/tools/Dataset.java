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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Dataset {

	private static final String DATASET_DEFAULT_FILENAME = "dataset.zip";

	private final Path datasetPath;
	private Path unpackFolder;
	private boolean isUnpacked;

	public Dataset(final InputStream inputStream, final Path unpackFolder) {
		checkNotNull(inputStream);
		checkNotNull(unpackFolder);
		checkArgument(Files.exists(unpackFolder));

		this.datasetPath = unpackFolder.resolve(DATASET_DEFAULT_FILENAME);
		this.unpackFolder = unpackFolder;
		this.isUnpacked = false;

		try {
			Files.copy(inputStream, datasetPath);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public InputStream newInputStream() throws IOException {
		return new BufferedInputStream(Files.newInputStream(datasetPath));
	}

	public Path getUnpackFolder() {
		return this.unpackFolder;
	}

	public boolean isUnpacked() {
		return this.isUnpacked;
	}

	public void setUnpacked(final boolean unpacked) {
		this.isUnpacked = unpacked;
	}

	public void removeUnpackFolder() {
		this.unpackFolder = null;
		this.isUnpacked = false;
	}

}
