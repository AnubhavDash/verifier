package ch.post.it.evoting.verifier.block.block1;

import ch.post.it.evoting.verifier.common.block.TestSuite;

public class Block1TestSuite extends TestSuite {

    public static final String RESOURCE_BUNDLE_NAME = "block1/resources";

    public static final String PATH_CRYPTO_SETUP = "crypto_setup/";
    public static final String PATH_ELECTION_SETUP = "election_setup/";


    public Block1TestSuite() {
        super(Block1TestSuite.class.getPackage().getName() + ".tests");
    }

}
