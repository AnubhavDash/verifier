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

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.scytl.products.ov.mixnet.commons.beans.proofs.MultiExponentiationBasicProofAnswer;
import com.scytl.products.ov.mixnet.commons.beans.proofs.MultiExponentiationBasicProofInitialMessage;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ProductProofMessage;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProofSecondAnswer;

import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.ShuffleArgumentSecondAnswer;

@Mapper
public interface ShuffleArgumentSecondAnswerMapper {

	ShuffleArgumentSecondAnswerMapper INSTANCE = Mappers.getMapper(ShuffleArgumentSecondAnswerMapper.class);

	default ShuffleProofSecondAnswer map(ShuffleArgumentSecondAnswer source) {
		MultiExponentiationBasicProofInitialMessage iniMEBasic = MultiExponentiationArgumentMapper.INSTANCE
				.mapInitMessage(source.getMultiExponentiationArgumentInitMessage());
		MultiExponentiationBasicProofAnswer ansMEBasic = MultiExponentiationArgumentMapper.INSTANCE
				.mapAnswer(source.getMultiExponentiationArgumentAnswer());
		ProductProofMessage msgPA = ProductArgumentMapper.INSTANCE.map(source.getProductArgumentMessage());
		return new ShuffleProofSecondAnswer(iniMEBasic, ansMEBasic, msgPA);
	}

}
