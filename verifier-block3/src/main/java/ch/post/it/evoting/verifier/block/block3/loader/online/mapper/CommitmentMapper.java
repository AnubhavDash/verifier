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

import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.Commitment;

@Mapper
public interface CommitmentMapper {

	CommitmentMapper INSTANCE = Mappers.getMapper(CommitmentMapper.class);

	default PublicCommitment map(Commitment source) {
		return new PublicCommitment(GroupElementMapper.INSTANCE.map(source.getElement()));
	}

	default PublicCommitment[] mapFromList(List<Commitment> source) {
		List<PublicCommitment> collect = source.stream()
				.map(this::map)
				.collect(Collectors.toList());
		return collect.toArray(new PublicCommitment[0]);
	}
}
