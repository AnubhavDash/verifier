package ch.post.it.evoting.verifier.backend.tools;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.lingala.zip4j.ZipFile;

class DatasetServiceTest {

	@TempDir
	public Path tempDirectory;

	public final DatasetService datasetService = new DatasetService(new DirectoryService());

	@Test
	void testUnpack() throws IOException {
		final String zipName = "tempzip.zip";
		final String filename1 = "file1.txt";
		final String filename2 = "file2.txt";

		ZipFile zf = new ZipFile(tempDirectory.resolve(zipName).toFile());
		Path file1 = Files.createFile(tempDirectory.resolve(filename1));
		zf.addFile(file1.toFile());
		Path file2 = Files.createFile(tempDirectory.resolve(filename2));
		zf.addFile(file2.toFile());


		Dataset ds = new Dataset(Files.readAllBytes(tempDirectory.resolve(zipName)));
		Dataset result = datasetService.unpack(ds);

		assertTrue(result.getUnpackFolder().isPresent());
		assertTrue(Files.exists(result.getUnpackFolder().get().resolve(filename1)));
		assertTrue(Files.exists(result.getUnpackFolder().get().resolve(filename2)));

		assertFalse(Files.exists(result.getUnpackFolder().get().resolve(zipName)));
	}
}
