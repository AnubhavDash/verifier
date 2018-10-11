package ch.post.it.evoting.verifier.block.block2.secureLog;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class SecureLogBundle {
    private static final Logger LOGGER = Logger.getLogger(SecureLogBundle.class);

    private CheckPointLogEntry beginCheckPoint;
    private CheckPointLogEntry endCheckPoint;
    private List<RegularLogEntry> regularLogEntries = new ArrayList<>();

    public void setBeginCheckPoint(CheckPointLogEntry beginCheckPoint) {
        this.beginCheckPoint = beginCheckPoint;
    }

    public void setEndCheckPoint(CheckPointLogEntry endCheckPoint) {
        this.endCheckPoint = endCheckPoint;
    }

    public void addRegularLogEntry(RegularLogEntry regularLogEntry) {
        regularLogEntries.add(regularLogEntry);
    }

    public boolean hasRegularLogEntries() {
        return !regularLogEntries.isEmpty();
    }

    public boolean isComplete() {
        return this.endCheckPoint != null;
    }

    public void validate() throws SecureLogBundleValidationException {
        if (!this.isComplete()) {
            throw new SecureLogBundleValidationException("bundle is not complete");
        }
        // TODO uncomment following logger. Was commented due to performance reasons during development (debug level is not enabled for productive use)
        // LOGGER.debug(String.format("Bundle{prev:%s, curr:%s, elementsCount:%s}", this.beginCheckPoint, this.endCheckPoint, this.regularLogEntries.size()));
        //TODO implement this
    }
}
