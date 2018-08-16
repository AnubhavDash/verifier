package ch.post.it.evoting.verifier.common.block.tools;

import java.io.File;
import java.nio.file.Path;

public class PathHelper {
    private PathHelper() {
        //private constructor, use static
    }

    public static File[] listDirectories(Path path) {
        if (path != null && path.toFile().isDirectory()) {
            return path.toFile().listFiles(((subDir, name) -> subDir.isDirectory()));
        } else {
            throw new IllegalArgumentException("Given path is not a directory :"+path);
        }
    }
}
