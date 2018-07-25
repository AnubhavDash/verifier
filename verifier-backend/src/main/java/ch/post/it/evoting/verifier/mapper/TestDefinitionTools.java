package ch.post.it.evoting.verifier.mapper;

import ch.post.it.evoting.verifier.common.TestDefinition;

public class TestDefinitionTools {
    public static String computeUniqueKey(TestDefinition testDefinition) {
        return String.format("%02d-%02d", testDefinition.getBlockId(), testDefinition.getId());
    }
}
