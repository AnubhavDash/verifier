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
package ch.post.it.evoting.verifier.backend.tools;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

public class Dataset {

	private static final String DATASET_DEFAULT_FILENAME_FORMAT = "dataset-%s.zip";

	private final Path datasetPath;
	private final DatasetType expectedType;
	private Path unpackFolder;
	private boolean isUnpacked;
	private DatasetType actualType;

	public Dataset(final InputStream inputStream, final Path unpackFolder, final DatasetType expectedType) {
		checkNotNull(inputStream);
		checkNotNull(unpackFolder);
		checkArgument(Files.exists(unpackFolder));
		checkNotNull(expectedType);

		final String datasetDefaultFilename = String.format(DATASET_DEFAULT_FILENAME_FORMAT, expectedType.name().toLowerCase(Locale.ENGLISH));

		this.datasetPath = unpackFolder.resolve(datasetDefaultFilename);
		this.unpackFolder = unpackFolder;
		this.isUnpacked = false;
		this.expectedType = expectedType;
		this.actualType = null; // only set actual dataset type when unpacking.

		try {
			Files.copy(inputStream, datasetPath, StandardCopyOption.REPLACE_EXISTING);
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

	public String getExpectedType() {
		return expectedType.name().toLowerCase(Locale.ENGLISH);
	}

	public DatasetType getActualType() {
		return actualType;
	}

	public void setActualType(final DatasetType datasetType) {
		checkNotNull(datasetType);
		checkState(datasetType.equals(expectedType), "The given zip does not correspond to a %s dataset.", getExpectedType());
		this.actualType = datasetType;
	}

	public void removeUnpackFolder() {
		this.unpackFolder = null;
		this.isUnpacked = false;
	}
}
