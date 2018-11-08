/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block3.loader.online.mapper;

import ch.post.it.evoting.verifier.dto.onlinemixing.*;
import com.scytl.products.ov.mixnet.commons.beans.proofs.*;
import com.scytl.products.ov.mixnet.commons.homomorphic.Randomness;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalRandomness;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AnsMEBasicMapper {


    AnsMEBasicMapper INSTANCE = Mappers.getMapper(AnsMEBasicMapper.class);
    /*

    @Mappings({
            @Mapping(target = "_a", source = "exponentsA"),
            @Mapping(target = "_r", source = "exponentR"),
            @Mapping(target = "_b", source = "exponentsB"),
            @Mapping(target = "_s", source = "exponentS"),
            @Mapping(target = "_tau", source = "randomnessTau")
    })
    MultiExponentiationBasicProofAnswer map(AnsMEBasic source);

    default MultiExponentiationReductionAnswer mapToReduction(AnsMEBasic ansSource, IniMEBasic iniSource){
        MultiExponentiationBasicProofInitialMessage iniBasic = IniMEBasicMapper.INSTANCE.map(iniSource);
        MultiExponentiationReductionInitialMessage iniReduct = IniMEBasicMapper.INSTANCE.mapToReduction(iniSource);
        MultiExponentiationBasicProofAnswer ansBasic = map(ansSource);
        MultiExponentiationReductionAnswer ansReduct = null;

        MultiExponentiationReductionAnswer result = new MultiExponentiationReductionAnswer(ansBasic.getExponentsB(), ansBasic.getExponentS(), iniBasic, ansBasic, iniReduct, ansReduct);
        return result;

            @JsonProperty("iniBasic") final MultiExponentiationBasicProofInitialMessage iniBasic,
            @JsonProperty("ansBasic") final MultiExponentiationBasicProofAnswer ansBasic,
            @JsonProperty("iniReduct") final MultiExponentiationReductionInitialMessage iniReduct,
            @JsonProperty("ansReduct") final MultiExponentiationReductionAnswer ansReduct) {

    }

    default Exponent map(ExponentsA__1 source){
        return new Exponent(source.getValue(), source.getQ());
    }

    default Exponent map(ExponentR__1 source){
        return new Exponent(source.getValue(), source.getQ());
    }

    default Exponent map(ExponentsB__1 source){
        return new Exponent(source.getValue(), source.getQ());
    }

    default Exponent map(ExponentS__1 source){
        return new Exponent(source.getValue(), source.getQ());
    }

    default Randomness map(RandomnessTau source){
        Randomness result = new GjosteenElGamalRandomness(source.getRandomnessValue().getValue().longValue(), source.getRandomnessValue().getQ());
        return result;
    }


    target
        private final Exponent[] _a;
        private final Exponent _r;
        private final Exponent[] _b;
        private final Exponent _s;
        private final Randomness _tau;

    source
        private List<ExponentsA__1> exponentsA = new ArrayList<ExponentsA__1>();
        private ExponentR__1 exponentR;
        private ExponentsB__1 exponentsB;
        private ExponentS__1 exponentS;
        private RandomnessTau randomnessTau;
     */

}
