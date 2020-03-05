/*
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

import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.*;
import com.scytl.products.ov.mixnet.commons.beans.proofs.*;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface SecondAnswerMapper {

    SecondAnswerMapper INSTANCE = Mappers.getMapper(SecondAnswerMapper.class);

    default ShuffleProofSecondAnswer map(SecondAnswer source) {
        MultiExponentiationBasicProofInitialMessage iniMEBasic = IniMEBasicMapper.INSTANCE.map(source.getIniMEBasic());
        MultiExponentiationBasicProofAnswer ansMEBasic = AnsMEBasicMapper.INSTANCE.map(source.getAnsMEBasic());
        ProductProofMessage msgPA = map(source.getMsgPA());
        return new ShuffleProofSecondAnswer(iniMEBasic, ansMEBasic, msgPA);
    }

    default ProductProofMessage map(MsgPA source) {
        PublicCommitment cb = new PublicCommitment(PublicCommitmentMapper.INSTANCE.map(source.getCommitmentPublicB().getElement()));
        SingleValueProductProofInitialMessage iniSVA = map(source.getIniSVA());
        SingleValueProductProofAnswer ansVa = map(source.getAnsSVA());
        HadamardProductProofInitialMessage iniHpa = map(source.getIniHPA());
        HadamardProductProofAnswer ansHpa = map(source.getAnsHPA());
        ProductProofMessage result = new ProductProofMessage(cb, iniSVA, ansVa, iniHpa, ansHpa);
        return result;
    }

    default HadamardProductProofInitialMessage map(IniHPA source) {
        PublicCommitment[] cB = source.getCommitmentPublicB().stream()
                .map(cpb -> PublicCommitmentMapper.INSTANCE.map(cpb))
                .collect(Collectors.toList()).toArray(new PublicCommitment[0]);
        return new HadamardProductProofInitialMessage(cB);
    }

    default HadamardProductProofAnswer map(AnsHPA source) {
        Initial ini = source.getInitial();
        Answer ans = source.getAnswer();
        PublicCommitment cA0 = PublicCommitmentMapper.INSTANCE.map(ini.getCommitmentPublicA0());
        PublicCommitment cBM = PublicCommitmentMapper.INSTANCE.map(ini.getCommitmentPublicBM());
        PublicCommitment[] cD = PublicCommitmentMapper.INSTANCE.map(ini.getCommitmentPublicD());
        ZeroProofInitialMessage initial = new ZeroProofInitialMessage(cA0, cBM, cD);
        ZeroProofAnswer answer = new ZeroProofAnswer(mapFromList(ans.getExponentsA()), mapFromList(ans.getExponentsB()), map(ans.getExponentR()), map(ans.getExponentS()), map(ans.getExponentT()));
        return new HadamardProductProofAnswer(initial, answer);
    }

    default SingleValueProductProofInitialMessage map(IniSVA source) {
        PublicCommitment cd = new PublicCommitment(PublicCommitmentMapper.INSTANCE.map(source.getCommitmentPublicD().getElement()));
        PublicCommitment cLowDelta = new PublicCommitment(PublicCommitmentMapper.INSTANCE.map(source.getCommitmentPublicLowDelta().getElement()));
        PublicCommitment cHighDelta = new PublicCommitment(PublicCommitmentMapper.INSTANCE.map(source.getCommitmentPublicHighDelta().getElement()));
        return new SingleValueProductProofInitialMessage(cd, cLowDelta, cHighDelta);
    }

    default SingleValueProductProofAnswer map(AnsSVA source) {
        Exponent[] tildeA = mapFromList(source.getExponentsTildeA());
        Exponent[] tildeB = mapFromList(source.getExponentsTildeB());
        Exponent tildeR = map(source.getExponentTildeR());
        Exponent tildeS = map(source.getExponentTildeS());
        return new SingleValueProductProofAnswer(tildeA, tildeB, tildeR, tildeS);
    }

    default Exponent map(ExponentValue source) {
        return new Exponent(source.getValue(), source.getQ());
    }

    default Exponent[] mapFromList(List<ExponentValue> source) {
        List<Exponent> collect = source.stream().map(this::map).collect(Collectors.toList());
        return collect.toArray(new Exponent[0]);
    }

}
