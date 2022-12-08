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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VerifyEncryptedPCCExponentiationProofsAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyEncryptedPCCExponentiationProofsAlgorithm.class);

	private final VerifyEncryptedPCCExponentiationProofsVerificationCardSetAlgorithm verifyEncryptedPCCExponentiationProofsVerificationCardSetAlgorithm;

	public VerifyEncryptedPCCExponentiationProofsAlgorithm(
			final VerifyEncryptedPCCExponentiationProofsVerificationCardSetAlgorithm verifyEncryptedPCCExponentiationProofsVerificationCardSetAlgorithm) {
		this.verifyEncryptedPCCExponentiationProofsVerificationCardSetAlgorithm = verifyEncryptedPCCExponentiationProofsVerificationCardSetAlgorithm;
	}

	/**
	 * Verifies the PCC exponentiation proof of a list of verification card set IDs.
	 *
	 * @param input            the input for the VerifyEncryptedPCCExponentiationProofs algorithm.
	 * @param contextAndInputs the contexts and inputs for the {@code VerifyEncryptedPCCExponentiationProofsVerificationCardSetAlgorithm} per control
	 *                         component j and verification card set vcs_i as a {@link Stream}.
	 * @return true if the proofs are valid for all the {@code j} and {@code i}, false otherwise.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyEncryptedPCCExponentiationProofs(final VerifyEncryptedExponentiationProofsInput input,
			final Stream<ExponentiationProofsVerificationExtractionService.ContextAndInputForVerificationCardSetAndControlComponent> contextAndInputs) {
		checkNotNull(input);
		checkNotNull(contextAndInputs);

		// Input.
		final String ee = input.getElectionEventId();
		final List<String> vcs = input.getVerificationCardSetIds();

		// Operation.
		final boolean vcsEncryptedPCCVerif = contextAndInputs
				.parallel()
				// Corresponds to the loop for j in [1, 4] and i in [0, N_bb)
				.map(j_i -> verifyEncryptedPCCExponentiationProofsVerificationCardSetAlgorithm
						.verifyEncryptedPCCExponentiationProofsVerificationCardSet(j_i.context(), j_i.input()))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (vcsEncryptedPCCVerif) {
			LOGGER.debug("The encrypted PCC exponentiated proofs are valid. [ee: {}, vcs: {}]", ee, vcs);
		} else {
			LOGGER.error("The encrypted PCC exponentiated proofs are invalid. [ee: {}, vcs: {}]", ee, vcs);
		}
		return vcsEncryptedPCCVerif;
	}
}
