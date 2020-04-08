package ch.post.it.evoting.verifier.common.block.tools;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public final class ToStringHelper {

    private ToStringHelper() {
        // private constructor.
    }

    public static String integerToString(int i) {
        return String.valueOf(i);
    }

    public static String integerToString(Integer integer) {
        return integer.toString();
    }

    public static String bigIntegerToString(BigInteger bigInteger) {
        return bigInteger.toString(10);
    }

    /**
     * Utility functions - 1.1.3.1 Exponent.
     *
     * @param v The value of the exponent.
     * @param q The cardinality of the subgroup.
     * @return The string representation of the exponent.
     */
    public static String exponentToString(BigInteger v, BigInteger q) {
        return "Exponent [q=" + bigIntegerToString(q) + ", value=" + bigIntegerToString(v) + "]";
    }

    /**
     * Utility functions - 1.1.3.2 ZpGroupElement.
     *
     * @param v The value of the exponent.
     * @param p The congruence class.
     * @param q The cardinality of the subgroup.
     * @return The string representation of an element in the <code>Z<sub>p</sub></code> group.
     */
    public static String zpGroupElementToString(BigInteger v, BigInteger p, BigInteger q) {
        return "ZpGroupElement [value=" + bigIntegerToString(v) + ", p=" + bigIntegerToString(p) + ", q=" + bigIntegerToString(q) + "]";
    }

    /**
     * Utility functions - 1.1.3.3 PublicCommitment.
     *
     * @param v The value being committed.
     * @param p The congruence class.
     * @param q The cardinality of the subgroup.
     * @return The string representation of the public commitment.
     */
    public static String publicCommitmentToString(BigInteger v, BigInteger p, BigInteger q) {
        return "PublicCommitment [_commitment=" + zpGroupElementToString(v, p, q) + "]";
    }

    /**
     * Utility functions - 1.1.3.4 SingleValueProductProofInitialMessage
     *
     * @param c_d         The first value being committed.
     * @param c_lowDelta  The second value being committed.
     * @param c_highDelta The third value being committed.
     * @param p           The congruence class.
     * @param q           The cardinality of the subgroup.
     * @return The string representation of the initial message object.
     */
    public static String singleValueProductProofInitialMessageToString(BigInteger c_d, BigInteger c_lowDelta, BigInteger c_highDelta,
                                                                       BigInteger p, BigInteger q) {
        return "SingleValueProductProofInitialMessage [cd=" + publicCommitmentToString(c_d, p, q) +
                ", commitmentPublicLowDelta=" + publicCommitmentToString(c_lowDelta, p, q) +
                ", commitmentPublicHighDelta=" + publicCommitmentToString(c_highDelta, p, q) + "]";
    }

    /**
     * Utility functions - 1.1.3.5 CommitmentParams
     *
     * @param h     An element.
     * @param g_vec A vector of elements.
     * @param p     The congruence class.
     * @param q     The cardinality of the subgroup.
     * @return The string representation of the commitment parameters.
     */
    public static String commitmentParamsToString(BigInteger h, List<BigInteger> g_vec, BigInteger p, BigInteger q) {
        final String vectorString = g_vec.stream().map(g -> zpGroupElementToString(g, p, q)).collect(Collectors.joining(", "));
        return "CommitmentParams [h=" + zpGroupElementToString(h, p, q) + ", g=[" + vectorString + "]]";
    }

    /**
     * Utility functions - 1.1.3.6 ZeroProofInitialMessage
     *
     * @param c_a0    A commitment to a value.
     * @param c_bm    A commitment to a value.
     * @param c_d_vec A vector of commitments.
     * @param p       The congruence class.
     * @param q       The cardinality of the subgroup.
     * @return The string representation of the zero proof initial message object.
     */
    public static String zeroProofInitialMessageToString(BigInteger c_a0, BigInteger c_bm, List<BigInteger> c_d_vec, BigInteger p,
                                                         BigInteger q) {
        final String vectorString = c_d_vec.stream().map(c_d -> publicCommitmentToString(c_d, p, q)).collect(Collectors.joining(", "));
        return "ZeroProofInitialMessage [_cA0=" + publicCommitmentToString(c_a0, p, q) + ", _cBM=" + publicCommitmentToString(c_bm, p, q) + ", _cD=[" + vectorString + "]]";
    }

}
