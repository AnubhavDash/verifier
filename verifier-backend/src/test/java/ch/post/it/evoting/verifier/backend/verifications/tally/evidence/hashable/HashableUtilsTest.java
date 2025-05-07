/*
 * (c) Copyright 2025 Swiss Post Ltd.
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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence.hashable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;

class HashableUtilsTest {

	private static final Random RANDOM = new Random();
	private static final String TOKEN_NAME = "testTokenName";

	@Test
	void fromNullableStringReturnsExpectedHashable() {
		final Hashable hashableNull = assertDoesNotThrow(() -> HashableUtils.fromNullableString(null, TOKEN_NAME));
		assertEquals(HashableUtils.NO_TOKENNAME_VALUE.formatted(TOKEN_NAME), hashableNull.toHashableForm());

		final String value = "exampleValue";
		final Hashable hashable = assertDoesNotThrow(() -> HashableUtils.fromNullableString(value, TOKEN_NAME));
		assertEquals(value, hashable.toHashableForm());
	}

	@Test
	void fromNullableDateReturnsExpectedHashable() throws DatatypeConfigurationException {
		final Hashable hashableNull = assertDoesNotThrow(() -> HashableUtils.fromNullableDate(null, TOKEN_NAME));
		assertEquals(HashableUtils.NO_TOKENNAME_VALUE.formatted(TOKEN_NAME), hashableNull.toHashableForm());

		final GregorianCalendar gregorianCalendar = new GregorianCalendar();
		final XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		final Hashable hashable = assertDoesNotThrow(() -> HashableUtils.fromNullableDate(xmlGregorianCalendar, TOKEN_NAME));
		assertEquals(xmlGregorianCalendar.toXMLFormat(), hashable.toHashableForm());
	}

	@Test
	void fromNullableBooleanReturnsExpectedHashable() {
		final Hashable hashableNull = assertDoesNotThrow(() -> HashableUtils.fromNullableBoolean(null, TOKEN_NAME));
		assertEquals(HashableUtils.NO_TOKENNAME_VALUE.formatted(TOKEN_NAME), hashableNull.toHashableForm());

		final Boolean value = RANDOM.nextBoolean();
		final Hashable hashable = assertDoesNotThrow(() -> HashableUtils.fromNullableBoolean(value, TOKEN_NAME));
		assertEquals(value.toString(), hashable.toHashableForm());
	}

	@Test
	void fromNullableBigIntegerReturnsExpectedHashable() {
		final Hashable hashableNull = assertDoesNotThrow(() -> HashableUtils.fromNullableBigInteger(null, TOKEN_NAME));
		assertEquals(HashableUtils.NO_TOKENNAME_VALUE.formatted(TOKEN_NAME), hashableNull.toHashableForm());

		final BigInteger value = new BigInteger(3072, RANDOM);
		final Hashable hashable = assertDoesNotThrow(() -> HashableUtils.fromNullableBigInteger(value, TOKEN_NAME));
		assertEquals(value, hashable.toHashableForm());
	}

	@Test
	void fromNullableIntegerWithNullValueReturnsExpectedHashable() {
		final Hashable hashableNull = assertDoesNotThrow(() -> HashableUtils.fromNullableInteger(null, TOKEN_NAME));
		assertEquals(HashableUtils.NO_TOKENNAME_VALUE.formatted(TOKEN_NAME), hashableNull.toHashableForm());

		final int value = RANDOM.nextInt(10000);
		final Hashable hashable = assertDoesNotThrow(() -> HashableUtils.fromNullableInteger(value, TOKEN_NAME));
		assertEquals(BigInteger.valueOf(value), hashable.toHashableForm());
	}

	@Test
	void fromNullableLongWithNullValueReturnsExpectedHashable() {
		final Hashable hashableNull = assertDoesNotThrow(() -> HashableUtils.fromNullableLong(null, TOKEN_NAME));
		assertEquals(HashableUtils.NO_TOKENNAME_VALUE.formatted(TOKEN_NAME), hashableNull.toHashableForm());

		final long value = RANDOM.nextLong(100000000L);
		final Hashable hashable = assertDoesNotThrow(() -> HashableUtils.fromNullableLong(value, TOKEN_NAME));
		assertEquals(BigInteger.valueOf(value), hashable.toHashableForm());
	}
}
