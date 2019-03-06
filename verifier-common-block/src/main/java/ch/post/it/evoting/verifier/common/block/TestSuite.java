/**
 * This file is part of Verifier Swiss Post.
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.common.block;

import ch.post.it.evoting.verifier.common.*;
import org.apache.log4j.Logger;
import org.reflections.Reflections;

import java.io.File;
import java.util.List;
import java.util.Set;
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
    public Stream<TestResult> process(File inputDirectory, Set<TestTrait> options) {
        return tests.stream().map(t -> {
            TestDefinition def = t.getTestDefinition();
            // Do skip the test if there are any defined restrictions
            // and the test trait does not match the restriction
            if ( ( options != null && !options.isEmpty())
                    && !def.containsAnyTestTrait(options)) {
                TestResult result = new TestResult(def);
                result.setStatus(Status.NA);
                return result;
            } else {
                return t.executeTest(inputDirectory);
            }
        });
    }
}
