package ch.post.it.evoting.verifier.block.block3.loader.offline;

import ch.post.it.evoting.verifier.block.block3.loader.ReEncryptedBallotsLoader;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;

import java.io.IOException;
import java.nio.file.Path;

public class OfflineReEncryptedBallotsLoader extends AbstractOfflineBallotLoader implements ReEncryptedBallotsLoader {

    public OfflineReEncryptedBallotsLoader(Path path) throws IOException {
        super(path);
    }

    @Override
    public ElGamalEncryptedBallots getReEncryptedBallots() throws IOException {
        Path fullPath = PathHelper.getFile(this.path.toFile(), "reencryptedBallots.csv").toPath();
        return get(fullPath);
    }
}
