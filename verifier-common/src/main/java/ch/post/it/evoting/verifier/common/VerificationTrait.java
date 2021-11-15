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
package ch.post.it.evoting.verifier.common;

import ch.post.it.evoting.verifier.common.event.Block1Event;
import ch.post.it.evoting.verifier.common.event.Block2Event;
import ch.post.it.evoting.verifier.common.event.Block3Event;
import ch.post.it.evoting.verifier.common.event.Block4Event;
import ch.post.it.evoting.verifier.common.event.PreDecryptionEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;

/**
 * Used to group tests for execution
 */
public enum VerificationTrait {

	PRE_DECRYPTION(PreDecryptionEvent.class),
	BLOCK_1(Block1Event.class),
	BLOCK_2(Block2Event.class),
	BLOCK_3(Block3Event.class),
	BLOCK_4(Block4Event.class);

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
