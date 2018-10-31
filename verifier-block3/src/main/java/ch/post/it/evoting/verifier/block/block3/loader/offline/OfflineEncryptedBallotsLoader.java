package ch.post.it.evoting.verifier.block.block3.loader.offline;

import ch.post.it.evoting.verifier.block.block3.loader.EncryptedBallotsLoader;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;

import java.io.IOException;
import java.nio.file.Path;

public class OfflineEncryptedBallotsLoader extends OfflineAbstractBallotLoader implements EncryptedBallotsLoader {

    public OfflineEncryptedBallotsLoader(Path path) throws IOException {
        super(path);
    }

    @Override
    public ElGamalEncryptedBallots getEncryptedBallots() throws IOException {
        Path fullPath = PathHelper.getFile(path.toFile(), "encryptedBallots.csv").toPath();
        return get(fullPath);
    }

}
