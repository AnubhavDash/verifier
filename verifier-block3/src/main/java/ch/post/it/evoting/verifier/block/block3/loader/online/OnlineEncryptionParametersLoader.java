package ch.post.it.evoting.verifier.block.block3.loader.online;

import ch.post.it.evoting.verifier.block.block3.loader.EncryptionParametersLoader;
import ch.post.it.evoting.verifier.dto.OnlineMixing;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class OnlineEncryptionParametersLoader extends OnlineGenericLoader implements EncryptionParametersLoader {

    OnlineMixing onlineMixing;

    public OnlineEncryptionParametersLoader(Path path) throws IOException {
        onlineMixing = load(path);
    }

    @Override
    public ZpGroup getZpGroup() throws IOException {
        //conversion onlineMixing -> ZpGroup (mapStruct!!!!)
        return null;
    }
}
