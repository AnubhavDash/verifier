package ch.post.it.evoting.verifier.backend.tools;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.nio.file.attribute.AclEntryFlag.DIRECTORY_INHERIT;
import static java.nio.file.attribute.AclEntryFlag.FILE_INHERIT;
import static java.nio.file.attribute.AclEntryPermission.APPEND_DATA;
import static java.nio.file.attribute.AclEntryPermission.DELETE;
import static java.nio.file.attribute.AclEntryPermission.EXECUTE;
import static java.nio.file.attribute.AclEntryPermission.READ_ACL;
import static java.nio.file.attribute.AclEntryPermission.READ_ATTRIBUTES;
import static java.nio.file.attribute.AclEntryPermission.READ_DATA;
import static java.nio.file.attribute.AclEntryPermission.READ_NAMED_ATTRS;
import static java.nio.file.attribute.AclEntryPermission.SYNCHRONIZE;
import static java.nio.file.attribute.AclEntryPermission.WRITE_ATTRIBUTES;
import static java.nio.file.attribute.AclEntryPermission.WRITE_DATA;
import static java.nio.file.attribute.AclEntryPermission.WRITE_NAMED_ATTRS;
import static java.nio.file.attribute.AclEntryType.ALLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

@Service
public class DirectoryService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryService.class);

	public DirectoryService() {
		//intentionally left blank
	}

	/**
	 * Creates a new directory in the default temporary-file directory, using the given prefix to generate its name.
	 * The resulting Path is secured so that only the current user can access
	 * @param prefix the prefix string to be used in generating the directory's name; may be null
	 * @return the path to the newly created directory
	 * @throws IOException if an I/O error occurs or the temporary-file directory does not exist
	 */
	public Path createSecuredTemporaryDirectory(String prefix) throws IOException {
		@SuppressWarnings("java:S5443") // The security of the directory is set just after this creation
		final Path tempDirectory = Files.createTempDirectory(prefix);
		if (!secureDirectory(tempDirectory)) {
			LOGGER.warn("Unable to set the security on the temporary directory that only the current user can access.");
		}
		return tempDirectory;
	}

	/**
	 * Recursively delete the given directory
	 * @param deleteDirectory the directory to delete
	 * @throws NullPointerException if the path is null
	 * @throws IllegalArgumentException if the path is not a directory
	 */
	public void deleteTemporaryDirectory(Path deleteDirectory) {
		checkNotNull(deleteDirectory, "the path must be not null");
		checkArgument(Files.isDirectory(deleteDirectory), "Given path must be a directory");
		try {
			FileSystemUtils.deleteRecursively(deleteDirectory);
		} catch (IOException e) {
			LOGGER.warn("Unable to delete the existing temporary directory");
		}
	}

	private boolean secureDirectory(final Path path) throws IOException {
		final String POSIX_FILE_ATTRIBUTE_VIEW = "posix";
		final String ACL_FILE_ATTRIBUTE_VIEW = "acl";
		final String POSIX_USER_ONLY_PERMISSION = "rwx------";

		final Set<String> supportedFileAttributeViews = path.getFileSystem().supportedFileAttributeViews();
		if (supportedFileAttributeViews.contains(POSIX_FILE_ATTRIBUTE_VIEW)) {
			LOGGER.debug("File system supports POSIX, setting permission");
			Files.setPosixFilePermissions(path, PosixFilePermissions.fromString(POSIX_USER_ONLY_PERMISSION));
			return true;
		} else if (supportedFileAttributeViews.contains(ACL_FILE_ATTRIBUTE_VIEW)) {
			LOGGER.debug("File system supports ACL, setting permission");
			final UserPrincipal fileOwner = Files.getOwner(path);

			final AclFileAttributeView view = Files.getFileAttributeView(path, AclFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);

			final AclEntry entry = AclEntry.newBuilder()
					.setType(ALLOW)
					.setPrincipal(fileOwner)
					.setFlags(DIRECTORY_INHERIT,
							FILE_INHERIT)
					.setPermissions(WRITE_NAMED_ATTRS,
							WRITE_ATTRIBUTES,
							DELETE,
							WRITE_DATA,
							READ_ACL,
							APPEND_DATA,
							READ_ATTRIBUTES,
							READ_DATA,
							EXECUTE,
							SYNCHRONIZE,
							READ_NAMED_ATTRS)
					.build();

			final List<AclEntry> acl = List.of(entry);
			view.setAcl(acl);
			return true;
		} else {
			LOGGER.debug("File system doesn't support either POSIX nor ACL. Trying to set basic permission");
			final File file = path.toFile();
			return file.setExecutable(true) && file.setReadable(true) && file.setWritable(true);
		}
	}
}
