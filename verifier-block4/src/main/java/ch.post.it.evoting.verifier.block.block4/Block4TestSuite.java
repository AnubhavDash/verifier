package ch.post.it.evoting.verifier.block.block4;

import ch.post.it.evoting.verifier.common.block.TestSuite;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Block4TestSuite extends TestSuite {

    public static final String RESOURCE_BUNDLE_NAME = "block4/resources";

    public static final Path PATH_ELECTION_SETUP = Paths.get("election_setup/");
    public static final Path PATH_BALLOTBOXES = Paths.get("ballotboxes/");
    public static final Path PATH_RESULTS = Paths.get("results/");

    public Block4TestSuite() {
        super(Block4TestSuite.class.getPackage().getName() + ".tests");
    }

}
