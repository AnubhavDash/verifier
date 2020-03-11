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

import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.ProductArgumentMessage;
import com.scytl.products.ov.mixnet.commons.beans.proofs.*;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductArgumentMapper {

    ProductArgumentMapper INSTANCE = Mappers.getMapper(ProductArgumentMapper.class);

    default ProductProofMessage map(ProductArgumentMessage source) {
        PublicCommitment cb = new PublicCommitment(GroupElementMapper.INSTANCE.map(source.getCommitmentB().getElement()));
        SingleValueProductProofInitialMessage iniSVA = SingleValueArgumentMapper.INSTANCE.mapInitMessage(source.getSingleValueArgumentInitMessage());
        SingleValueProductProofAnswer ansVa = SingleValueArgumentMapper.INSTANCE.mapAnswer(source.getSingleValueArgumentAnswer());
        HadamardProductProofInitialMessage iniHpa = HadamardProductArgumentMapper.INSTANCE.mapInitMessage(source.getHadamardProductArgumentInitMessage());
        HadamardProductProofAnswer ansHpa = HadamardProductArgumentMapper.INSTANCE.mapAnswer(source.getHadamardProductArgumentAnswer());
        return new ProductProofMessage(cb, iniSVA, ansVa, iniHpa, ansHpa);
    }

}
