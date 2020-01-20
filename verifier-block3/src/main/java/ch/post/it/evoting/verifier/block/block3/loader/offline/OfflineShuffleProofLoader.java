/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block3.loader.offline;

import ch.post.it.evoting.verifier.block.block3.scytl.loader.ShuffleProofLoader;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProof;
import com.scytl.products.ov.mixnet.commons.io.JSONProofsReader;

import java.io.IOException;
import java.nio.file.Path;

public class OfflineShuffleProofLoader implements ShuffleProofLoader {

    private final Path path;
    private final JSONProofsReader jsonProofsReader;

    public OfflineShuffleProofLoader(Path path) {
        this.path = path;
        this.jsonProofsReader = new JSONProofsReader();
    }

    @Override
    public ShuffleProof getShuffleProof() throws IOException {
        return jsonProofsReader.read(PathHelper.getFile(path.toFile(), "proofs\\.json").toPath());
    }
}
