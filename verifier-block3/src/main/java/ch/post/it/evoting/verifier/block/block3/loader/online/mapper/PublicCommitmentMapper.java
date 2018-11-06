package ch.post.it.evoting.verifier.block.block3.loader.online.mapper;

import ch.post.it.evoting.verifier.dto.onlinemixing.*;
import com.scytl.products.ov.mixnet.commons.beans.proofs.MultiExponentiationBasicProofInitialMessage;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProofSecondAnswer;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PublicCommitmentMapper {

    PublicCommitmentMapper INSTANCE = Mappers.getMapper(PublicCommitmentMapper.class);
    /*


    @Mappings({
            @Mapping(target = "_commitment", source = "element")
    })
    PublicCommitment map(CommitmentPublicA0__1 source);

    @Mappings({
            @Mapping(target = "_commitment", source = "element")
    })
    PublicCommitment map(CommitmentPublicB__2 source);


    default GroupElement map(Element__10 element){
        return new ZpElement(element.getValue(), element.getP(), element.getQ());
    }

    default GroupElement map(Element__11 element){
        return new ZpElement(element.getValue(), element.getP(), element.getQ());
    }

     */
}
