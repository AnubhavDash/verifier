/*
 * Copyright 2021 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.common.block.tools;

import java.math.BigInteger;

import lombok.NonNull;

@SuppressWarnings({ "java:S100", "java:S117" })
public final class MathHelper {

	private MathHelper() {
		//private constructor, use static
	}

	/**
	 * Tests for mathematical equality the two provided {@link BigInteger}s parameters. For testing, the two provided parameters must be non-null,
	 * otherwise a {@link NullPointerException} will be thrown indicating the first parameter found to be null.
	 *
	 * @param valueA {@link NonNull} value to be tested
	 * @param valueB {@link NonNull} value to be tested
	 * @return true if parameter "valueA" is mathematically equal to parameter "valueB", false otherwise
	 */
	public static boolean areEqual(@NonNull BigInteger valueA, @NonNull BigInteger valueB) {
		return valueA.compareTo(valueB) == 0;
	}

	public static boolean isEulerCriterionValid(BigInteger vo, BigInteger p) {
		BigInteger exponent = (p.subtract(BigInteger.ONE)).divide(BigInteger.TWO);
		BigInteger ec = vo.modPow(exponent, p);
		return MathHelper.areEqual(ec, BigInteger.ONE);
	}

	public static boolean isPrime(BigInteger value) {
		return value.isProbablePrime(Integer.MAX_VALUE);
	}

}
