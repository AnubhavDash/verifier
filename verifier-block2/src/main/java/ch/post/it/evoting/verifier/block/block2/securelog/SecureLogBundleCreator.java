package ch.post.it.evoting.verifier.block.block2.securelog;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicReference;

public final class SecureLogBundleCreator {
    private static final Logger LOGGER = Logger.getLogger(SecureLogBundleCreator.class);

    private SecureLogBundleCreator() {
        //private ctor. Use static method
    }

    @Getter
    @Setter
    static class MyStruct {
        SecureLogBundle bundle;
        CheckPointLogEntry lastAnalysedCheckPoint;
        boolean terminal = false;
    }

    public static Flux<SecureLogBundle> from(Flux<SecureLogEntry> source) {
        AtomicReference<SecureLogEntry> last = new AtomicReference<>();
        return source
                .doOnNext(last::set)
                .concatMap(e -> {
                    //duplicate all checkpoints
                    if (e instanceof CheckPointLogEntry) {
                        return Flux.just(e, e);
                    } else {
                        return Flux.just(e);
                    }
                }).scan(new MyStruct(), (s, e) -> {
                    MyStruct result = new MyStruct();
                    if (e instanceof CheckPointLogEntry) {
                        if (e.equals(s.getLastAnalysedCheckPoint())) {
                            //this checkpoint is a duplicate one -> create new Bundle
                            result.setBundle(new SecureLogBundle());
                            result.getBundle().setBeginCheckPoint((CheckPointLogEntry) e);
                        } else if (s.getLastAnalysedCheckPoint() != null) {
                            //this checkpoint is not the first one of the Flux
                            result.setBundle(s.getBundle());
                            result.getBundle().setEndCheckPoint((CheckPointLogEntry) e);
                            result.setTerminal(true);
                        }
                        result.setLastAnalysedCheckPoint((CheckPointLogEntry) e);
                    } else if (e instanceof RegularLogEntry) {
                        result.setLastAnalysedCheckPoint(s.getLastAnalysedCheckPoint());
                        result.setBundle(s.getBundle());
                        result.getBundle().addRegularLogEntry((RegularLogEntry) e);
                    } else {
                        throw new IllegalArgumentException("Unsupported SecureLogEntry implementation : " + e.getClass());
                    }
                    return result;
                }).filter(MyStruct::isTerminal).map(MyStruct::getBundle).doOnComplete(() -> {
                    SecureLogEntry lastEntry = last.get();
                    if (lastEntry != null && !(lastEntry instanceof CheckPointLogEntry)) {
                        throw new RuntimeException("SecureLog on host {" + lastEntry.getHost() + "}, source {" + lastEntry.getSource() + "} does not terminate with a checkpoint");
                    }
                });
    }
}
