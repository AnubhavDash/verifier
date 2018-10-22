package ch.post.it.evoting.verifier.processor;

import ch.post.it.evoting.verifier.dto.Test;

public interface ProcessListener {
    void testProcessed(Test test);
}
