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

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import ch.post.it.evoting.verifier.common.block.tools.*;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.LongStream;

public class CheckGeneratorG extends AbstractVerification {

    private static final BigInteger BIG_INTEGER_TWO = new BigInteger("2");

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

        final PathNode pathNode = pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);
        EncryptionGroup encryptionGroup = Deserializer.fromJson(pathNode.getPath(), EncryptionGroup.class);

        if (!MathHelper.isPrime(encryptionGroup.getG())) {
            throw buildVerificationFailureException(
                    "The generator g is not prime",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification10.nok.message",
                    encryptionGroup.getG().toString()
            );
        }

        if (!MathHelper.isEulerCriterionValid(encryptionGroup.getG(), encryptionGroup.getP())) {
            throw buildVerificationFailureException(
                    "g is not part of the subgroup q",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification10.euler.nok.message",
                    encryptionGroup.getG().toString()
            );
        }

        findSmallerPrimeOfSubgroup(encryptionGroup.getG(), encryptionGroup.getP()).ifPresent(s -> {
            throw buildVerificationFailureException(
                    "g must be the smallest prime number in the subgroup (p, q)",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification10.smallest.nok.message",
                    s.toString(), encryptionGroup.getG().toString()
            );
        });

        result.setStatus(Status.OK);
        return result;
    }

    /**
     * Check if there is a smaller prime in the subgroup. The parameter g has to be prime and be part of the subgroup. These checks are
     * NOT done in this method.
     *
     * @return A smaller prime of the subgroup if any.
     */
    private Optional<BigInteger> findSmallerPrimeOfSubgroup(BigInteger g, BigInteger p) {
        if (MathHelper.areEqual(g, BIG_INTEGER_TWO)) {
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
