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
import java.util.List;

import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;

public final class Requirements {

	private Requirements() {
	}

	public static void requireIsInZ_q(BigInteger x, EncryptionGroup encryptionGroup) {
		if (!MathHelper.isInZ_q(x, encryptionGroup)) {
			throw new IllegalArgumentException("x is not member of Z_q");
		}
	}

	public static void requireVectorIsInZ_q(List<BigInteger> x_vec, EncryptionGroup encryptionGroup) {
		x_vec.forEach(x -> requireIsInZ_q(x, encryptionGroup));
	}

	public static void requireIsMember(BigInteger x, EncryptionGroup encryptionGroup) {
		if (!MathHelper.isMember(x, encryptionGroup)) {
			throw new IllegalArgumentException("x is not member of the given encryptionGroup");
		}
	}

	public static void requireVectorIsMember(List<BigInteger> x_vec, EncryptionGroup encryptionGroup) {
		x_vec.forEach(x -> requireIsMember(x, encryptionGroup));
	}

	/**
	 * Check that the size of {@code a_vec} is equal to the size of vector {@code b_vec}.
	 *
	 * @param a_vec The first vector.
	 * @param b_vec The second vector.
	 * @throws IllegalArgumentException if {@code a_vec.size() != b_vec.size()}.
	 */
	public static void requireVectorDimensionEqual(List<BigInteger> a_vec, List<BigInteger> b_vec) {
		if (a_vec.size() != b_vec.size()) {
			throw new IllegalArgumentException("The vectors dimensions do not match.");
		}
	}

	/**
	 * Check that the size of vector {@code a_vec} is smaller or equal to the size of vector {@code b_vec}.
	 *
	 * @param a_vec The first vector.
	 * @param b_vec The second vector.
	 * @throws IllegalArgumentException if {@code a_vec.size() > b_vec.size()}.
	 */
	public static void requireVectorDimensionLTE(List<BigInteger> a_vec, List<BigInteger> b_vec) {
		if (a_vec.size() > b_vec.size()) {
			throw new IllegalArgumentException("Dimension of first vector is not smaller or equal to the second's one!");
		}
	}

	/**
	 * Check that the size of vector {@code a_vec} is strictly smaller than the size of vector {@code b_vec}.
	 *
	 * @param a_vec The first vector.
	 * @param b_vec The second vector.
	 * @throws IllegalArgumentException if {@code a_vec.size() >= b_vec.size()}.
	 */
	public static void requireVectorDimensionLT(List<BigInteger> a_vec, List<BigInteger> b_vec) {
		if (a_vec.size() >= b_vec.size()) {
			throw new IllegalArgumentException("Dimension of first vector is not strictly smaller than the second's one!");
		}
	}

	/**
	 * Check that the size of vector {@code a_vec} is greater or equal to the size of vector {@code b_vec}.
	 *
	 * @param a_vec The first vector.
	 * @param b_vec The second vector.
	 * @throws IllegalArgumentException if {@code a_vec.size() < b_vec.size()}.
	 */
	public static void requireVectorDimensionGTE(List<BigInteger> a_vec, List<BigInteger> b_vec) {
		if (a_vec.size() < b_vec.size()) {
			throw new IllegalArgumentException("Dimension of first vector is not greater or equal to the second's one!");
		}
	}

	/**
	 * Check that the size of vector {@code a_vec} is strictly greater than the size of vector {@code b_vec}.
	 *
	 * @param a_vec The first vector.
	 * @param b_vec The second vector.
	 * @throws IllegalArgumentException if {@code a_vec.size() <= b_vec.size()}.
	 */
	public static void requireVectorDimensionGT(List<BigInteger> a_vec, List<BigInteger> b_vec) {
		if (a_vec.size() <= b_vec.size()) {
			throw new IllegalArgumentException("Dimension of first vector is not strictly greater than the second's one!");
		}
	}

}
