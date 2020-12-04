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

import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.SingleValueArgumentAnswer;
import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.SingleValueArgumentInitMessage;
import com.scytl.products.ov.mixnet.commons.beans.proofs.SingleValueProductProofAnswer;
import com.scytl.products.ov.mixnet.commons.beans.proofs.SingleValueProductProofInitialMessage;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SingleValueArgumentMapper {

    SingleValueArgumentMapper INSTANCE = Mappers.getMapper(SingleValueArgumentMapper.class);

    default SingleValueProductProofInitialMessage mapInitMessage(SingleValueArgumentInitMessage source) {
        PublicCommitment cd = new PublicCommitment(GroupElementMapper.INSTANCE.map(source.getCommitmentD().getElement()));
        PublicCommitment cLowDelta = new PublicCommitment(GroupElementMapper.INSTANCE.map(source.getCommitmentLowerDelta().getElement()));
        PublicCommitment cHighDelta = new PublicCommitment(GroupElementMapper.INSTANCE.map(source.getCommitmentUpperDelta().getElement()));
        return new SingleValueProductProofInitialMessage(cd, cLowDelta, cHighDelta);
    }

    default SingleValueProductProofAnswer mapAnswer(SingleValueArgumentAnswer source) {
        com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent[] tildeA = ExponentMapper.INSTANCE.mapFromList(source.getExponentsTildeA());
        com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent[] tildeB = ExponentMapper.INSTANCE.mapFromList(source.getExponentsTildeB());
        com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent tildeR = ExponentMapper.INSTANCE.map(source.getExponentTildeR());
        com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent tildeS = ExponentMapper.INSTANCE.map(source.getExponentTildeS());
        return new SingleValueProductProofAnswer(tildeA, tildeB, tildeR, tildeS);
    }

}
