package ch.post.it.evoting.verifier.block.block2.secureLog;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecureLogBundleValidationException extends Exception {
    private String host;
    private String source;

    public SecureLogBundleValidationException(String message, String host, String source) {
        super(message);
        this.host = host;
        this.source = source;
    }
}
