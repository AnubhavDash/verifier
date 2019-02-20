package ch.post.it.evoting.verifier.block.block3.scytl.loader;

import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;

import java.io.IOException;

public interface PublicKeyLoader {

    ElGamalPublicKey getPublicKey() throws IOException;
}
