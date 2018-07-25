package ch.post.it.evoting.verifier.common.block;

import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.VerifierBlock;
import org.apache.log4j.Logger;
import org.reflections.Reflections;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TestSuite implements VerifierBlock {
    private static Logger log = Logger.getLogger(TestSuite.class);
    private List<Test> tests;

    protected TestSuite(String packagePrefix) {
        Reflections reflections = new Reflections(packagePrefix);
        tests = reflections.getSubTypesOf(Test.class).parallelStream().map(c -> {
            try {
                return (Test) c.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException("Unable to instantiate the tests", e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Stream<TestDefinition> getTests() {
        return tests.stream().map(Test::getTestDefinition);
    }

    @Override
    public Stream<TestResult> process(File inputDirectory) {
        return tests.stream().map(t -> t.executeTest(inputDirectory));
    }
}
