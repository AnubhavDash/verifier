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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet;
import ch.post.it.evoting.evotinglibraries.domain.LocalDateTimeUtils;

@Service
public class DirectoryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryService.class);

	private static final String PREFIX = "verifier-dataset";
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");

	private final Path datasetUnzipLocation;

	public DirectoryService(
			@Value("${dataset.unzip.location:}")
			final Path datasetUnzipLocation) {

		if (datasetUnzipLocation == null) {
			this.datasetUnzipLocation = Path.of(System.getProperty("java.io.tmpdir"));
		} else {
			try {
				this.datasetUnzipLocation = datasetUnzipLocation.toRealPath(LinkOption.NOFOLLOW_LINKS);
			} catch (final IOException e) {
				throw new IllegalStateException(String.format("The provided directory path does not exist. [path: %s]", datasetUnzipLocation));
			}
		}

		LOGGER.debug("Unzip location set. [path: {}]", this.datasetUnzipLocation);
	}

	/**
	 * Creates a new directory in configured directory, using a prefix and the current timestamp to generate its name. The resulting Path is secured
	 * so that only the current user can access
	 *
	 * @return the path to the newly created directory.
	 * @throws IOException if an I/O error occurs during directory creation.
	 */
	public Path createSecuredDirectory() throws IOException {
		final LocalDateTime now = LocalDateTimeUtils.now();
		final String timestamp = dateTimeFormatter.format(now);

		final Path directory = Files.createDirectory(datasetUnzipLocation.resolve(Path.of(PREFIX + "-" + timestamp)));
		if (!secureDirectory(directory)) {
			LOGGER.warn("Unable to set the security on the directory that only the current user can access.");
		}
		LOGGER.info("Unzip directory has been created. [path: {}]", directory);

		return directory;
	}

	/**
	 * Recursively delete the given directory
	 *
	 * @param directoryToDelete the directory to delete
	 * @throws NullPointerException     if the path is null
	 * @throws IllegalArgumentException if the path is not a directory
	 */
	public void deleteDirectory(final Path directoryToDelete) {
		checkNotNull(directoryToDelete);
		checkArgument(Files.isDirectory(directoryToDelete), "Given path must be a directory");
		try {
			FileSystemUtils.deleteRecursively(directoryToDelete);
			LOGGER.debug("Directory successfully deleted.");
		} catch (final IOException e) {
			LOGGER.warn("Unable to delete the existing directory");
		}
	}

	private boolean secureDirectory(final Path path) throws IOException {
		final String POSIX_FILE_ATTRIBUTE_VIEW = "posix";
		final String ACL_FILE_ATTRIBUTE_VIEW = "acl";
		final String POSIX_USER_ONLY_PERMISSION = "rwx------";

		final ImmutableSet<String> supportedFileAttributeViews = ImmutableSet.from(path.getFileSystem().supportedFileAttributeViews());
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
