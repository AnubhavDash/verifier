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

import java.util.Map;

public class VerificationResult {
	private VerificationDefinition verificationDefinition;
	private Status status;
	private Map<Language, String> message;
	private VerificationResultSeverity severity;

	public VerificationResult() {
	}

	public VerificationResult(VerificationDefinition definition) {
		this.verificationDefinition = definition;
	}

	public VerificationDefinition getVerificationDefinition() {
		return verificationDefinition;
	}

	public void setVerificationDefinition(VerificationDefinition verificationDefinition) {
		this.verificationDefinition = verificationDefinition;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Map<Language, String> getMessage() {
		return message;
	}

	public void setMessage(Map<Language, String> message) {
		this.message = message;
	}

}
