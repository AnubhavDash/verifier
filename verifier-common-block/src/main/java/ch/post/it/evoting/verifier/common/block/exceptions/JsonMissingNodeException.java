package ch.post.it.evoting.verifier.common.block.exceptions;

/**
 * Exception signaling a mandatory property is missing in a json file.
 */
public class JsonMissingNodeException extends RuntimeException {
    public JsonMissingNodeException(String message) {
        super(message);
    }
}
