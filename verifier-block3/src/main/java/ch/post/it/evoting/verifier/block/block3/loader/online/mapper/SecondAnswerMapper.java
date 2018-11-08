package ch.post.it.evoting.verifier.block.block3.loader.online.mapper;

import ch.post.it.evoting.verifier.dto.onlinemixing.*;
import com.scytl.products.ov.mixnet.commons.beans.proofs.*;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SecondAnswerMapper {

    /*


    SecondAnswerMapper INSTANCE = Mappers.getMapper(SecondAnswerMapper.class);

    @Mappings({
            @Mapping(target = "_iniMEBasic", source = "iniMEBasic"),
            @Mapping(target = "_ansMEBasic", source = "ansMEBasic"),
            @Mapping(target = "_iniMEReduct", ignore = true),
            @Mapping(target = "_ansMEReduct", ignore = true),
            @Mapping(target = "_msgPA", source = "MsgPA")
    })
    ShuffleProofSecondAnswer map(SecondAnswer source);

    default ProductProofMessage map(MsgPA source){
        PublicCommitment cb = new PublicCommitment((GroupElement) source.getCommitmentPublicB().getElement());
        SingleValueProductProofInitialMessage iniSVA = map(source.getIniSVA());
        // TODO Thierry do the 3 mappers below in order to create the result object
        SingleValueProductProofAnswer ansVa = map(source.getAnsSVA());
        HadamardProductProofInitialMessage iniHpa = null;
        HadamardProductProofAnswer ansHpa = null;
        ProductProofMessage result = new ProductProofMessage(cb, iniSVA, ansVa, iniHpa, ansHpa);
        return result;
    }
    default SingleValueProductProofInitialMessage map(IniSVA source){
        PublicCommitment cd = new PublicCommitment((GroupElement) source.getCommitmentPublicD());
        PublicCommitment cLowDelta = new PublicCommitment((GroupElement) source.getCommitmentPublicLowDelta());
        PublicCommitment cHighDelta = new PublicCommitment((GroupElement) source.getCommitmentPublicHighDelta());
        return new SingleValueProductProofInitialMessage(cd, cLowDelta, cHighDelta);
    }
    default SingleValueProductProofAnswer map(AnsSVA source){
        // TODO Thierry do the mapper ExponentTildeX to Exponent
        Exponent[] tildeA = null;
        Exponent[] tildeB = null;
        Exponent tildeR = null;
        Exponent tildeS  = null;
        return new SingleValueProductProofAnswer(tildeA, tildeB, tildeR, tildeS);
    }

    /*

    target
        private final MultiExponentiationBasicProofInitialMessage _iniMEBasic;
        private final MultiExponentiationBasicProofAnswer _ansMEBasic;
        private final MultiExponentiationReductionInitialMessage _iniMEReduct;
        private final MultiExponentiationReductionAnswer _ansMEReduct;
        private final ProductProofMessage _msgPA;



    source
        private MsgPA msgPA;
        private IniMEBasic iniMEBasic;
        private AnsMEBasic ansMEBasic;

             private MsgPA msgPA;
                private CommitmentPublicB commitmentPublicB;
                private IniSVA iniSVA;
                private AnsSVA ansSVA;
                private IniHPA iniHPA;
                private AnsHPA ansHPA;

     */


}
