package ch.post.it.evoting.verifier.block.block3;

import ch.post.it.evoting.verifier.block.block3.loader.online.OnlineMixingProofLoader;
import ch.post.it.evoting.verifier.block.block3.scytl.TestType;
import ch.post.it.evoting.verifier.block.block3.scytl.loader.OnlineDataLoader;
import ch.post.it.evoting.verifier.common.Status;
import com.scytl.products.ov.mixnet.BGVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class BGOnlineVerificationProcessor {

    private static final BGOnlineVerificationProcessor instance = new BGOnlineVerificationProcessor();
    private List<Object> attachedClients = new LinkedList<>();
    private Path path;
    private boolean processed = false;

    private final Map<TestType, AbstractMap.SimpleEntry<Status, String>> statuses = new HashMap<>();

    private BGOnlineVerificationProcessor() {
        //singleton, use static getInstanceAndRegister method
    }

    public static BGOnlineVerificationProcessor getInstanceAndRegister(Object o) {
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

    public synchronized void executeProcess(Path path) {
        if (this.path == null) {
            this.path = path;
        } else if (!this.path.equals(path)) {
            throw new RuntimeException("Not unique Path defined");
        }

        Function<Path, OnlineDataLoader> dataLoaderFunction = p -> {
            try {
                return new OnlineMixingProofLoader(p);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };


        if (!this.processed) {
            BGVerifier.verifyOnline(this.path, (TestType t, ch.post.it.evoting.verifier.block.block3.scytl.Status
                    s, String m) -> {
                if (!statuses.containsKey(t) || !statuses.get(t).getKey().equals(Status.NOK)) {
                    Status status = StatusConverter.map(s);
                    statuses.put(t, new AbstractMap.SimpleEntry<>(status, status == Status.NOK ? m : null));
                }
            }, dataLoaderFunction);
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
