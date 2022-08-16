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
