package ch.post.it.evoting.verifier.block.block1.verifications;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

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
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class VerifyVotingOptions extends AbstractVerification {
	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(1);
		def.setCategory(Category.INTEGRITY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification12.description"));
		def.setId(12);
		def.setName("verifyVotingOptions");
		def.addVerificationTrait(VerificationTrait.BLOCK_1);
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		final VerificationResult result = new VerificationResult();

		// Get the encryption parameters.
		final PathNode epPathNode = pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);
		final EncryptionParameters encryptionParameters = Deserializer.fromJson(epPathNode.getPath(), EncryptionParameters.class);
		final BigInteger g = encryptionParameters.getG();

		// Get the primes from the file.
		final PathNode primesPathNode = pathService.buildFromRootPath(StructureKey.PRIMES, inputDirectoryPath);
		final List<BigInteger> primes = extractPrimes(primesPathNode);

		// Generator must not be part of primes.
		checkGNotInPrimes(g, primes);

		// Check for duplicates.
		checkNoDuplicates(primes);

		// Check that the primes from the files are the smallest primes of the subgroup, excluding g.
		checkPrimesSmallestOfSubgroup(primes, encryptionParameters);

		result.setStatus(Status.OK);
		return result;
	}

	private void checkPrimesSmallestOfSubgroup(List<BigInteger> primes, EncryptionParameters encryptionParameters) {
		// Because g is the smallest prime in the subgroup, start just after it.
		long startInclusive = encryptionParameters.getG().longValueExact() + 1;
		long endExclusive = primes.get(primes.size() - 1).longValueExact();

		final boolean allContained = LongStream.range(startInclusive, endExclusive)
				.parallel()
				.mapToObj(BigInteger::valueOf)
				.filter(MathHelper::isPrime)
				.filter(prime -> MathHelper.isMember(prime, encryptionParameters))
				.allMatch(primes::contains);

		if (!allContained) {
			throw buildVerificationFailureException(
					"There is a prime number of the subgroup not present in the primes list.",
					Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification12.nok.smallest.message"
			);
		}
	}

	private void checkNoDuplicates(List<BigInteger> primes) {
		final Set<BigInteger> primesSet = new HashSet<>(primes);
		if (primesSet.size() != primes.size()) {
			throw buildVerificationFailureException(
					"The primes list contains duplicates.",
					Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification12.nok.duplicates.message"
			);
		}
	}

	private void checkGNotInPrimes(BigInteger g, List<BigInteger> primes) {
		if (primes.contains(g)) {
			throw buildVerificationFailureException(
					"The primes list contains the generator g.",
					Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification12.nok.g.message",
					g.toString()
			);
		}
	}

	private static List<BigInteger> extractPrimes(PathNode primesPathNode) {
		final Iterable<List<String>> iterable = Deserializer.fromCsv(primesPathNode.getPath(), "\n", Arrays::asList);
		return StreamSupport.stream(iterable.spliterator(), false)
				.flatMap(Collection::stream)
				.map(TypeConverter::stringToBigInteger)
				.sorted()
				.collect(Collectors.toList());
	}
}
