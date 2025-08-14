/*
 * (c) Copyright 2025 Swiss Post Ltd.
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
package ch.post.it.evoting.verifier.backend.controller;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.post.it.evoting.verifier.backend.dto.DatasetConfiguration;
import ch.post.it.evoting.verifier.backend.processor.VerifierProcessor;
import ch.post.it.evoting.verifier.backend.tools.DatasetExtractionException;
import ch.post.it.evoting.verifier.backend.tools.DatasetType;

@RestController
@RequestMapping("/api/dataset")
public class DatasetController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatasetController.class);

	private final VerifierProcessor processor;

	DatasetController(final VerifierProcessor processor) {
		this.processor = processor;
	}

	@GetMapping
	public DatasetConfiguration getDatasetConfiguration() {
		return processor.getDatasetConfiguration();
	}

	@PostMapping("clean")
	public void cleanDatasets() {
		processor.cleanDatasets();
	}

	@PostMapping("{datasetType}")
	public ResponseEntity<String> uploadDataset(
			@PathVariable
			final DatasetType datasetType,
			@RequestParam("file")
			final MultipartFile file) {
		checkNotNull(datasetType);

		Path temporaryDataset = null;

		try {
			temporaryDataset = createTemporaryDataset(file);
			processor.setDataset(file.getOriginalFilename(), datasetType, temporaryDataset);
		} catch (final DatasetExtractionException e) {
			LOGGER.error("An error occurred while uploading the dataset. [datasetType: {}]", datasetType, e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} finally {
			if (temporaryDataset != null) {
				deleteTemporaryImport(temporaryDataset);
			}
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// The Verifier runs on a trusted bare metal machine with no network or internet connection.
	// Temporary file is created, used immediately, and deleted after processing.
	@SuppressWarnings("java:S5443")
	private Path createTemporaryDataset(final MultipartFile zip) {
		Path importTempPath = null;
		try {
			importTempPath = Files.createTempFile(null, null);
			zip.transferTo(importTempPath);
		} catch (final IOException e) {
			if (importTempPath != null) {
				deleteTemporaryImport(importTempPath);
			}
			throw new UncheckedIOException("Failed to create temporary import file.", e);
		}

		return importTempPath;
	}

	private void deleteTemporaryImport(final Path importTempPath) {
		try {
			Files.deleteIfExists(importTempPath);
		} catch (final IOException e) {
			throw new UncheckedIOException("Failed to delete temporary dataset file.", e);
		}
	}

}
