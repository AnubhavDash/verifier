package ch.post.it.evoting.verifier.common.block.tools.path;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;

@Getter
@AllArgsConstructor
class PathEntry {
    private PathType type;
    private Path parentPath;
    private String qualifier;
    private boolean dynamicAncestor;
    private List<RelationType> relations;
}
