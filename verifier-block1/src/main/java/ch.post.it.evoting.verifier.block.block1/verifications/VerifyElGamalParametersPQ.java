package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionParameters;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import org.apache.commons.lang.ArrayUtils;
import org.bouncycastle.crypto.digests.SHAKEDigest;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.BitSet;
import java.util.OptionalInt;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class VerifyElGamalParametersPQ extends AbstractVerification {

    static final int EXPECTED_P_BIT_LEN = 2048;
    static final int EXPECTED_Q_BIT_LEN = 2047;
    static final int MIN_SEED_BIT_LEN = 256;
    
    // Security strength of the SHAKE digest. This can only be 128 or 256.
    private static final int DIGEST_SECURITY_BITS_STRENGTH = 128;
    private static final String PREFIX_PRIME_Q = "prime q";
    private static final int CERTAINTY_LEVEL = 112;

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                "verification11.description"));
        def.setId(11);
        def.setName("verifyElGamalParameterPQ");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        final VerificationResult result = new VerificationResult();

        // Deserialize file.
        final PathNode pathNode = pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);
        final EncryptionParameters encryptionParameters = Deserializer.fromJson(pathNode.getPath(), EncryptionParameters.class);

        // Extract parameters.
        final BigInteger p = encryptionParameters.getEncryptionGroup().getP();
        final BigInteger q = encryptionParameters.getEncryptionGroup().getQ();
        final String seed = encryptionParameters.getSeed();
        final int qCounter = encryptionParameters.getQCounter();

        final int L = p.bitLength();
        final int N = q.bitLength();

        // The seed is encoded in Base64.
        final byte[] seedDecodedBytes = Base64.getDecoder().decode(seed);
        final int seedByteLength = seedDecodedBytes.length;

        // Validate input parameters.
        checkPQLengths(L, N);
        checkSeedLength(seedByteLength * Byte.SIZE);
        checkQCounterValue(qCounter, L);

        // Desired digest output bit length.
        final int digestBitLength = N - 1;

        // Compute needed byte length.
        final int outputByteLength = (digestBitLength + Byte.SIZE - 1) / Byte.SIZE;

        // Create byte array for prefix.
        final byte[] prefix = PREFIX_PRIME_Q.getBytes(StandardCharsets.UTF_8);

        // Check if there is a smaller counter value that gives another valid pair p,q.
        final OptionalInt optionalCounter = IntStream.range(0, qCounter)
                .parallel()
                .filter(givesValidValues(N, seedDecodedBytes, seedByteLength, digestBitLength, outputByteLength, prefix))
                .findAny();

        if (optionalCounter.isPresent()) {
            throw buildVerificationFailureException(
                    "A smaller counter value giving another pair of valid primes was found.",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification11.nok.counter.val.message",
                    String.valueOf(optionalCounter.getAsInt())
            );
        }

        // No valid pair has been found for smaller value of qCounter, so the final value qCounter should give the expected pair p,q.
        final BigInteger computedQ = computeQ(N, seedDecodedBytes, seedByteLength, digestBitLength, outputByteLength, prefix, qCounter);

        // Compute p from q and check if they match the values from the file.
        final BigInteger computedP = computedQ.multiply(BigInteger.TWO).add(BigInteger.ONE);
        if (!MathHelper.areEqual(computedP, p) || !MathHelper.areEqual(computedQ, q)) {
            throw buildVerificationFailureException(
                    "The calculated values of (p,q) do not correspond to the provided values of (p,q).",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification11.nok.message"
            );
        }

        result.setStatus(Status.OK);
        return result;
    }

    private IntPredicate givesValidValues(int n, byte[] seedDecodedBytes, int seedByteLength, int digestBitLength,
                                          int outputByteLength, byte[] prefix) {
        return counter -> {
            final BigInteger computedQ = computeQ(n, seedDecodedBytes, seedByteLength, digestBitLength, outputByteLength, prefix, counter);

            if (!computedQ.isProbablePrime(CERTAINTY_LEVEL)) {
                return false;
            } else {
                final BigInteger computedP = computedQ.multiply(BigInteger.TWO).add(BigInteger.ONE);
                return computedP.isProbablePrime(CERTAINTY_LEVEL);
            }

        };
    }

    private BigInteger computeQ(int n, byte[] seedDecodedBytes, int seedByteLength, int digestBitLength, int outputByteLength, byte[] prefix,
                                int counter) {
        // Byte array to digest.
        final ByteBuffer byteBuffer = ByteBuffer.allocate(prefix.length + seedByteLength + Integer.BYTES);
        byteBuffer.put(prefix);
        byteBuffer.put(seedDecodedBytes);
        byteBuffer.putInt(counter);
        final byte[] in = byteBuffer.array();

        // Update digest with the byte array to hash.
        final SHAKEDigest shakeDigest = new SHAKEDigest(DIGEST_SECURITY_BITS_STRENGTH);
        shakeDigest.update(in, 0, in.length);

        // Perform the hash.
        byte[] digest = new byte[outputByteLength];
        shakeDigest.doFinal(digest, 0, outputByteLength);

        // Go to BitSet so we can easily truncate the not needed bits to match the desired digest output bit length (digestBitLength).
        final BitSet bitSet = BitSet.valueOf(digest);
        truncate(digestBitLength, bitSet);

        // Calculate q.
        final BigInteger U = fromBitSet(bitSet);
        final BigInteger power = BigInteger.TWO.pow(n - 1);

        return power.add(U).add(BigInteger.ONE).subtract(U.mod(BigInteger.TWO));
    }

    private void checkQCounterValue(final int qCounter, final int L) {
        if (qCounter > 16 * StrictMath.pow(L, 2)) {
            throw buildVerificationFailureException(
                    "The q counter is too large.",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification11.nok.counter.len.message"
            );
        }
    }

    private void checkSeedLength(final int seedBitLength) {
        if (seedBitLength < MIN_SEED_BIT_LEN) {
            throw buildVerificationFailureException(
                    "The seed has not the required size (" + MIN_SEED_BIT_LEN + " bits).",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification11.nok.seed.message",
                    String.valueOf(MIN_SEED_BIT_LEN)
            );
        }
    }

    private void truncate(final int digestBitLength, final BitSet bitSet) {
        if (digestBitLength < bitSet.length()) {
            bitSet.clear(digestBitLength, bitSet.length());
        }
    }

    private static BigInteger fromBitSet(final BitSet bitSet) {
        // We need to reverse the order of the bytes because bitSet.toByteArray gives little-endian array.
        byte[] bitSetBytes = bitSet.toByteArray();
        ArrayUtils.reverse(bitSetBytes);

        return new BigInteger(bitSetBytes);
    }

    private void checkPQLengths(final int L, final int N) {
        if (L != EXPECTED_P_BIT_LEN) {
            throw buildVerificationFailureException(
                    "p bit length is not equal to " + EXPECTED_P_BIT_LEN + " bits.",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification11.nok.p.message",
                    String.valueOf(EXPECTED_P_BIT_LEN)
            );
        }
        if (N != EXPECTED_Q_BIT_LEN) {
            throw buildVerificationFailureException(
                    "q bit length is not equal to " + EXPECTED_Q_BIT_LEN + " bits.",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification11.nok.q.message",
                    String.valueOf(EXPECTED_Q_BIT_LEN)
            );
        }
    }

}
