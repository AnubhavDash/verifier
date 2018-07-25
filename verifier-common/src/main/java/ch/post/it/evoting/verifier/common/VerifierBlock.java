package ch.post.it.evoting.verifier.common;

import java.io.File;
import java.util.stream.Stream;

public interface VerifierBlock {
    Stream<TestDefinition> getTests();

    Stream<TestResult> process(File inputDirectory);
}
