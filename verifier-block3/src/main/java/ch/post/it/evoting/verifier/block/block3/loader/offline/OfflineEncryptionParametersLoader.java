package ch.post.it.evoting.verifier.block.block3.loader.offline;


import ch.post.it.evoting.verifier.block.block3.loader.EncryptionParametersLoader;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.EncryptionParameters;
import ch.post.it.evoting.verifier.dto.EncryptionParametersZpSubGroup;
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
        EncryptionParametersZpSubGroup ep = Deserializer.fromJson(path.toFile(), "encryptionParameters\\.json", EncryptionParametersZpSubGroup.class);
        BigInteger p = TypeConverter.base64ToBigInteger(ep.getZpSubgroup().getP());
        BigInteger q = TypeConverter.base64ToBigInteger(ep.getZpSubgroup().getQ());
        ZpGroupParams zpGroupParams = new ZpGroupParams(p, q);
        return new ZpGroup(zpGroupParams, new ZpElement(TypeConverter.base64ToBigInteger(ep.getZpSubgroup().getG()), zpGroupParams));
    }
}
