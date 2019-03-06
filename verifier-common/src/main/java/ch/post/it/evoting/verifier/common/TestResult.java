/**
 * This file is part of Verifier Swiss Post.
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.common;

import java.util.Map;

public class TestResult {
    private TestDefinition testDefinition;
    private Status status;
    private Map<Language, String> message;
    private TestResultSeverity severity;

    public TestResult(TestDefinition definition) {
        this.testDefinition = definition;
    }

    public TestDefinition getTestDefinition() {
        return testDefinition;
    }

    public void setTestDefinition(TestDefinition testDefinition) {
        this.testDefinition = testDefinition;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Map<Language, String> getMessage() {
        return message;
    }

    public void setMessage(Map<Language, String> message) {
        this.message = message;
    }

}
