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

import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import ch.post.it.evoting.cryptoprimitives.domain.validations.Validations;

public class VerifyEncryptedExponentiationProofsInput {

	private final String electionEventId;
	private final List<String> verificationCardSetIds;

	private VerifyEncryptedExponentiationProofsInput(final String electionEventId, final List<String> verificationCardSetIds) {
		this.electionEventId = electionEventId;
		this.verificationCardSetIds = verificationCardSetIds;
	}

	public String getElectionEventId() {
		return electionEventId;
	}

	public List<String> getVerificationCardSetIds() {
		return verificationCardSetIds;
	}

	public static class Builder {
		private String electionEventId;
		private List<String> verificationCardSetIds;

		public Builder setElectionEventId(final String electionEventId) {
			this.electionEventId = electionEventId;
			return this;
		}

		public Builder setVerificationCardSetIds(final List<String> verificationCardSetIds) {
			this.verificationCardSetIds = verificationCardSetIds;
			return this;
		}

		public VerifyEncryptedExponentiationProofsInput build() {
			validateUUID(electionEventId);
			checkNotNull(verificationCardSetIds);
			verificationCardSetIds.forEach(Validations::validateUUID);

			return new VerifyEncryptedExponentiationProofsInput(electionEventId, List.copyOf(verificationCardSetIds));
		}
	}
}
