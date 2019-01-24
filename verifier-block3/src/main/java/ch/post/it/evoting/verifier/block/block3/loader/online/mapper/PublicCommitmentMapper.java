package ch.post.it.evoting.verifier.block.block3.loader.online.mapper;

import ch.post.it.evoting.verifier.dto.onlinemixing.*;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface PublicCommitmentMapper {

    PublicCommitmentMapper INSTANCE = Mappers.getMapper(PublicCommitmentMapper.class);

    default PublicCommitment map(CommitmentPublicA0 source){
        return new PublicCommitment(map(source.getElement()));
    }

    default PublicCommitment map(CommitmentPublicB source){
        return new PublicCommitment(map(source.getElement()));
    }

    default PublicCommitment map(CommitmentPublicB__1 source){
        return new PublicCommitment(map(source.getElement()));
    }

    default PublicCommitment map(CommitmentPublicBM source){
        return new PublicCommitment(map(source.getElement()));
    }

    default PublicCommitment map(CommitmentPublicD source){
        return new PublicCommitment(map(source.getElement()));
    }

    default PublicCommitment map(CommitmentPublicD__1 source){
        return new PublicCommitment(map(source.getElement()));
    }

    default PublicCommitment map(CommitmentPublicA0__1 source){
        return new PublicCommitment(map(source.getElement()));
    }

    default PublicCommitment map(CommitmentPublicB__2 source){
        return new PublicCommitment(map(source.getElement()));
    }

    default GroupElement map(Element__2 element){
        return new ZpElement(element.getValue(), element.getP(), element.getQ());
    }

    default GroupElement map(Element__3 element){
        return new ZpElement(element.getValue(), element.getP(), element.getQ());
    }

    default GroupElement map(Element__4 element){
        return new ZpElement(element.getValue(), element.getP(), element.getQ());
    }

    default GroupElement map(Element__5 element){
        return new ZpElement(element.getValue(), element.getP(), element.getQ());
    }

    default GroupElement map(Element__6 element){
        return new ZpElement(element.getValue(), element.getP(), element.getQ());
    }

    default GroupElement map(Element__7 element){
        return new ZpElement(element.getValue(), element.getP(), element.getQ());
    }

    default GroupElement map(Element__8 element){
        return new ZpElement(element.getValue(), element.getP(), element.getQ());
    }

    default GroupElement map(Element__9 element){
        return new ZpElement(element.getValue(), element.getP(), element.getQ());
    }

    default GroupElement map(Element__10 element){
        return new ZpElement(element.getValue(), element.getP(), element.getQ());
    }

    default GroupElement map(Element__11 element){
        return new ZpElement(element.getValue(), element.getP(), element.getQ());
    }

    default PublicCommitment[] map(List<CommitmentPublicD__1> source){
        List<PublicCommitment> collect = source.stream().map(cpd -> map(cpd)).collect(Collectors.toList());
        return collect.toArray(new PublicCommitment[0]);
    }
}
