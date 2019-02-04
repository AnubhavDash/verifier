package ch.post.it.evoting.verifier.block.block2;

import ch.post.it.evoting.verifier.common.block.TestSuite;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Block2TestSuite extends TestSuite {

    public static final String RESOURCE_BUNDLE_NAME = "block2/resources";

    public static final Path PATH_BALLOTBOXES = Paths.get("ballotboxes/");
    public static final Path PATH_ELECTION_SETUP = Paths.get("election_setup/");
    public static final Path PATH_CERTIFICATES = Paths.get("certificates/");
    public static final Path PATH_CC_LOG_SIGN_CERTIFICATES = Paths.get("certificates/log_sign_keys");
    public static final Path PATH_CC_CA_CERTIFICATES = Paths.get("certificates/cc_ca_keys");
    public static final Path PATH_VOTING_CARD_SETS = Paths.get("voting_card_sets/");
    public static final Path PATH_SECURE_LOGS = Paths.get("secureLogs/");

    public Block2TestSuite() {
        super(Block2TestSuite.class.getPackage().getName() + ".tests");
    }

}
