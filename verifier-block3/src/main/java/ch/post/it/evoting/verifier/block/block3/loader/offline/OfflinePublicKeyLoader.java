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
package ch.post.it.evoting.verifier.block.block3.loader.offline;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;

import ch.post.it.evoting.verifier.block.block3.scytl.loader.PublicKeyLoader;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.PublicKey;
import ch.post.it.evoting.verifier.dto.PublicKey__1;

public class OfflinePublicKeyLoader implements PublicKeyLoader {
	private final Path path;

	public OfflinePublicKeyLoader(Path path) {
		this.path = path;
	}

	@Override
	public ElGamalPublicKey getPublicKey() throws IOException {
		PublicKey json = Deserializer.fromJson(path.toFile(), "publicKey\\.json", PublicKey.class);
		PublicKey__1 publicKey = json.getPublicKey();

		ZpGroupParams params = new ZpGroupParams(TypeConverter.base64ToBigInteger(publicKey.getZpSubgroup().getP()),
				TypeConverter.base64ToBigInteger(publicKey.getZpSubgroup().getQ()));

		ZpGroup zpGroup = new ZpGroup(params, new ZpElement(TypeConverter.base64ToBigInteger(publicKey.getZpSubgroup().getG()), params));

		List<GroupElement> pubKeys = publicKey.getElements().stream().map(s -> new ZpElement(TypeConverter.base64ToBigInteger(s), params))
				.collect(Collectors.toList());

		return new ElGamalPublicKey(pubKeys, zpGroup);
	}
}