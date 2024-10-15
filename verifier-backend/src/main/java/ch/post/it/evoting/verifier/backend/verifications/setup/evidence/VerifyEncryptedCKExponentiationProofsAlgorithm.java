/*
 * (c) Copyright 2024 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VerifyEncryptedCKExponentiationProofsAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyEncryptedCKExponentiationProofsAlgorithm.class);

	private final VerifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm verifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm;

	public VerifyEncryptedCKExponentiationProofsAlgorithm(
			final VerifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm verifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm) {
		this.verifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm = verifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm;
	}

	/**
	 * Verifies the CK exponentiation proof of a list of verification card set IDs.
	 *
	 * @param input            the input for the VerifyEncryptedCKExponentiationProofs algorithm.
	 * @param contextAndInputs the contexts and inputs for the {@code VerifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm} per control
	 *                         component j and verification card set vcs_i as a {@link Stream}.
	 * @return true if the proofs are valid for all the {@code j} and {@code i}, false otherwise.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyEncryptedCKExponentiationProofs(final VerifyEncryptedExponentiationProofsInput input,
			final List<ContextAndInputForVerificationCardSetAndControlComponent> contextAndInputs) {
		checkNotNull(input);
		checkNotNull(contextAndInputs);

		// Input.
		final String ee = input.getElectionEventId();
		final List<String> vcs = input.getVerificationCardSetIds();

		// Operation.
		final boolean vcsEncryptedCKVerif = contextAndInputs
				.stream()
				// Corresponds to the loop for j in [1, 4] and i in [0, N_bb)
				.map(j_i -> verifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm
						.verifyEncryptedCKExponentiationProofsVerificationCardSet(j_i.context(), j_i.input()))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (vcsEncryptedCKVerif) {
			LOGGER.debug("The encrypted CK exponentiated proofs are valid. [ee: {}, vcs: {}]", ee, vcs);
		} else {
			LOGGER.error("The encrypted CK exponentiated proofs are invalid. [ee: {}, vcs: {}]", ee, vcs);
		}
		return vcsEncryptedCKVerif;
	}
}