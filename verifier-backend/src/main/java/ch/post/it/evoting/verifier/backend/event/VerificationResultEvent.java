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
package ch.post.it.evoting.verifier.backend.event;

import java.util.Objects;

import org.springframework.context.ApplicationEvent;

import ch.post.it.evoting.verifier.backend.VerificationResult;

public class VerificationResultEvent extends ApplicationEvent {

	private final VerificationResult verificationResult;

	public VerificationResultEvent(final Object source, final VerificationResult verificationResult) {

		super(source);
		this.verificationResult = verificationResult;
	}

	public VerificationResult getVerificationResult() {
		return verificationResult;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VerificationResultEvent that = (VerificationResultEvent) o;
		return verificationResult.equals(that.verificationResult);
	}

	@Override
	public int hashCode() {
		return Objects.hash(verificationResult);
	}
}
