package ch.post.it.evoting.verifier.block.block3;

import ch.post.it.evoting.verifier.common.Status;

@FunctionalInterface
public interface BGResultNotifier {
    void notify(TestType t, Status s, String errorMessage);
}
