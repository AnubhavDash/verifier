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
package ch.post.it.evoting.verifier.backend;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap.toImmutableMap;

import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;

public class VerificationResult {

	private static final String RESOURCE_BUNDLE_NAME = "resources";
	private final VerificationDefinition verificationDefinition;
	private final Status status;
	private final ImmutableMap<Language, String> message;
	private final ImmutableList<String> errorStack;

	private VerificationResult(final VerificationDefinition verificationDefinition, final Status status, final ImmutableMap<Language, String> message,
			final ImmutableList<String> errorStack) {
		this.verificationDefinition = verificationDefinition;
		this.status = status;
		this.message = message;
		this.errorStack = errorStack;
	}

	public static VerificationResult success(final VerificationDefinition verificationDefinition) {
		return new VerificationResult(verificationDefinition, Status.OK, null, null);
	}

	public static VerificationResult failure(final VerificationDefinition verificationDefinition, final ImmutableMap<Language, String> message) {

		return new VerificationResult(verificationDefinition, Status.NOK, message, null);
	}

	public static VerificationResult error(final VerificationDefinition verificationDefinition, final Exception exception) {
		final ImmutableMap<Language, String> message = Arrays.stream(Language.values())
				.collect(toImmutableMap(
						lang -> lang,
						lang -> ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, lang.getLocale()).getString("core.error.unexpected.message")));

		final StackTraceElement[] stackTrace = exception.getStackTrace();
		final ImmutableList<String> errorStack = Stream.concat(
						Stream.of(exception.toString()),
						Arrays.stream(stackTrace).map(StackTraceElement::toString))
				.collect(toImmutableList());
		return new VerificationResult(verificationDefinition, Status.UNEXPECTED_ERROR, message, errorStack);
	}

	public VerificationDefinition getVerificationDefinition() {
		return verificationDefinition;
	}

	public Status getStatus() {
		return status;
	}

	public ImmutableMap<Language, String> getMessage() {
		return message;
	}

	public ImmutableList<String> getErrorStack() {
		return errorStack;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final VerificationResult that = (VerificationResult) o;
		return verificationDefinition.equals(that.verificationDefinition) && status == that.status && Objects.equals(message, that.message)
				&& Objects.equals(errorStack, that.errorStack);
	}

	@Override
	public int hashCode() {
		return Objects.hash(verificationDefinition, status, message, errorStack);
	}

}
