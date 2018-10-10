package ch.post.it.evoting.verifier.block.block2.secureLog;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class SecureLogBundleCreator {
    private Stream<SecureLogBundle> stream = Stream.empty();
    private List<SecureLogBundle> buffer = new ArrayList<>();
    private SecureLogBundle lastBundle = null;
    private static final int BUFFER_SIZE = 1000;
    private boolean finishWithLastRowElement = false;

    private SecureLogBundleCreator() {
        //private ctor, use static from outside
    }

    private void addBundle(SecureLogBundle i) {
        buffer.add(i);
        lastBundle = i;

        if (buffer.size() > BUFFER_SIZE) {
            processBuffer();
        }
    }

    private void processBuffer() {
        stream = Stream.of(stream, buffer.stream()).flatMap(Function.identity());
        buffer = new ArrayList<>();
    }

    public static Stream<SecureLogBundle> from(Stream<SecureLogEntry> source) {
        SecureLogBundleCreator bundleAccumulator = source.reduce(new SecureLogBundleCreator(), (accumulator, secureLog) -> {
            SecureLogBundle last = accumulator.lastBundle;
            accumulator.finishWithLastRowElement = false;
            if (secureLog instanceof CheckPointLogEntry) {
                CheckPointLogEntry checkPointLog = (CheckPointLogEntry) secureLog;
                if (last == null) {
                    SecureLogBundle newSecureLogBundle = new SecureLogBundle();
                    newSecureLogBundle.setBeginCheckPoint(checkPointLog);
                    accumulator.addBundle(newSecureLogBundle);
                } else {
                    last.setEndCheckPoint(checkPointLog);
                    SecureLogBundle newSecureLogBundle = new SecureLogBundle();
                    newSecureLogBundle.setBeginCheckPoint(checkPointLog);
                    accumulator.addBundle(newSecureLogBundle);
                }
            } else if (secureLog instanceof RegularLogEntry) {
                accumulator.lastBundle.addRegularLogEntry((RegularLogEntry) secureLog);
            } else if (secureLog instanceof LastRowLogEntry) {
                accumulator.finishWithLastRowElement = true;
            } else {
                throw new UnsupportedOperationException("Unknown SecureLogEntry instance : " + secureLog.getClass());
            }
            return accumulator;
        }, (bc1, bc2) -> bc1); //no need to combine, because accumulator is treated as a single instance through all reduce process

        if (!bundleAccumulator.finishWithLastRowElement) {
            throw new RuntimeException("SecureLogs doesn't terminate with a LastRowLogEntry");
        }
        //TODO remove test above bacuase not used anymore
        if (bundleAccumulator.lastBundle != null && bundleAccumulator.lastBundle.hasRegularLogEntries()) {
            throw new RuntimeException("SecureLogs doesn't terminate with a checkpoint");
        }

        //do not return the last bundle because it will not be complete
        bundleAccumulator.processBuffer();
        return bundleAccumulator.stream.filter(SecureLogBundle::isComplete);
    }
}
