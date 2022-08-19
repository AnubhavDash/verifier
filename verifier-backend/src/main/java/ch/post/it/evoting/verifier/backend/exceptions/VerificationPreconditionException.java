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
package ch.post.it.evoting.verifier.backend.exceptions;

/**
 * Exception to be used by the verifications to indicate that their input data are incomplete, malformed or impossible to deserialize.
 */
public class VerificationPreconditionException extends RuntimeException {

	public VerificationPreconditionException(final String message) {
		super(message);
	}

	public VerificationPreconditionException(final String message, final Throwable throwable) {
		super(message, throwable);
	}

}
