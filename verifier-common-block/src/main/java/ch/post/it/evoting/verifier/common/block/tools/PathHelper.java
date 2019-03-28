/**
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.common.block.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PathHelper {
    private PathHelper() {
        //private constructor, use static
    }

    public static File[] listDirectories(Path path) {
        if (path != null && path.toFile().isDirectory()) {
            return path.toFile().listFiles(File::isDirectory);
        } else {
            throw new IllegalArgumentException("Given path is not a directory :" + path);
        }
    }

    public static File[] getFiles(File inputDirectory, String filenamePattern) throws FileNotFoundException {
        File[] file = getFilesInternal(inputDirectory, filenamePattern, false).toArray(new File[]{});
        if (file.length == 0) {
            throw new FileNotFoundException(filenamePattern);
        } else {
            return file;
        }
    }

    public static List<File> getFiles(File inputDirectory, String filenamePattern, boolean recursive) throws FileNotFoundException {
        List<File> result = getFilesInternal(inputDirectory, filenamePattern, recursive);
        if (result.size() == 0) {
            throw new FileNotFoundException(filenamePattern);
        }
        return result;
    }

    private static List<File> getFilesInternal(File inputDirectory, String filenamePattern, boolean recursive) {
        List<File> result = new ArrayList<>();

        File[] files = inputDirectory.listFiles((dir, name) -> name.matches(filenamePattern));
        if (files != null) {
            result.addAll(Arrays.stream(files).filter(File::isFile).collect(Collectors.toList()));
        }

        if (recursive) {
            for (File directory : listDirectories(inputDirectory.toPath())) {
                result.addAll(getFilesInternal(directory, filenamePattern, true));
            }
        }
        return result;
    }

    public static File getFile(File inputDirectory, String filenamePattern) throws FileNotFoundException {
        File[] file = getFiles(inputDirectory, filenamePattern);
        if (file.length > 1) {
            throw new InvalidParameterException(String.format("more than one file found, filename is not specific enough. Dir:%s filenamePattern:%s ", inputDirectory, filenamePattern));
        } else {
            return file[0];
        }
    }
}
