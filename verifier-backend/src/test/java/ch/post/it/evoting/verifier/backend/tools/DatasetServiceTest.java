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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.base.Throwables;

import ch.post.it.evoting.verifier.backend.tools.path.PathService;

import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;

class DatasetServiceTest {

	private static final String DATASET_CONTEXT_EXPECTED_FILE = "configuration-anonymized.xml";
	private static final String DATASET_CONTEXT_EXPECTED_FOLDER = "setup";
	private static final String DATASET_SETUP_EXPECTED_FILE = "controlComponentCodeSharesPayload.0.json";
	private static final String DATASET_SETUP_EXPECTED_FOLDER = Paths.get("setup", "verification_card_sets", "43B803449095FA47C0335A3B489FB61B")
			.toString();
	private static final String DATASET_TALLY_EXPECTED_FILE = "controlComponentBallotBoxPayload_1.json";
	private static final String DATASET_TALLY_EXPECTED_FOLDER = Paths.get("tally", "ballot_boxes", "0E65660B5AF70D18DA2D47C3F4718102").toString();

	private static DatasetService datasetService;

	@TempDir
	private static Path datasetTempDir;

	@BeforeAll
	static void setUpAll() {
		datasetService = new DatasetService(new DirectoryService(datasetTempDir), new PathService());
	}

	@TempDir
	private Path zipDirectory;

	@TempDir
	private Path folderToZip;

	@TempDir
	private Path unpackFolder;

	@ParameterizedTest
	@MethodSource()
	void testUnpackDataset(
			final DatasetType expectedType,
			final String folder,
			final String file) throws IOException {
		final String zipName = String.format("tmp-%s.zip", expectedType);
		final String pathToFile = folder + File.separator + file;

		Files.createDirectories(folderToZip.resolve(folder));
		Files.createFile(folderToZip.resolve(pathToFile));

		createDatasetZip(zipName);

		final Dataset dataset = new Dataset(Files.newInputStream(zipDirectory.resolve(zipName)), unpackFolder, expectedType);
		final Dataset result = datasetService.unpack(dataset);

		assertTrue(Files.exists(result.getUnpackFolder().resolve(pathToFile)));

		assertFalse(Files.exists(result.getUnpackFolder().resolve(zipName)));
	}

	static Stream<Arguments> testUnpackDataset() {
		return Stream.of(
				Arguments.of(DatasetType.CONTEXT, DATASET_CONTEXT_EXPECTED_FOLDER, DATASET_CONTEXT_EXPECTED_FILE),
				Arguments.of(DatasetType.SETUP, DATASET_SETUP_EXPECTED_FOLDER, DATASET_SETUP_EXPECTED_FILE),
				Arguments.of(DatasetType.TALLY, DATASET_TALLY_EXPECTED_FOLDER, DATASET_TALLY_EXPECTED_FILE)
		);
	}

	@ParameterizedTest
	@MethodSource()
	void testUnpackWrongDatasetType(
			final DatasetType expectedType,
			final String folder,
			final String file) throws IOException {
		final String zipName = String.format("tmp-%s.zip", expectedType);
		final String pathToFile = folder + File.separator + file;

		Files.createDirectories(folderToZip.resolve(folder));
		Files.createFile(folderToZip.resolve(pathToFile));

		createDatasetZip(zipName);

		final Dataset dataset = new Dataset(Files.newInputStream(zipDirectory.resolve(zipName)), unpackFolder, expectedType);

		final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> datasetService.unpack(dataset));
		assertEquals(String.format("The given zip does not correspond to a %s dataset.", expectedType.name().toLowerCase(Locale.ENGLISH)),
				Throwables.getRootCause(exception).getMessage());
	}

	static Stream<Arguments> testUnpackWrongDatasetType() {
		return Stream.of(
				Arguments.of(DatasetType.SETUP, DATASET_CONTEXT_EXPECTED_FOLDER, DATASET_CONTEXT_EXPECTED_FILE),
				Arguments.of(DatasetType.TALLY, DATASET_CONTEXT_EXPECTED_FOLDER, DATASET_CONTEXT_EXPECTED_FILE),
				Arguments.of(DatasetType.CONTEXT, DATASET_SETUP_EXPECTED_FOLDER, DATASET_SETUP_EXPECTED_FILE),
				Arguments.of(DatasetType.TALLY, DATASET_SETUP_EXPECTED_FOLDER, DATASET_SETUP_EXPECTED_FILE),
				Arguments.of(DatasetType.CONTEXT, DATASET_TALLY_EXPECTED_FOLDER, DATASET_TALLY_EXPECTED_FILE),
				Arguments.of(DatasetType.SETUP, DATASET_TALLY_EXPECTED_FOLDER, DATASET_TALLY_EXPECTED_FILE)
		);
	}

	@Test
	void testUnpackNoDatasetTypeFound() throws IOException {
		final String zipName = "tmp.zip";
		final String filename1 = "file1.txt";
		final String filename2 = "file2.txt";
		Files.createFile(folderToZip.resolve(filename1));
		Files.createFile(folderToZip.resolve(filename2));

		createDatasetZip(zipName);

		final Dataset dataset = new Dataset(Files.newInputStream(zipDirectory.resolve(zipName)), unpackFolder, DatasetType.SETUP);

		final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> datasetService.unpack(dataset));
		assertEquals("input is not a setup dataset.", Throwables.getRootCause(exception).getMessage());
	}

	private void createDatasetZip(final String zipName) throws IOException {
		final ZipParameters zipParameters = new ZipParameters();
		zipParameters.setCompressionLevel(CompressionLevel.NO_COMPRESSION);

		final Stream<Path> paths = Files.walk(folderToZip);
		final OutputStream outputStream = new FileOutputStream(zipDirectory.resolve(zipName).toFile());
		final ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

		try (paths; outputStream; zipOutputStream) {
			paths.forEach(path -> {
				if (!Files.isDirectory(path)) {
					final String zipEntryName = folderToZip.relativize(path).toString();
					try (final InputStream inputStream = Files.newInputStream(path)) {
						zipParameters.setFileNameInZip(zipEntryName);
						zipOutputStream.putNextEntry(zipParameters);
						inputStream.transferTo(zipOutputStream);
						zipOutputStream.closeEntry();
					} catch (final IOException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
	}

}
