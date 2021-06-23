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
package ch.post.it.evoting.verifier.block.block1.verifications;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.PublicKey;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.dto.ElectoralAuthority;

public class IsMemberOfGroupPKEA extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(1);
		def.setCategory(Category.INTEGRITY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification07.description"));
		def.setId(7);
		def.setName("isMemberOfGroup(pk_ea)");
		def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		def.addVerificationTrait(VerificationTrait.BLOCK_1);
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		final PathNode electoralAuthPathNode = pathService.buildFromRootPath(StructureKey.ELECTORAL_PUBLIC_KEY, inputDirectoryPath);
		ElectoralAuthority electoralAuthority = Deserializer.fromJson(electoralAuthPathNode.getPath(), ElectoralAuthority.class);

		String publicKeyB64 = electoralAuthority.getPublicKey();
		byte[] decoded = TypeConverter.base64ToByte(publicKeyB64);
		PublicKey publicKey = Deserializer.fromJson(decoded, PublicKey.class);

		BigInteger p = publicKey.getGroup().getP();
		List<BigInteger> elements = publicKey.getKeys();
		if (elements.isEmpty()) {
			throw buildVerificationFailureException(
					"No such Elements was found in the publicKey",
					Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification07.nok.message.no.elements"
			);
		} else {
			List<String> errors = elements.stream()
					.filter(bigInteger -> !MathHelper.isEulerCriterionValid(bigInteger, p))
					.map(TypeConverter::bigIntegerToB64String)
					.collect(Collectors.toList());

			if (!errors.isEmpty()) {
				throw buildVerificationFailureException(
						"Euler criterion does not equal to 1",
						Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification07.nok.message",
						errors.toString()
				);
			}
		}

		result.setStatus(Status.OK);
		return result;
	}

}
