package ch.post.it.evoting.verifier.util;

import ch.post.it.evoting.verifier.common.TestDefinition;

public class TestDefinitionTools {

    private TestDefinitionTools(){
        //private constructor, static use
    }

    public static String computeUniqueKey(TestDefinition testDefinition) {
        return String.format("%02d-%02d", testDefinition.getBlockId(), testDefinition.getId());
    }
}
