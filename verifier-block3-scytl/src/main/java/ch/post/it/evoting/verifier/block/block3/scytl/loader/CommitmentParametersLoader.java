package ch.post.it.evoting.verifier.block.block3.scytl.loader;

import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;

import java.io.IOException;

public interface CommitmentParametersLoader {
    CommitmentParams getCommitmentParams(final ZpGroup zpGroup, final int numberOfVoters) throws IOException;
}
