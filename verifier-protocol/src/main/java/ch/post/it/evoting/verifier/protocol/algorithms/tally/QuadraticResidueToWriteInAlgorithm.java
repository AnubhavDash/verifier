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

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;

import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.ZqElement;
import ch.post.it.evoting.cryptoprimitives.math.ZqGroup;

/**
 * Implements the QuadraticResidueToWriteIn algorithm.
 */
public class QuadraticResidueToWriteInAlgorithm {

	private final IntegerToWriteInAlgorithm integerToWriteInAlgorithm;

	public QuadraticResidueToWriteInAlgorithm(final IntegerToWriteInAlgorithm integerToWriteInAlgorithm) {
		this.integerToWriteInAlgorithm = integerToWriteInAlgorithm;
	}

	/**
	 * Maps a quadratic residue to a write-in string.
	 *
	 * @param quadraticResidue y, the quadratic residue as a {@link GqElement}. Must be non-null.
	 * @return the corresponding write-in string.
	 * @throws NullPointerException if the input is null.
	 */
	public String quadraticResidueToWriteIn(final GqElement quadraticResidue) {
		checkNotNull(quadraticResidue);

		final GqGroup gqGroup = quadraticResidue.getGroup();
		final ZqGroup zqGroup = ZqGroup.sameOrderAs(gqGroup);
		final BigInteger p = gqGroup.getP();
		final BigInteger q = gqGroup.getQ();

		// Input.
		final BigInteger y = quadraticResidue.getValue();

		// Operation.
		BigInteger x = y.modPow(p.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), p);
		if (x.compareTo(q) > 0) {
			x = p.subtract(x);
		}

		return integerToWriteInAlgorithm.integerToWriteIn(ZqElement.create(x, zqGroup));
	}
}
