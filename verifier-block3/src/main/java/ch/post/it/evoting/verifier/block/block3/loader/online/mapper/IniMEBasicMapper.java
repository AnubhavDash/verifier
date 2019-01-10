package ch.post.it.evoting.verifier.block.block3.loader.online.mapper;

import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.onlinemixing.CiphertextsE;
import ch.post.it.evoting.verifier.dto.onlinemixing.IniMEBasic;
import com.scytl.products.ov.mixnet.commons.beans.proofs.MultiExponentiationBasicProofInitialMessage;
import com.scytl.products.ov.mixnet.commons.beans.proofs.MultiExponentiationReductionInitialMessage;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalCiphertext;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface IniMEBasicMapper {

    IniMEBasicMapper INSTANCE = Mappers.getMapper(IniMEBasicMapper.class);

    default MultiExponentiationBasicProofInitialMessage map(IniMEBasic source) {
        PublicCommitment cA0 = PublicCommitmentMapper.INSTANCE.map(source.getCommitmentPublicA0());
        List<PublicCommitment> cB = source.getCommitmentPublicB().stream().map(e -> PublicCommitmentMapper.INSTANCE.map(e)).collect(Collectors.toList());
        List<Ciphertext> ciphertexts = source.getCiphertextsE().stream().map(c -> map(c, cA0.getElement().getParams())).collect(Collectors.toList());
        MultiExponentiationBasicProofInitialMessage result = new MultiExponentiationBasicProofInitialMessage(cA0, cB.toArray(new PublicCommitment[]{}), ciphertexts.toArray(new Ciphertext[]{}));
        return result;
    }

    default Ciphertext map(CiphertextsE source, ZpGroupParams params) {
        GroupElement gamma = new ZpElement(TypeConverter.stringToBigInteger(source.getGamma().split(";")[0]), params);
        List<GroupElement> phis = Arrays.stream(source.getPhis().split(",")).map(p -> {
            return new ZpElement(TypeConverter.stringToBigInteger(p.split(";")[0]), params);
        }).collect(Collectors.toList())
                ;
        GjosteenElGamalCiphertext result = new GjosteenElGamalCiphertext(gamma, phis);

        return result;
    }

    default MultiExponentiationReductionInitialMessage mapToReduction(IniMEBasic source) {
        PublicCommitment cA0 = PublicCommitmentMapper.INSTANCE.map(source.getCommitmentPublicA0());
        List<PublicCommitment> cB = source.getCommitmentPublicB().stream().map(e -> PublicCommitmentMapper.INSTANCE.map(e)).collect(Collectors.toList());
        List<Ciphertext> ciphertexts = source.getCiphertextsE().stream().map(c -> map(c, cA0.getElement().getParams())).collect(Collectors.toList());
        MultiExponentiationReductionInitialMessage result = new MultiExponentiationReductionInitialMessage(cB.toArray(new PublicCommitment[]{}), ciphertexts.toArray(new Ciphertext[]{}));
        return result;
    }

    /*
        target
            private final PublicCommitment _cA0;
            private final PublicCommitment[] _cB;
            private final Ciphertext[] _E;

        source
            private CommitmentPublicA0__1 commitmentPublicA0;
            private List<CommitmentPublicB__2> commitmentPublicB = new ArrayList<CommitmentPublicB__2>();
            private List<CiphertextsE> ciphertextsE = new ArrayList<CiphertextsE>();


     */

}
