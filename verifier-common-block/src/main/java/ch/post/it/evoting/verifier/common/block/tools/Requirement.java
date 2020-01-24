package ch.post.it.evoting.verifier.common.block.tools;

import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;

import java.math.BigInteger;
import java.util.List;

public final class Requirement {

    private Requirement() {
    }

    public static void requireIsInZ_q(BigInteger x, EncryptionGroup encryptionGroup){
        if (!MathHelper.isInZ_q(x, encryptionGroup)) {
            throw new IllegalArgumentException("x is not member of Z_q");
        }
    }

    public static void requireVectorIsInZ_q(List<BigInteger> x_vec, EncryptionGroup encryptionGroup){
        x_vec.forEach(x -> requireIsInZ_q(x, encryptionGroup));
    }


    public static void requireIsMember(BigInteger x, EncryptionGroup encryptionGroup){
        if (!MathHelper.isMember(x, encryptionGroup)) {
            throw new IllegalArgumentException("x is not member of the given encryptionGroup");
        }
    }

    public static void requireVectorIsMember(List<BigInteger> x_vec, EncryptionGroup encryptionGroup){
        x_vec.forEach(x -> requireIsMember(x, encryptionGroup));
    }

}
