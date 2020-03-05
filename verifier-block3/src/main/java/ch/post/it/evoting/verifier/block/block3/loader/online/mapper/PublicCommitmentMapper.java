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

import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.EncryptedBallot;
import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.EncryptedBallotWrapper;
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

    default PublicCommitment map(EncryptedBallotWrapper source){
        return new PublicCommitment(map(source.getElement()));
    }

    default GroupElement map(EncryptedBallot element){
        return new ZpElement(element.getValue(), element.getP(), element.getQ());
    }

    default PublicCommitment[] map(List<EncryptedBallotWrapper> source){
        List<PublicCommitment> collect = source.stream().map(cpd -> map(cpd)).collect(Collectors.toList());
        return collect.toArray(new PublicCommitment[0]);
    }
}
