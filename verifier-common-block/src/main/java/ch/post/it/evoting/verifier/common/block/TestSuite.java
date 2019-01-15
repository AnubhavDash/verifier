package ch.post.it.evoting.verifier.common.block;

import ch.post.it.evoting.verifier.common.*;
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
    public Stream<TestResult> process(File inputDirectory, List<TestTrait> options) {
        return tests.stream().map(t -> {
            TestDefinition def = t.getTestDefinition();
            // Do skip the test ift there are any defined restrictions
            // and the test trait does not match the restriction
            if ( ( options != null && !options.isEmpty())
                    && !def.containsAnyTestTrait(options)) {
                TestResult result = new TestResult(def);
                result.setStatus(Status.NA);
                return result;
            }
            return t.executeTest(inputDirectory);
        });
    }
}
