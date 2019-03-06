/**
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
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

}
