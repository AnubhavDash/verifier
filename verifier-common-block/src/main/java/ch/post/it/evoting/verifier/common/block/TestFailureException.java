package ch.post.it.evoting.verifier.common.block;

public class TestFailureException extends RuntimeException {
    private String[] args;

    public TestFailureException(String... args) {
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }
}
