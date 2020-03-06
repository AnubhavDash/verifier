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
     * String representation for an exponent,
     *
     * @param v The value of the exponent.
     * @param q The cardinality of the subgroup.
     * @return The string representation of the exponent.
     */
    public static String exponentToString(BigInteger v, BigInteger q) {
        return "Exponent [q=" + bigIntegerToString(q) + ", value=" + bigIntegerToString(v) + "]";
    }

    /**
     * String representation for an element in the <code>Z<sub>p</sub></code> group.
     *
     * @param v The value of the exponent.
     * @param p The congruence class.
     * @param q The cardinality of the subgroup.
     * @return The string representation of the element.
     */
    public static String zpGroupElementToString(BigInteger v, BigInteger p, BigInteger q) {
        return "ZpGroupElement [value=" + bigIntegerToString(v) + ", p=" + bigIntegerToString(p) + ", q=" + bigIntegerToString(q) + "]";
    }

    /**
     * String representation for the public commitment object.
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
     * String representation for the single value proof initial message object.
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
     * String representation for the commitment parameters.
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

}
