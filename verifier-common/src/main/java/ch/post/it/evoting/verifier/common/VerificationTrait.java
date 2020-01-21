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
package ch.post.it.evoting.verifier.common;

/**
 * Used to group tests for execution
 */
public enum VerificationTrait {

    PRE_DECRYPTION,
    BLOCK_1,
    BLOCK_2,
    BLOCK_3,
    BLOCK_4;

    public static VerificationTrait fromValue(String value) throws IllegalArgumentException  {
        return VerificationTrait.valueOf(value);
    }
}
