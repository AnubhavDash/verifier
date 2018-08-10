package ch.post.it.evoting.verifier.block.block1;

import ch.post.it.evoting.verifier.common.block.TestSuite;

public class Block1TestSuite extends TestSuite {

    public static final String RESOURCE_BUNDLE_NAME = "block1/resources";

    public Block1TestSuite() {
        super(Block1TestSuite.class.getPackage().getName() + ".tests");
    }

}
