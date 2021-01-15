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

import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.Exponent;

@Mapper
public interface ExponentMapper {

	ExponentMapper INSTANCE = Mappers.getMapper(ExponentMapper.class);

	default com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent map(Exponent source) {
		return new com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent(source.getValue(), source.getQ());
	}

	default com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent[] mapFromList(List<Exponent> source) {
		List<com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent> collect = source.stream().map(this::map).collect(Collectors.toList());
		return collect.toArray(new com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent[0]);
	}

}
