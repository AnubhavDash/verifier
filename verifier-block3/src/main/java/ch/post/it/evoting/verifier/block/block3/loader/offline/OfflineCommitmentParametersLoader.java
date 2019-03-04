/**
 * This file is part of Verifier Swiss Post.
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block3.loader.offline;

import ch.post.it.evoting.verifier.block.block3.scytl.loader.CommitmentParametersLoader;
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
