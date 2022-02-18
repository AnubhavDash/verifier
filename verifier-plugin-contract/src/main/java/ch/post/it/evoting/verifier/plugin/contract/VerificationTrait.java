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
package ch.post.it.evoting.verifier.plugin.contract;

import ch.post.it.evoting.verifier.plugin.contract.event.ConfigurationEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.FinalDecryptionEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.PreDecryptionEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerifierEvent;

/**
 * Used to group tests for execution
 */
public enum VerificationTrait {

	CONFIGURATION(ConfigurationEvent.class),
	PRE_DECRYPTION(PreDecryptionEvent.class),
	FINAL_DECRYPTION(FinalDecryptionEvent.class);

	private final Class<? extends VerifierEvent> eventClass;

	VerificationTrait(final Class<? extends VerifierEvent> eventClass) {
		this.eventClass = eventClass;
	}

	public Class<? extends VerifierEvent> getEventClass() {
		return eventClass;
	}

	public static VerificationTrait fromValue(String value) throws IllegalArgumentException {
		return VerificationTrait.valueOf(value);
	}
}
