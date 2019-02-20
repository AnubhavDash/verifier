package ch.post.it.evoting.verifier.block.block3;

import ch.post.it.evoting.verifier.common.Status;

public final class StatusConverter {
    public static Status map(ch.post.it.evoting.verifier.block.block3.scytl.Status scytlStatus) {
        return Status.valueOf(scytlStatus.toString());
    }}
