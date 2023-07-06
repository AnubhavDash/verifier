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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;

public record VerifyOnlineControlComponentBallotBoxInput(Map<String, ElGamalMultiRecipientPublicKey> verificationCardPublicKeyMap,
														 ControlComponentBallotBoxPayload firstControlComponentBallotBox,
														 List<ControlComponentShufflePayload> controlComponentShuffles) {

	public VerifyOnlineControlComponentBallotBoxInput(final Map<String, ElGamalMultiRecipientPublicKey> verificationCardPublicKeyMap,
			final ControlComponentBallotBoxPayload firstControlComponentBallotBox,
			final List<ControlComponentShufflePayload> controlComponentShuffles) {
		this.verificationCardPublicKeyMap = Collections.unmodifiableMap(new LinkedHashMap<>(checkNotNull(verificationCardPublicKeyMap)));
		this.firstControlComponentBallotBox = checkNotNull(firstControlComponentBallotBox);
		this.controlComponentShuffles = List.copyOf(checkNotNull(controlComponentShuffles));
	}
}
