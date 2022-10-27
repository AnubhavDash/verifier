/*
 * Copyright 2022 Post CH Ltd
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
package ch.post.it.evoting.verifier.protocol.algorithms.tally;

import static ch.post.it.evoting.verifier.protocol.domain.WriteInAlphabet.WRITE_IN_ALPHABET;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.LinkedList;

import ch.post.it.evoting.cryptoprimitives.math.ZqElement;

/**
 * Implements the IntegerToWriteIn algorithm.
 */
public class IntegerToWriteInAlgorithm {

	/**
	 * Maps a {@link ZqElement} to a write-in string.
	 *
	 * @param encoding x, the encoding as a {@link ZqElement}. Must be non-null and different from zero.
	 * @return the write-in string.
	 * @throws NullPointerException     if any parameter is null.
	 * @throws IllegalArgumentException if the encoding's value is zero.
	 */
	@SuppressWarnings("java:S117")
	public String integerToWriteIn(final ZqElement encoding) {

		// Input.
		BigInteger x = checkNotNull(encoding).getValue();
		checkArgument(!x.equals(BigInteger.ZERO), "The encoding x value must not be zero.");

		final BigInteger a = BigInteger.valueOf(WRITE_IN_ALPHABET.size());

		// Operation.
		final LinkedList<String> s = new LinkedList<>();
		while (x.compareTo(BigInteger.ZERO) > 0) {
			final BigInteger b = x.mod(a);
			final String c = WRITE_IN_ALPHABET.get(b.intValueExact());
			s.add(0, c);
			x = x.subtract(b).divide(a);
		}

		return String.join("", s);
	}
}