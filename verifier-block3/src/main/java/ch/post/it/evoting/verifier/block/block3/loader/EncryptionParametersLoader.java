package ch.post.it.evoting.verifier.block.block3.loader;

import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;

import java.io.IOException;

public interface EncryptionParametersLoader {

    ZpGroup getZpGroup() throws IOException;
}