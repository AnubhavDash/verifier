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
package ch.post.it.evoting.verifier.backend.hashable;

import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.fromNullableBigInteger;
import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.fromNullableString;

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

	static Hashable fromPersonMailAddressInfo(final PersonMailAddressInfoType personMailAddressInfo) {
		return HashableList.of(
				fromNullableString(personMailAddressInfo.getMrMrs(), "mrMrs"),
				fromNullableString(personMailAddressInfo.getTitle(), "title"),
				fromNullableString(personMailAddressInfo.getFirstName(), "firstName"),
				HashableString.from(personMailAddressInfo.getLastName())
		);
	}

	static Hashable fromAddressInformation(final AddressInformationType addressInformation) {
		final boolean isSwissZipCode = addressInformation.getSwissZipCode() != null;
		return HashableList.of(
				fromNullableString(addressInformation.getAddressLine1(), "addressLine1"),
				fromNullableString(addressInformation.getAddressLine2(), "addressLine2"),
				fromNullableString(addressInformation.getStreet(), "street"),
				fromNullableString(addressInformation.getHouseNumber(), "houseNumber"),
				fromNullableString(addressInformation.getDwellingNumber(), "dwellingNumber"),
				fromNullableBigInteger(BigInteger.valueOf(addressInformation.getPostOfficeBoxNumber()), "postOfficeBoxNumber"),
				fromNullableString(addressInformation.getPostOfficeBoxText(), "postOfficeBoxText"),
				fromNullableString(addressInformation.getLocality(), "locality"),
				HashableString.from(addressInformation.getTown()),
				isSwissZipCode ?
						HashableList.of(
								HashableBigInteger.from(BigInteger.valueOf(addressInformation.getSwissZipCode())),
								fromNullableString(addressInformation.getSwissZipCodeAddOn(), "swissZipCodeAddOn"),
								fromNullableBigInteger(BigInteger.valueOf(addressInformation.getSwissZipCodeId()), "swissZipCodeId")
						) :
						fromNullableString(addressInformation.getForeignZipCode(), "foreignZipCode"),
				fromCountry(addressInformation.getCountry())
		);
	}

	static Hashable fromCountry(final CountryType country) {
		return HashableList.of(
				fromNullableBigInteger(BigInteger.valueOf(country.getCountryId()), "countryId"),
				fromNullableString(country.getCountryIdISO2(), "countryIdISO2"),
				HashableString.from(country.getCountryNameShort())
		);
	}

	static Hashable fromSwissAddressInformation(final SwissAddressInformationType swissAddressInformation) {
		return HashableList.of(
				fromNullableString(swissAddressInformation.getAddressLine1(), "addressLine1"),
				fromNullableString(swissAddressInformation.getAddressLine2(), "addressLine2"),
				fromNullableString(swissAddressInformation.getStreet(), "street"),
				fromNullableString(swissAddressInformation.getHouseNumber(), "houseNumber"),
				fromNullableString(swissAddressInformation.getDwellingNumber(), "dwellingNumber"),
				fromNullableString(swissAddressInformation.getLocality(), "locality"),
				HashableString.from(swissAddressInformation.getTown()),
				HashableBigInteger.from(BigInteger.valueOf(swissAddressInformation.getSwissZipCode())),
				fromNullableString(swissAddressInformation.getSwissZipCodeAddOn(), "swissZipCodeAddOn"),
				fromNullableBigInteger(BigInteger.valueOf(swissAddressInformation.getSwissZipCodeId()), "swissZipCodeId"),
				fromCountry(swissAddressInformation.getCountry())
		);
	}
}
