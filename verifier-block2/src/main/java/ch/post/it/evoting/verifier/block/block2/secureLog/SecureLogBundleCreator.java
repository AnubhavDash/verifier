package ch.post.it.evoting.verifier.block.block2.secureLog;

import io.reactivex.Observable;
import io.reactivex.Single;

import java.util.ArrayList;
import java.util.List;

public class SecureLogBundleCreator {
    private Observable<SecureLogBundle> observable = Observable.empty();
    private List<SecureLogBundle> buffer = new ArrayList<>();
    private SecureLogBundle lastBundle = null;
    private static final int BUFFER_SIZE = 1000;

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
        observable = Observable.concat(observable, Observable.fromIterable(buffer::iterator));
        buffer = new ArrayList<>();
    }

    public static Observable<SecureLogBundle> from(Observable<SecureLogEntry> source) {
        Single<SecureLogBundleCreator> bundleAccumulator = source.reduce(new SecureLogBundleCreator(), (accumulator, secureLog) -> {
            SecureLogBundle last = accumulator.lastBundle;
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
                // nothing to do
            } else {
                throw new UnsupportedOperationException("Unknown SecureLogEntry instance : " + secureLog.getClass());
            }
            return accumulator;
        });

        return bundleAccumulator.flatMapObservable(b -> {
            /*if (!b.finishWithLastRowElement) {
                throw new RuntimeException("SecureLogs doesn't terminate with a LastRowLogEntry");
            }
            //TODO remove test below because not used anymore
            if (b.lastBundle != null && b.lastBundle.hasRegularLogEntries()) {
                System.out.println("ERROR");
                //throw new RuntimeException("SecureLogs doesn't terminate with a checkpoint");
            }*/
            b.processBuffer();
            return b.observable;
        });

    }
}
