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
package ch.post.it.evoting.verifier.backend.domain;

import static com.google.common.base.Preconditions.checkNotNull;

public enum VerifierMode {
	SETUP("setup"),
	TALLY("tally");

	private final String mode;

	VerifierMode(final String mode) {
		this.mode = checkNotNull(mode);
	}

	public String getMode() {
		return mode;
	}
}
