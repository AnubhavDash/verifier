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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence.algorithms;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;

/**
 * Implements the VerifyEncryptionParameters verification algorithm.
 */
@Service
@SuppressWarnings("java:S117")
public class VerifyEncryptionParametersAlgorithm {

	private final ElGamal elGamal;

	public VerifyEncryptionParametersAlgorithm(final ElGamal elGamal) {
		this.elGamal = elGamal;
	}

	/**
	 * Verifies that the given encryption parameters are equal to the ones re-computed using the {@code seed}.
	 *
	 * @param p_hat p&#770;, the p to validate. Must be non-null.
	 * @param q_hat q&#770;, the q to validate. Must be non-null and satisfy p&#770; = 2 * q&#770; + 1.
	 * @param g_hat g&#770;, the g to validate. Must be non-null.
	 * @param seed  the seed used to generate p&#770;, q&#770; and g&#770;.
	 * @return {@code true} if the provided parameters match the re-computed ones, {@code false} otherwise.
	 * @throws NullPointerException     if any parameter is null.
	 * @throws IllegalArgumentException if p&#770; &#8800; 2 * q&#770; + 1.
	 */
	public boolean verifyEncryptionParameters(final BigInteger p_hat, final BigInteger q_hat, final GqElement g_hat, final String seed) {
		checkNotNull(p_hat);
		checkNotNull(q_hat);
		checkNotNull(g_hat);
		checkNotNull(seed);
		checkArgument(p_hat.compareTo(q_hat.shiftLeft(1).add(BigInteger.ONE)) == 0, "p_hat must be equal to 2 * q_hat + 1.");

		// Operation.
		final GqGroup gqGroup = elGamal.getEncryptionParameters(seed);
		final BigInteger p = gqGroup.getP();
		final BigInteger q = gqGroup.getQ();
		final GqElement g = gqGroup.getGenerator();

		return p.equals(p_hat) && q.equals(q_hat) && g.equals(g_hat);
	}

}
