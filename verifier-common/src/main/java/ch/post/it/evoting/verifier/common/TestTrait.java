package ch.post.it.evoting.verifier.common;

/**
 * Used to group tests for execution
 */
public enum TestTrait {

    PreDecryption;

    public static TestTrait fromValue(String value) throws IllegalArgumentException  {
        return TestTrait.valueOf(value);
    }
}