package ch.post.it.evoting.verifier.common.block.tools;

import java.util.HashMap;

public class CountMap<T> extends HashMap<T, Long> {
    public void increment(T key) {
        this.putIfAbsent(key, 0L);
        this.compute(key, (k, oldValue) -> oldValue + 1);
    }
}
