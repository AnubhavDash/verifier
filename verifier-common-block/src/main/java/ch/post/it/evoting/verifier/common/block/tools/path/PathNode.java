package ch.post.it.evoting.verifier.common.block.tools.path;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathNode {

    private List<Path> paths;
    private Path firstPath;
    private StructureNode structureNode;

    PathNode(List<Path> path, StructureNode structureNode) {
        this.paths = path;
        this.firstPath = path.get(0);
        this.structureNode = structureNode;
    }

    public Path getPath() {
        return firstPath;
    }

    public List<Path> getRegexPaths() {
        return paths;
    }

    public Path getRelation(RelationType relationType) {
        if (Files.isDirectory(firstPath)) {
            throw new IllegalArgumentException(String.format("PathNode is a directory: %s", firstPath.getFileName()));
        }
        for (RelationType r : structureNode.getRelations()) {
            if (r.equals(relationType)) {
                return firstPath.resolveSibling(firstPath.getFileName() + "." + r.toLowerCase());
            }
        }
        throw new IllegalArgumentException(String.format("Asked relation does not exist: %s", relationType));
    }

    /**
     * In the case where this PathNode represents several files (determined by regex), obtain the relation for the file at the specified
     * {@code index}.
     *
     * @param relationType The {@link RelationType} to get.
     * @param index        The index of the path to get the relation.
     * @return The relation for the path at specified index.
     */
    public Path getRelation(RelationType relationType, int index) {
        if (paths.stream().anyMatch(Files::isDirectory)) {
            throw new IllegalArgumentException("PathNode is a directory.");
        }
        for (RelationType r : structureNode.getRelations()) {
            if (r.equals(relationType)) {
                final String relationPathString = paths.get(index).getFileName().toString() + "." + relationType.toLowerCase();
                return paths.get(index).resolveSibling(relationPathString);
            }
        }
        throw new IllegalArgumentException(String.format("Asked relation does not exist: %s", relationType));
    }

    public List<Path> getSubDirectories() throws IOException {
        if (Files.isDirectory(firstPath)) {
            try (final Stream<Path> walk = Files.walk(firstPath, 1)) {
                return walk.filter(p -> Files.isDirectory(p) && !firstPath.equals(p)).collect(Collectors.toCollection(ArrayList::new));
            }
        } else {
            throw new IllegalArgumentException("The PathNode does not represent a directory.");
        }
    }

    public List<Path> getSubFiles() throws IOException {
        if (Files.isDirectory(firstPath)) {
            try (final Stream<Path> walk = Files.walk(firstPath, 1)) {
                return walk.filter(p -> Files.isRegularFile(p) && !firstPath.equals(p)).collect(Collectors.toCollection(ArrayList::new));
            }
        } else {
            throw new IllegalArgumentException("The PathNode does not represent a directory.");
        }
    }

    public String getQualifier() {
        return structureNode.getQualifier();
    }

}
