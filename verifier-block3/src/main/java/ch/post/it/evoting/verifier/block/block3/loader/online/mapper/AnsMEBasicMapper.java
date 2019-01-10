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

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface AnsMEBasicMapper {

    AnsMEBasicMapper INSTANCE = Mappers.getMapper(AnsMEBasicMapper.class);

    default MultiExponentiationBasicProofAnswer map(AnsMEBasic source){
        List<Exponent> collect = source.getExponentsA().stream().map(this::map).collect(Collectors.toList());
        Exponent[] a = collect.toArray(new Exponent[collect.size()]);
        Exponent r = map(source.getExponentR());
        Exponent b = map(source.getExponentsB());
        Exponent s = map(source.getExponentS());
        Randomness tau = map(source.getRandomnessTau());
        return new MultiExponentiationBasicProofAnswer(a, r, b, s, tau);
    }

    default MultiExponentiationReductionAnswer mapToReduction(AnsMEBasic ansSource, IniMEBasic iniSource){
        MultiExponentiationBasicProofInitialMessage iniBasic = IniMEBasicMapper.INSTANCE.map(iniSource);
        MultiExponentiationReductionInitialMessage iniReduct = IniMEBasicMapper.INSTANCE.mapToReduction(iniSource);
        MultiExponentiationBasicProofAnswer ansBasic = map(ansSource);
        MultiExponentiationReductionAnswer ansReduct = null;

        MultiExponentiationReductionAnswer result = new MultiExponentiationReductionAnswer(ansBasic.getExponentsB(), ansBasic.getExponentS(), iniBasic, ansBasic, iniReduct, ansReduct);
        return result;
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

}
