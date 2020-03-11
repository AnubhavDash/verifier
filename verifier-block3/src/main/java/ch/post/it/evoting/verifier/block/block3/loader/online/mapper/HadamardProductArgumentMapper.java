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

import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.HadamardProductArgumentAnswer;
import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.HadamardProductArgumentInitMessage;
import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.ZeroArgumentAnswer;
import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.ZeroArgumentInitMessage;
import com.scytl.products.ov.mixnet.commons.beans.proofs.HadamardProductProofAnswer;
import com.scytl.products.ov.mixnet.commons.beans.proofs.HadamardProductProofInitialMessage;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ZeroProofAnswer;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ZeroProofInitialMessage;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.stream.Collectors;

@Mapper
public interface HadamardProductArgumentMapper {

    HadamardProductArgumentMapper INSTANCE = Mappers.getMapper(HadamardProductArgumentMapper.class);

    default HadamardProductProofInitialMessage mapInitMessage(HadamardProductArgumentInitMessage source) {
        PublicCommitment[] cB = source.getCommitmentsB().stream()
                .map(CommitmentMapper.INSTANCE::map)
                .collect(Collectors.toList()).toArray(new PublicCommitment[0]);
        return new HadamardProductProofInitialMessage(cB);
    }

    default HadamardProductProofAnswer mapAnswer(HadamardProductArgumentAnswer source) {
        ZeroArgumentInitMessage ini = source.getZeroArgumentInitMessage();
        ZeroArgumentAnswer ans = source.getZeroArgumentAnswer();
        PublicCommitment cA0 = CommitmentMapper.INSTANCE.map(ini.getCommitmentA0());
        PublicCommitment cBM = CommitmentMapper.INSTANCE.map(ini.getCommitmentBM());
        PublicCommitment[] cD = CommitmentMapper.INSTANCE.mapFromList(ini.getCommitmentD());
        ZeroProofInitialMessage initial = new ZeroProofInitialMessage(cA0, cBM, cD);
        ZeroProofAnswer answer = new ZeroProofAnswer(
                ExponentMapper.INSTANCE.mapFromList(ans.getExponentsA()),
                ExponentMapper.INSTANCE.mapFromList(ans.getExponentsB()),
                ExponentMapper.INSTANCE.map(ans.getExponentR()),
                ExponentMapper.INSTANCE.map(ans.getExponentS()),
                ExponentMapper.INSTANCE.map(ans.getExponentT())
        );
        return new HadamardProductProofAnswer(initial, answer);
    }
}
