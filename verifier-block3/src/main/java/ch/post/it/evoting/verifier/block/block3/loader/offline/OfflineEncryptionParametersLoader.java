package ch.post.it.evoting.verifier.block.block3.loader.offline;


import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.block.block3.scytl.loader.EncryptionParametersLoader;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.EncryptionParameters;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;

public class OfflineEncryptionParametersLoader implements EncryptionParametersLoader {

    private final Path path;

    public OfflineEncryptionParametersLoader(Path path) {
        this.path = path;
    }

    @Override
    public ZpGroup getZpGroup() throws IOException {
        // path = inputDirectory.toPath().resolve(Block1TestSuite.PATH_CRYPTO_SETUP);
        EncryptionParameters ep = Deserializer.fromJson(path.resolve(Block3TestSuite.PATH_CRYPTO_SETUP).toFile(), "encryptionParameters\\.json", EncryptionParameters.class);
        BigInteger p = TypeConverter.stringToBigInteger(ep.getP());
        BigInteger q = TypeConverter.stringToBigInteger(ep.getQ());
        ZpGroupParams zpGroupParams = new ZpGroupParams(p, q);
        return new ZpGroup(zpGroupParams, new ZpElement(TypeConverter.stringToBigInteger(ep.getG()), zpGroupParams));
    }
}
