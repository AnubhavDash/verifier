package ch.post.it.evoting.verifier.block.block3.loader.offline;

import ch.post.it.evoting.verifier.block.block3.loader.ShuffleProofLoader;
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
