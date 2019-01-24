package ch.post.it.evoting.verifier.block.block3;

import ch.post.it.evoting.verifier.common.Status;
import com.scytl.products.ov.mixnet.BGVerifier;

import java.nio.file.Path;
import java.util.*;

public class BGOfflineVerificationProcessor {

    private static final BGOfflineVerificationProcessor instance = new BGOfflineVerificationProcessor();
    private List<Object> attachedClients = new LinkedList<>();
    private Path path;
    private boolean processed = false;

    private final Map<TestType, AbstractMap.SimpleEntry<Status, String>> statuses = new HashMap<>();

    private BGOfflineVerificationProcessor() {
        //singleton, use static getInstanceAndRegister method
    }

    public static BGOfflineVerificationProcessor getInstanceAndRegister(Object o) {
        instance.register(o);
        return instance;
    }

    public synchronized void register(Object o) {
        if (!attachedClients.contains(o)) {
            attachedClients.add(o);
        }
    }

    public synchronized void unregister(Object o) {
        attachedClients.remove(o);
        if (attachedClients.isEmpty()) {
            this.reset();
        }
    }

    private void reset() {
        this.processed = false;
        this.statuses.clear();
        this.path = null;
    }

    public  synchronized void executeProcess(Path path) {
        if (this.path == null) {
            this.path = path;
        } else if (!this.path.equals(path)) {
            throw new RuntimeException("Not unique Path defined");
        }

        if (!this.processed) {
            BGVerifier.verify(this.path, (TestType t, Status s, String m) -> {
                if (!statuses.containsKey(t) || !statuses.get(t).getKey().equals(Status.NOK)) {
                    statuses.put(t, new AbstractMap.SimpleEntry<>(s, s == Status.NOK ? m : null));
                }
            });
            this.processed = true;
        }
    }

    public AbstractMap.SimpleEntry<Status, String> getStatus(TestType type) {
        if (!processed) {
            throw new RuntimeException("you must call executeProcess before getting result");
        }
        return statuses.containsKey(type) ? statuses.get(type) : new AbstractMap.SimpleEntry<>(Status.NA, null);
    }

}
