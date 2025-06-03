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

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.xml.datatype.XMLGregorianCalendar;

import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;

public interface HashableUtils {

	String NO_TOKENNAME_VALUE = "no %s value";

	static Collector<String, ?, HashableList> stringsToHashableList() {
		return Collectors.mapping(HashableString::from, HashableList.toHashableList());
	}

	static Hashable fromDate(final XMLGregorianCalendar calendar) {
		return HashableString.from(calendar.toXMLFormat());
	}

	static Hashable fromNullableString(final String value, final String tokenName) {
		return fromNullable(value, tokenName, HashableString::from);
	}

	static Hashable fromNullableDate(final XMLGregorianCalendar value, final String tokenName) {
		return fromNullable(value, tokenName, HashableUtils::fromDate);
	}

	static Hashable fromNullableBoolean(final Boolean value, final String tokenName) {
		return fromNullable(value, tokenName, v -> HashableString.from(v.toString()));
	}

	static Hashable fromNullableBigInteger(final BigInteger value, final String tokenName) {
		return fromNullable(value, tokenName, HashableBigInteger::from);
	}

	static Hashable fromNullableInteger(final Integer value, final String tokenName) {
		return fromNullable(value, tokenName, HashableBigInteger::from);
	}

	static Hashable fromNullableLong(final Long value, final String tokenName) {
		return fromNullable(value, tokenName, v -> HashableBigInteger.from(BigInteger.valueOf(v)));
	}

	static <T> Hashable fromNullable(final T value, final String tokenName, final Function<T, Hashable> function) {
		return value != null ? function.apply(value) : HashableString.from(NO_TOKENNAME_VALUE.formatted(tokenName));
	}

	static <T extends Collection> Hashable fromNullableCollection(final T value, final String tokenName, final Function<T, Hashable> function) {
		return value != null && !value.isEmpty() ? function.apply(value) : HashableString.from(NO_TOKENNAME_VALUE.formatted(tokenName));
	}
}
