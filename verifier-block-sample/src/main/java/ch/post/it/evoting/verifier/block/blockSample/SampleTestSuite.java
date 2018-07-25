package ch.post.it.evoting.verifier.block.blockSample;

import ch.post.it.evoting.verifier.common.block.TestSuite;

public class SampleTestSuite extends TestSuite {

    public static final String RESOURCE_BUNDLE_NAME = "blockSample/resources";

    public SampleTestSuite() {
        super(SampleTestSuite.class.getPackage().getName() + ".tests");
    }

}
