package ch.post.it.evoting.verifier.common.block.tools.path;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PathService {

    private Map<PathTreeKey, PathEntry> pathMap = new HashMap<>();

    public PathService() {
        try {
            // TODO Allow to override file as main argument.
            final Path path = Paths.get(getClass().getResource("/dataset_structure.json").toURI());

            ObjectMapper mapper = new ObjectMapper();
            final JsonNode rootNode = mapper.readTree(Files.readAllBytes(path));

            // TODO check business structure + keys
            StructureChecker.process(rootNode);

            addMapEntry(rootNode, Paths.get(""), false);

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Impossible to find/read structure file.");
        }
    }

    /**
     * Obtain file or directory path from the specified {@link PathTreeKey}.
     *
     * @param pathTreeKey        The file or directory to obtain.
     * @param inputDirectoryPath The root path where the dataset lies.
     * @return The path for the {@code datasetKey}.
     * @throws IOException If the path can not be obtained.
     */
    public PathNode buildPathNode(PathTreeKey pathTreeKey, Path inputDirectoryPath) throws IOException {
        final PathEntry pathEntry = pathMap.get(pathTreeKey);

        // If the file/folder has a dynamic part in its path, the dynamic part has to be specified.
        if (pathEntry.isDynamicAncestor()) {
            throw new RuntimeException(String.format("The file/directory %s is contained in a folder with a dynamic name. Please use " +
                            "PathService.buildDynamicPathNode(FileTreeKey fileTreeKey, Path dynamicPath, Path inputDirectoryPath).",
                    pathEntry.getQualifier()));
        }

        // Combine input path with file/directory parent path.
        final Path combined = inputDirectoryPath.resolve(pathEntry.getParentPath());

        return new PathNode(resolve(combined, pathEntry), pathEntry);
    }

    /**
     * Obtain file or directory path from the specified {@link PathTreeKey}.
     *
     * @param pathTreeKey        The file or directory to obtain.
     * @param dynamicPath        The dynamic part of the path.
     * @return The path for the {@code datasetKey}.
     * @throws IOException If the path can not be obtained.
     */
    public PathNode buildFromDynamicPathNode(PathTreeKey pathTreeKey, Path dynamicPath) throws IOException {
        final PathEntry pathEntry = pathMap.get(pathTreeKey);

        // Check if asked key is really dynamic.
        if (!pathEntry.isDynamicAncestor()) {
            throw new RuntimeException(String.format("The file/directory %s is not contained in a folder with a dynamic name. Please use " +
                    "PathService.buildPathNode(FileTreeKey fileTreeKey, Path inputDirectoryPath).", pathEntry.getQualifier()));
        }

        // dynamicPath is already absolute
        return new PathNode(resolve(dynamicPath, pathEntry), pathEntry);
    }

    private List<Path> resolve(Path startingPath, PathEntry pathEntry) throws IOException {
        List<Path> paths = Files.find(startingPath, 1,
                (path, attributes) -> path.getFileName().toString().matches(pathEntry.getQualifier()))
                .filter(path -> PathType.FILE.equals(pathEntry.getType()) ? Files.isRegularFile(path) : Files.isDirectory(path))
                .collect(Collectors.toList());
        if (paths.size() == 0) {
            throw new NoSuchFileException(String.format("No file or directory found with given name/pattern. Starting path: %s " +
                    "namePattern:%s ", startingPath, pathEntry.getQualifier()));
        } else {
            return paths;
        }
    }

    /**
     * All the checks for missing nodes, wrong structure, etc... is already done when calling this method.
     */
    private void addMapEntry(JsonNode currentNode, Path parentPath, boolean dynamicAncestor) {
        for (JsonNode node : currentNode) {
            // Check whether the name is regular or a regex.
            String currentName = node.path("name").isMissingNode() ? node.path("regex").asText() : node.path("name").asText();

            // Register current node as long as it is not a dynamic name folder.
            final PathType type = PathType.valueOf(node.path("type").asText());
            if (PathType.FILE.equals(type)) {
                // Check if there are defined relations and add them.
                final JsonNode relationsNode = node.path("relations");
                List<RelationType> relations = getRelations(relationsNode);
                // Add PathNode to the map
                pathMap.put(PathTreeKey.valueOf(node.path("key").asText()), new PathEntry(type, parentPath, currentName, dynamicAncestor, relations));
            } else {
                pathMap.put(PathTreeKey.valueOf(node.path("key").asText()), new PathEntry(type, parentPath, currentName, dynamicAncestor, null));
                // Update dynamic value for child
                if (PathType.DYNAMIC_DIRECTORY.equals(type)) {
                    dynamicAncestor = true;
                }
            }

            // If the current node is a folder or dynamic folder, recursively continue.
            if (!PathType.FILE.equals(type)) {
                addMapEntry(node.path("content"), parentPath.resolve(currentName), dynamicAncestor);
            }
        }
    }

    private List<RelationType> getRelations(JsonNode relationsNode) {
        List<RelationType> relations = new ArrayList<>();
        if (!relationsNode.isMissingNode()) {
            for (JsonNode relationNode : relationsNode) {
                relations.add(RelationType.valueOf(relationNode.path("type").asText()));
            }
        }
        return relations;
    }

}
