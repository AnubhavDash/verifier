package ch.post.it.evoting.verifier.block.block3.loader.online.mapper;

import ch.post.it.evoting.verifier.dto.onlinemixing.*;
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
        ZeroProofAnswer answer = new ZeroProofAnswer(mapFromListExponentsA(ans.getExponentsA()), mapFromListExponentsB(ans.getExponentsB()), map(ans.getExponentR()), map(ans.getExponentS()), map(ans.getExponentT()));
        return new HadamardProductProofAnswer(initial, answer);
    }

    default SingleValueProductProofInitialMessage map(IniSVA source) {
        PublicCommitment cd = new PublicCommitment(PublicCommitmentMapper.INSTANCE.map(source.getCommitmentPublicD().getElement()));
        PublicCommitment cLowDelta = new PublicCommitment(PublicCommitmentMapper.INSTANCE.map(source.getCommitmentPublicLowDelta().getElement()));
        PublicCommitment cHighDelta = new PublicCommitment(PublicCommitmentMapper.INSTANCE.map(source.getCommitmentPublicHighDelta().getElement()));
        return new SingleValueProductProofInitialMessage(cd, cLowDelta, cHighDelta);
    }

    default SingleValueProductProofAnswer map(AnsSVA source) {
        Exponent[] tildeA = mapFromListTildeA(source.getExponentsTildeA());
        Exponent[] tildeB = mapFromListTildeB(source.getExponentsTildeB());
        Exponent tildeR = map(source.getExponentTildeR());
        Exponent tildeS = map(source.getExponentTildeS());
        return new SingleValueProductProofAnswer(tildeA, tildeB, tildeR, tildeS);
    }

    default Exponent map(ExponentTildeR source) {
        return new Exponent(source.getValue(), source.getQ());
    }

    default Exponent map(ExponentTildeS source) {
        return new Exponent(source.getValue(), source.getQ());
    }

    default Exponent map(ExponentT source) {
        return new Exponent(source.getValue(), source.getQ());
    }

    default Exponent map(ExponentR source) {
        return new Exponent(source.getValue(), source.getQ());
    }

    default Exponent map(ExponentS source) {
        return new Exponent(source.getValue(), source.getQ());
    }

    default Exponent mapFromTildeA(ExponentsTildeA source) {
        return new Exponent(source.getValue(), source.getQ());
    }

    default Exponent mapFromTildeB(ExponentsTildeB source) {
        return new Exponent(source.getValue(), source.getQ());
    }

    default Exponent mapFromExponentsA(ExponentsA source) {
        return new Exponent(source.getValue(), source.getQ());
    }

    default Exponent mapFromExponentsB(ExponentsB source) {
        return new Exponent(source.getValue(), source.getQ());
    }

    default Exponent[] mapFromListTildeA(List<ExponentsTildeA> source) {
        List<Exponent> collect = source.stream().map(this::mapFromTildeA).collect(Collectors.toList());
        return collect.toArray(new Exponent[0]);
    }

    default Exponent[] mapFromListTildeB(List<ExponentsTildeB> source) {
        List<Exponent> collect = source.stream().map(this::mapFromTildeB).collect(Collectors.toList());
        return collect.toArray(new Exponent[0]);
    }

    default Exponent[] mapFromListExponentsA(List<ExponentsA> source) {
        List<Exponent> collect = source.stream().map(this::mapFromExponentsA).collect(Collectors.toList());
        return collect.toArray(new Exponent[0]);
    }

    default Exponent[] mapFromListExponentsB(List<ExponentsB> source) {
        List<Exponent> collect = source.stream().map(this::mapFromExponentsB).collect(Collectors.toList());
        return collect.toArray(new Exponent[0]);
    }

}
