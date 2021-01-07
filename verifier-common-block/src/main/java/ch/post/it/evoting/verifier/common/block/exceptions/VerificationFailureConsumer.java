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

import java.util.function.Consumer;

/**
 * Consumer that allows to work with streams without having to to introduce try-catch blocks for checked exceptions. This consumer wraps every
 * exception in a {@link VerificationFailureWrappedException} except the {@link VerificationFailureException}.
 *
 * @param <T> The type of the current variable of the stream.
 */
@FunctionalInterface
public interface VerificationFailureConsumer<T> extends Consumer<T> {

	void acceptThrows(T t) throws Exception;

	@Override
	default void accept(final T t) {
		try {
			acceptThrows(t);
		} catch (final Exception e) {
			try {
				throw (VerificationFailureException) e;
			} catch (ClassCastException cce) {
				throw new VerificationFailureWrappedException(e);
			}
		}
	}
}
