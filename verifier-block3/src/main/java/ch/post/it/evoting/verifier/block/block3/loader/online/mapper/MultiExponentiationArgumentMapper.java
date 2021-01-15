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

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.scytl.products.ov.mixnet.commons.beans.proofs.MultiExponentiationBasicProofAnswer;
import com.scytl.products.ov.mixnet.commons.beans.proofs.MultiExponentiationBasicProofInitialMessage;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.homomorphic.Randomness;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalRandomness;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.MultiExponentiationArgumentAnswer;
import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.MultiExponentiationArgumentInitMessage;
import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.RandomnessTau;

@Mapper
public interface MultiExponentiationArgumentMapper {

	MultiExponentiationArgumentMapper INSTANCE = Mappers.getMapper(MultiExponentiationArgumentMapper.class);

	default MultiExponentiationBasicProofInitialMessage mapInitMessage(MultiExponentiationArgumentInitMessage source) {
		PublicCommitment cA0 = CommitmentMapper.INSTANCE.map(source.getCommitmentA0());
		List<PublicCommitment> cB = source.getCommitmentsB().stream()
				.map(CommitmentMapper.INSTANCE::map)
				.collect(Collectors.toList());
		List<Ciphertext> ciphertexts = source.getCiphertextsE().stream()
				.map(c -> CiphertextMapper.INSTANCE.map(c, cA0.getElement().getParams()))
				.collect(Collectors.toList());

		return new MultiExponentiationBasicProofInitialMessage(cA0, cB.toArray(new PublicCommitment[] {}), ciphertexts.toArray(new Ciphertext[] {}));
	}

	default MultiExponentiationBasicProofAnswer mapAnswer(MultiExponentiationArgumentAnswer source) {
		List<com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent> collect = source.getExponentsA().stream()
				.map(ExponentMapper.INSTANCE::map)
				.collect(Collectors.toList());
		com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent[] a = collect
				.toArray(new com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent[collect.size()]);
		com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent r = ExponentMapper.INSTANCE.map(source.getExponentR());
		com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent b = ExponentMapper.INSTANCE.map(source.getExponentsB());
		com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent s = ExponentMapper.INSTANCE.map(source.getExponentS());
		Randomness tau = mapRandomness(source.getRandomnessTau());
		return new MultiExponentiationBasicProofAnswer(a, r, b, s, tau);
	}

	default Randomness mapRandomness(RandomnessTau source) {
		return new GjosteenElGamalRandomness(source.getExponent().getValue(), source.getExponent().getQ());
	}

}
