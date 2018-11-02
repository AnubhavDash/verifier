/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block3.loader;

import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;

import java.io.IOException;

/**
 * Class CommitmentParametersLoader.
 * This represents TODO.
 *
 * @author lalandret
 * @version $$Revision$$
 */
public interface CommitmentParametersLoader {
    CommitmentParams getCommitmentParams(final ZpGroup zpGroup, final int numberOfVoters) throws IOException;
}
