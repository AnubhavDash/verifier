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

import ch.ech.xmlns.ech_0010._6.AddressInformationType;
import ch.ech.xmlns.ech_0010._6.CountryType;
import ch.ech.xmlns.ech_0010._6.PersonMailAddressInfoType;
import ch.ech.xmlns.ech_0010._6.PersonMailAddressType;
import ch.ech.xmlns.ech_0010._6.SwissAddressInformationType;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;

interface HashableEch0010Factory {
	static Hashable fromPersonMailAddress(final PersonMailAddressType personMailAddress) {
		return HashableList.of(
				fromPersonMailAddressInfo(personMailAddress.getPerson()),
				fromAddressInformation(personMailAddress.getAddressInformation())
		);
	}

	private static Hashable fromPersonMailAddressInfo(final PersonMailAddressInfoType personMailAddressInfo) {
		return HashableList.of(
				HashableUtils.fromNullableString(personMailAddressInfo.getMrMrs(), "mrMrs"),
				HashableUtils.fromNullableString(personMailAddressInfo.getTitle(), "title"),
				HashableUtils.fromNullableString(personMailAddressInfo.getFirstName(), "firstName"),
				HashableString.from(personMailAddressInfo.getLastName())
		);
	}

	static Hashable fromAddressInformation(final AddressInformationType addressInformation) {
		final boolean isSwissZipCode = addressInformation.getSwissZipCode() != null;
		return HashableList.of(
				HashableUtils.fromNullableString(addressInformation.getAddressLine1(), "addressLine1"),
				HashableUtils.fromNullableString(addressInformation.getAddressLine2(), "addressLine2"),
				HashableUtils.fromNullableString(addressInformation.getStreet(), "street"),
				HashableUtils.fromNullableString(addressInformation.getHouseNumber(), "houseNumber"),
				HashableUtils.fromNullableString(addressInformation.getDwellingNumber(), "dwellingNumber"),
				HashableUtils.fromNullableLong(addressInformation.getPostOfficeBoxNumber(), "postOfficeBoxNumber"),
				HashableUtils.fromNullableString(addressInformation.getPostOfficeBoxText(), "postOfficeBoxText"),
				HashableUtils.fromNullableString(addressInformation.getLocality(), "locality"),
				HashableString.from(addressInformation.getTown()),
				isSwissZipCode ?
						HashableList.of(
								HashableBigInteger.from(BigInteger.valueOf(addressInformation.getSwissZipCode())),
								HashableUtils.fromNullableString(addressInformation.getSwissZipCodeAddOn(), "swissZipCodeAddOn"),
								HashableUtils.fromNullableInteger(addressInformation.getSwissZipCodeId(), "swissZipCodeId")
						) :
						HashableUtils.fromNullableString(addressInformation.getForeignZipCode(), "foreignZipCode"),
				fromCountry(addressInformation.getCountry())
		);
	}

	private static Hashable fromCountry(final CountryType country) {
		return HashableList.of(
				HashableUtils.fromNullableInteger(country.getCountryId(), "countryId"),
				HashableUtils.fromNullableString(country.getCountryIdISO2(), "countryIdISO2"),
				HashableString.from(country.getCountryNameShort())
		);
	}

	static Hashable fromSwissAddressInformation(final SwissAddressInformationType swissAddressInformation) {
		return HashableList.of(
				HashableUtils.fromNullableString(swissAddressInformation.getAddressLine1(), "addressLine1"),
				HashableUtils.fromNullableString(swissAddressInformation.getAddressLine2(), "addressLine2"),
				HashableUtils.fromNullableString(swissAddressInformation.getStreet(), "street"),
				HashableUtils.fromNullableString(swissAddressInformation.getHouseNumber(), "houseNumber"),
				HashableUtils.fromNullableString(swissAddressInformation.getDwellingNumber(), "dwellingNumber"),
				HashableUtils.fromNullableString(swissAddressInformation.getLocality(), "locality"),
				HashableString.from(swissAddressInformation.getTown()),
				HashableBigInteger.from(BigInteger.valueOf(swissAddressInformation.getSwissZipCode())),
				HashableUtils.fromNullableString(swissAddressInformation.getSwissZipCodeAddOn(), "swissZipCodeAddOn"),
				HashableUtils.fromNullableInteger(swissAddressInformation.getSwissZipCodeId(), "swissZipCodeId"),
				fromCountry(swissAddressInformation.getCountry())
		);
	}
}
