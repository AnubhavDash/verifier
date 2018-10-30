package ch.post.it.evoting.verifier.block.block3.loader;

import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;

import java.io.IOException;
import java.nio.file.Path;

public interface EncryptionParametersLoader {

    ZpGroup getZpGroup(Path path) throws IOException;
}