/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.common.block.exceptions;

import java.util.Map;

import ch.post.it.evoting.verifier.common.Language;

public class VerificationFailureException extends RuntimeException {
	private String[] args;
	private Map<Language, String> failureMessage;

	public VerificationFailureException(String... args) {
		this.args = args;
	}

	public VerificationFailureException(String message, Map<Language, String> failureMessage) {
		super(message);
		this.failureMessage = failureMessage;
	}

	public String[] getArgs() {
		return args;
	}

	public Map<Language, String> getFailureMessage() {
		return failureMessage;
	}
}
