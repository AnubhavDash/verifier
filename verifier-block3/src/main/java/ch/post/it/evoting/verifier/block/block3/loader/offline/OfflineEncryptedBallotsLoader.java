/**
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

import ch.post.it.evoting.verifier.block.block3.scytl.loader.EncryptedBallotsLoader;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;

import java.io.IOException;
import java.nio.file.Path;

public class OfflineEncryptedBallotsLoader extends AbstractOfflineBallotLoader implements EncryptedBallotsLoader {

    public OfflineEncryptedBallotsLoader(Path path, Path rootPath) throws IOException {
        super(path, rootPath);
    }

    @Override
    public ElGamalEncryptedBallots getEncryptedBallots() throws IOException {
        Path fullPath = PathHelper.getFile(path.toFile(), "encryptedBallots.csv").toPath();
        return get(fullPath);
    }

}
