package ch.post.it.evoting.verifier.block.block1;

import ch.post.it.evoting.verifier.common.block.TestSuite;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Block1TestSuite extends TestSuite {

    public static final String RESOURCE_BUNDLE_NAME = "block1/resources";

    public static final Path PATH_CRYPTO_SETUP = Paths.get("crypto_setup/");
    public static final Path PATH_CERTIFICATES = Paths.get("certificates/");
    public static final Path PATH_ADMINBOARD = Paths.get("adminboard/");
    public static final Path PATH_ELECTION_SETUP = Paths.get("election_setup/");
    public static final Path PATH_VOTING_CARD_SETS = Paths.get("voting_card_sets");


    public Block1TestSuite() {
        super(Block1TestSuite.class.getPackage().getName() + ".tests");
    }

}
