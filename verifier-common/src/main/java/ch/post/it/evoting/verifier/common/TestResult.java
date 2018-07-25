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

    public TestResultSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(TestResultSeverity severity) {
        this.severity = severity;
    }
}
