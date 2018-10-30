package ch.post.it.evoting.verifier.block.block3.loader.offline;


import ch.post.it.evoting.verifier.block.block3.loader.EncryptionParametersLoader;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.EncryptionParameters;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;

public class OfflineEncryptionParametersLoader implements EncryptionParametersLoader {

    @Override
    public ZpGroup getZpGroup(Path path) throws IOException {
        // path = inputDirectory.toPath().resolve(Block1TestSuite.PATH_CRYPTO_SETUP);
        EncryptionParameters ep = Deserializer.fromJson(path.toFile(), "encryptionParameters\\.json", EncryptionParameters.class);
        BigInteger p = TypeConverter.stringToBigInteger(ep.getP());
        BigInteger q = TypeConverter.stringToBigInteger(ep.getQ());
        ZpGroupParams zpGroupParams = new ZpGroupParams(p, q);
        ZpGroup zpGroup = new ZpGroup(zpGroupParams);
        return  zpGroup;
    }
}
