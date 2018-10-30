package ch.post.it.evoting.verifier.block.block3.loader.offline;

import ch.post.it.evoting.verifier.block.block3.loader.PublicKeyLoader;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.PublicKey;
import ch.post.it.evoting.verifier.dto.PublicKey__1;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class OfflinePublicKeyLoader implements PublicKeyLoader {
    private final Path path;

    public OfflinePublicKeyLoader(Path path) {
        this.path = path;
    }

    @Override
    public ElGamalPublicKey getPublicKey() throws IOException {
        // path = ballotboxes / id / 0
        PublicKey json = Deserializer.fromJson(path.toFile(), "publicKey\\.json", PublicKey.class);
        PublicKey__1 publicKey = json.getPublicKey();

        ZpGroupParams params = new ZpGroupParams(TypeConverter.base64ToBigInteger(publicKey.getZpSubgroup().getP()), TypeConverter.base64ToBigInteger(publicKey.getZpSubgroup().getQ()));

        ZpGroup zpGroup = new ZpGroup(params, new ZpElement(publicKey.getZpSubgroup().getG(), params));

        List<GroupElement> pubKeys = publicKey.getElements().stream().map(s -> new ZpElement(s, params)).collect(Collectors.toList());

        return new ElGamalPublicKey(pubKeys, zpGroup);
    }
}