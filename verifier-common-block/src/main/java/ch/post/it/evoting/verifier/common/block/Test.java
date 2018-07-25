package ch.post.it.evoting.verifier.common.block;

import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;

import java.io.File;

public abstract class Test {

    public Test() {
        //force having a non-arg constructor
    }

    public abstract TestDefinition getTestDefinition();

    public abstract TestResult executeTest(File inputDirectory);
}
