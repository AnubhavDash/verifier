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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;

import net.lingala.zip4j.ZipFile;

class DatasetServiceTest {

	private final DatasetService datasetService = new DatasetService(DomainObjectMapper.getNewInstance(), new DirectoryService(),
			new XmlFileRepository<>());
	@TempDir
	private Path tempDirectory;

	@Test
	void testUnpack() throws IOException {
		final String zipName = "tempzip.zip";
		final String filename1 = "file1.txt";
		final String filename2 = "file2.txt";

		try (ZipFile zf = new ZipFile(tempDirectory.resolve(zipName).toFile())) {
			Path file1 = Files.createFile(tempDirectory.resolve(filename1));
			zf.addFile(file1.toFile());
			Path file2 = Files.createFile(tempDirectory.resolve(filename2));
			zf.addFile(file2.toFile());
		}

		Dataset ds = new Dataset(Files.readAllBytes(tempDirectory.resolve(zipName)));
		Dataset result = datasetService.unpack(ds);

		assertTrue(result.getUnpackFolder().isPresent());
		assertTrue(Files.exists(result.getUnpackFolder().get().resolve(filename1)));
		assertTrue(Files.exists(result.getUnpackFolder().get().resolve(filename2)));

		assertFalse(Files.exists(result.getUnpackFolder().get().resolve(zipName)));
	}

}
