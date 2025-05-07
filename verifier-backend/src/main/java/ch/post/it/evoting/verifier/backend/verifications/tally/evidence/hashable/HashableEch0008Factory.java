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

import static ch.post.it.evoting.verifier.backend.verifications.tally.evidence.hashable.HashableUtils.fromNullableInteger;
import static ch.post.it.evoting.verifier.backend.verifications.tally.evidence.hashable.HashableUtils.fromNullableString;

import ch.ech.xmlns.ech_0008._3.CountryType;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;

interface HashableEch0008Factory {
	static Hashable fromCountry(final CountryType country) {
		return HashableList.of(
				fromNullableInteger(country.getCountryId(), "countryId"),
				fromNullableString(country.getCountryIdISO2(), "countryIdISO2"),
				HashableString.from(country.getCountryNameShort())
		);
	}
}
