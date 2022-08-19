package ch.post.it.evoting.verifier.backend.tools;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;

import org.springframework.stereotype.Service;

import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;

@Service
public class DatasetService {

	private final DirectoryService directoryService;

	public DatasetService(DirectoryService directoryService) {
		this.directoryService = directoryService;
	}

	public Dataset unpack(final Dataset dataset) throws IOException {
		checkNotNull(dataset, "The dataset must be not null");

		if (dataset.getUnpackFolder().isPresent()) {
			return dataset;
		}

		final Path tempDirectory = directoryService.createSecuredTemporaryDirectory("verifier-dataset");
		try (final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(dataset.getZip()))) {
			LocalFileHeader entry;
			boolean hasEntry = false;
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					hasEntry = true;
					final Path fileLocation = tempDirectory.resolve(entry.getFileName());
					if (!Files.exists(fileLocation.getParent())) {
						Files.createDirectories(fileLocation.getParent());
					}
					final byte[] buffer = new byte[1024];
					try (final FileOutputStream fos = new FileOutputStream(fileLocation.toFile())) {
						int len;
						while ((len = zis.read(buffer)) != -1) {
							fos.write(buffer, 0, len);
						}
					}
				}
			}
			if (!hasEntry) {
				throw new InvalidParameterException("input is not a ZIP file or is empty.");
			}
		}
		dataset.setUnpackFolder(tempDirectory);
		return dataset;
	}

	public void clean(final Dataset dataset) {
		checkNotNull(dataset);

		dataset.getUnpackFolder().ifPresent(unpackFolder -> {
			directoryService.deleteTemporaryDirectory(unpackFolder);
			dataset.removeUnpackFolder();
		});
	}
}
