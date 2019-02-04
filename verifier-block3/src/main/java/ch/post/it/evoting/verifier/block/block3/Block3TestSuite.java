package ch.post.it.evoting.verifier.block.block3;

import ch.post.it.evoting.verifier.common.block.TestSuite;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Block3TestSuite extends TestSuite {

    public static final String RESOURCE_BUNDLE_NAME = "block3/resources";
    public static final Path PATH_ELECTION_SETUP = Paths.get("election_setup/");
    public static final Path PATH_BALLOTBOXES = Paths.get("ballotboxes/");
    public static final Path PATH_CERTIFICATES = Paths.get("certificates/");
    public static final Path PATH_CC_MIXING_KEYS = Paths.get("certificates/cc_mixing_keys/");
    public static final Path PATH_ADMINBOARD = Paths.get("adminboard/");
    public static final Path PATH_CRYPTO_SETUP = Paths.get("crypto_setup/");

    public Block3TestSuite() {
        super(Block3TestSuite.class.getPackage().getName() + ".tests");
    }

}
