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
package ch.post.it.evoting.verifier.backend.controller;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
import ch.post.it.evoting.verifier.backend.dto.Verification;
import ch.post.it.evoting.verifier.backend.processor.VerifierProcessor;
import ch.post.it.evoting.verifier.backend.tools.DatasetExtractionException;
import ch.post.it.evoting.verifier.backend.tools.DatasetType;

@RestController
@RequestMapping("/api/")
public class VerifierController {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifierController.class);

	private final VerifierProcessor processor;

	VerifierController(final VerifierProcessor processor) {
		this.processor = processor;
	}

	@GetMapping("/ping")
	public boolean ping() {
		return true;
	}

	@PostMapping("/clean")
	public void clean() {
		this.processor.clean();
	}

	@PostMapping("/reset")
	public void reset() {
		this.processor.resetExecution();
	}

	@PostMapping("/changeMode")
	public void changeMode() {
		this.processor.cleanSetupTally();
	}

	@GetMapping(value = "/datasetConfiguration")
	public DatasetConfiguration getDatasetConfiguration() {
		return this.processor.getDatasetConfiguration();
	}

	@PostMapping("/dataset/{datasetType}")
	public ResponseEntity<String> uploadDataset(
			@PathVariable
			final DatasetType datasetType,
			@RequestParam("file")
			final MultipartFile file) {
		checkNotNull(datasetType);

		Path temporaryDataset = null;

		try {
			temporaryDataset = createTemporaryDataset(file);
			this.processor.setDataset(file.getOriginalFilename(), datasetType, temporaryDataset);
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

	@GetMapping("/verifications")
	public List<Verification> getTestStatus() {
		return this.processor.getVerifications();
	}

	@PostMapping("/verifications")
	public ResponseEntity<String> process(
			@RequestParam()
			final String runOptions) {
		try {
			this.processor.process(runOptions);
		} catch (final IllegalArgumentException e) {
			LOGGER.error("Unable to process the verifications", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

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
