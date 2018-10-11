package ch.post.it.evoting.verifier.block.block2.secureLog;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SecureLogEntry {

    private String host;
    private String index;

    protected abstract void deserialize(String line);

    public static SecureLogEntry from(String line) {
        SecureLogEntry result;
        if (line.contains("New Secret Key generated")) {
            result = new CheckPointLogEntry();
        } else if (line.contains("lastrow")) {
            result = new LastRowLogEntry();
        } else {
            result = new RegularLogEntry();
        }
        result.deserialize(line);
        return result;
    }
}
