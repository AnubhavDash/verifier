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
import java.util.Optional;
import java.util.stream.LongStream;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionParameters;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class CheckGeneratorG extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(1);
		def.setCategory(Category.INTEGRITY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification10.description"));
		def.setId(10);
		def.setName("checkGenerator(g)");
		def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		def.addVerificationTrait(VerificationTrait.BLOCK_1);
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		final PathNode encryptParamsPathNode = pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);
		EncryptionParameters encryptionParameters = Deserializer.fromJson(encryptParamsPathNode.getPath(), EncryptionParameters.class);

		if (!MathHelper.isPrime(encryptionParameters.getG())) {
			throw buildVerificationFailureException(
					"The generator g is not prime",
					Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification10.nok.message",
					encryptionParameters.getG().toString()
			);
		}

		if (!MathHelper.isEulerCriterionValid(encryptionParameters.getG(), encryptionParameters.getP())) {
			throw buildVerificationFailureException(
					"g is not part of the subgroup q",
					Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification10.euler.nok.message",
					encryptionParameters.getG().toString()
			);
		}

		findSmallerPrimeOfSubgroup(encryptionParameters.getG(), encryptionParameters.getP()).ifPresent(s -> {
			throw buildVerificationFailureException(
					"g must be the smallest prime number in the subgroup (p, q)",
					Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification10.smallest.nok.message",
					s.toString(), encryptionParameters.getG().toString()
			);
		});

		result.setStatus(Status.OK);
		return result;
	}

	/**
	 * Check if there is a smaller prime in the subgroup. The parameter g has to be prime and be part of the subgroup. These checks are NOT done in
	 * this method.
	 *
	 * @return A smaller prime of the subgroup if any.
	 */
	private static Optional<BigInteger> findSmallerPrimeOfSubgroup(BigInteger g, BigInteger p) {
		if (MathHelper.areEqual(g, BigInteger.TWO)) {
			return Optional.empty();
		} else {
			// Check is there is a prime number less than g (except 2) that is also a quadratic residue.
			// Be aware that if g is greater than Long.MAX_VALUE, a ArithmeticException is thrown.
			return LongStream.range(2, g.longValueExact())
					.parallel()
					.mapToObj(BigInteger::valueOf)
					.filter(MathHelper::isPrime)
					.filter(prime -> MathHelper.isEulerCriterionValid(prime, p))
					.findAny();
		}
	}

}
