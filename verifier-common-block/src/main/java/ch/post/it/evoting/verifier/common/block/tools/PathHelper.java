/**
 * This file is part of Verifier Swiss Post.
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.common.block.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        File[] file = inputDirectory.listFiles((dir, name) -> name.matches(filenamePattern));
        if (file == null || file.length == 0) {
            throw new FileNotFoundException(filenamePattern);
        } else {
            return file;
        }
    }

    public static List<File> getFiles(File inputDirectory, String filenamePattern, Boolean recursive) throws FileNotFoundException {
        List<File> files = new ArrayList<>();
        if (recursive) {
            File[] directories = listDirectories(inputDirectory.toPath());
            for (File directory : directories) {
                File[] file = getFiles(directory, filenamePattern);
                if (file == null || file.length == 0) {
                    throw new FileNotFoundException(filenamePattern);
                } else {
                    files.addAll(Arrays.asList(file));
                }
            }
        } else {
            files.addAll(Arrays.asList(getFiles(inputDirectory, filenamePattern)));
        }
        return files;
    }

    public static File getFile(File inputDirectory, String filenamePattern) throws FileNotFoundException {
        File[] file = inputDirectory.listFiles((dir, name) -> name.matches(filenamePattern));
        if (file == null || file.length == 0) {
            throw new FileNotFoundException(filenamePattern);
        } else if (file.length > 1) {
            throw new InvalidParameterException(String.format("more than one file found, filename is not specific enough. Dir:%s filenamePattern:%s ", inputDirectory, filenamePattern));
        } else {
            return file[0];
        }
    }
}
