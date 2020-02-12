package ch.post.it.evoting.verifier.common.block.tools.path;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;

@Getter
public class StructureNode {
    private PathType type;
    private Path parentPath;
    private String qualifier;
    private boolean dynamicAncestor;
    private List<RelationType> relations;

    StructureNode(PathType type, Path parentPath, String qualifier, boolean dynamicAncestor, List<RelationType> relations) {
        this.type = type;
        this.parentPath = parentPath;
        this.qualifier = qualifier;
        this.dynamicAncestor = dynamicAncestor;
        this.relations = relations;
    }
}
