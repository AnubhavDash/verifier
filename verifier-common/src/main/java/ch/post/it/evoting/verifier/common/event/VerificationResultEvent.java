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
package ch.post.it.evoting.verifier.common.event;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import org.springframework.context.ApplicationEvent;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;

public class VerificationResultEvent extends ApplicationEvent {

	private static final String RESOURCE_BUNDLE_NAME = "core/resources";

	private final Status status;
	private final VerificationDefinition verificationDefinition;
	private final Map<Language, String> message;

	private VerificationResultEvent(final Object source, final Status status, final VerificationDefinition verificationDefinition,
			final Map<Language, String> message) {

		super(source);
		this.status = status;
		this.verificationDefinition = verificationDefinition;
		this.message = message;
	}

	public static VerificationResultEvent success(final Object source, final VerificationDefinition verificationDefinition) {
		return new VerificationResultEvent(source, Status.OK, verificationDefinition, null);
	}

	public static VerificationResultEvent failure(final Object source, final VerificationDefinition verificationDefinition,
			final Map<Language, String> message) {

		return new VerificationResultEvent(source, Status.NOK, verificationDefinition, message);
	}

	public static VerificationResultEvent error(final Object source, final VerificationDefinition verificationDefinition) {
		final Map<Language, String> message = new EnumMap<>(Language.class);
		Arrays.stream(Language.values()).forEach(lang -> message.put(lang,
				ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, lang.getLocale()).getString("core.error.unexpected.message")));

		return new VerificationResultEvent(source, Status.UNEXPECTED_ERROR, verificationDefinition, message);
	}

	public VerificationDefinition getVerificationDefinition() {
		return verificationDefinition;
	}

	public Status getStatus() {
		return status;
	}

	public Map<Language, String> getMessage() {
		return message;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final VerificationResultEvent that = (VerificationResultEvent) o;
		return Objects.equals(verificationDefinition, that.verificationDefinition) && status == that.status && Objects.equals(message,
				that.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(verificationDefinition, status, message);
	}
}
