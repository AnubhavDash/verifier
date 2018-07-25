/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.common.block.tools;

import java.math.BigInteger;
import java.util.Base64;

/**
 * Class TypeConverter.
 * This represents TODO.
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class TypeHelper {

    public static BigInteger base64ToBigInteger(String value) {
        return new BigInteger(Base64.getDecoder().decode(value));
    }

    public static boolean isPrime(BigInteger value) {
        //TODO check this or find another method
        return value.isProbablePrime(Integer.MAX_VALUE);
    }

}
