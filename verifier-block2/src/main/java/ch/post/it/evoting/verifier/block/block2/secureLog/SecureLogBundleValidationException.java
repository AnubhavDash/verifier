package ch.post.it.evoting.verifier.block.block2.secureLog;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecureLogBundleValidationException extends Exception {
    private String host;

    public SecureLogBundleValidationException(String message, String host) {
        super(message);
        this.host = host;
    }
}
