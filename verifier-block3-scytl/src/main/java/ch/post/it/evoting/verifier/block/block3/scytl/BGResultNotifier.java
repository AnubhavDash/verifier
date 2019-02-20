package ch.post.it.evoting.verifier.block.block3.scytl;

@FunctionalInterface
public interface BGResultNotifier {
    void notify(TestType t, Status s, String errorMessage);
}
