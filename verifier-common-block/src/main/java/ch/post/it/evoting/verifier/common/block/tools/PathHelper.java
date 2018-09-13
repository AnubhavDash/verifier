package ch.post.it.evoting.verifier.common.block.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.security.InvalidParameterException;

public class PathHelper {
    private PathHelper() {
        //private constructor, use static
    }

    public static File[] listDirectories(Path path) {
        if (path != null && path.toFile().isDirectory()) {
            return path.toFile().listFiles(((subDir, name) -> subDir.isDirectory()));
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
