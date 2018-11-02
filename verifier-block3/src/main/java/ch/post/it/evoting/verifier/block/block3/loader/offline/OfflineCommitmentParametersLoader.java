/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block3.loader.offline;

import ch.post.it.evoting.verifier.block.block3.loader.CommitmentParametersLoader;
import com.scytl.products.ov.mixnet.commons.io.BGReader;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Class OfflineCommitmentParametersLoader.
 * This represents TODO.
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class OfflineCommitmentParametersLoader implements CommitmentParametersLoader {

    private final Path path;

    public OfflineCommitmentParametersLoader(Path path) {
        this.path = path;
    }

    @Override
    public CommitmentParams getCommitmentParams(final ZpGroup zpGroup, final int numberOfVoters) throws IOException {
        return BGReader.createCommitmentParams(zpGroup, numberOfVoters, path);
    }
}
