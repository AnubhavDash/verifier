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

import static ch.post.it.evoting.verifier.backend.verifications.tally.evidence.hashable.HashableUtils.fromDate;
import static ch.post.it.evoting.verifier.backend.verifications.tally.evidence.hashable.HashableUtils.fromNullable;
import static ch.post.it.evoting.verifier.backend.verifications.tally.evidence.hashable.HashableUtils.fromNullableBoolean;
import static ch.post.it.evoting.verifier.backend.verifications.tally.evidence.hashable.HashableUtils.fromNullableCollection;
import static ch.post.it.evoting.verifier.backend.verifications.tally.evidence.hashable.HashableUtils.fromNullableDate;
import static ch.post.it.evoting.verifier.backend.verifications.tally.evidence.hashable.HashableUtils.fromNullableString;
import static ch.post.it.evoting.verifier.backend.verifications.tally.evidence.hashable.HashableUtils.stringsToHashableList;

import java.math.BigInteger;

import ch.ech.xmlns.ech_0058._5.HeaderType;
import ch.ech.xmlns.ech_0058._5.NamedMetaDataType;
import ch.ech.xmlns.ech_0058._5.PartialDeliveryType;
import ch.ech.xmlns.ech_0058._5.SendingApplicationType;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;

interface HashableEch0058Factory {
	static Hashable fromHeader(final HeaderType deliveryHeader) {
		return HashableList.of(
				HashableString.from(deliveryHeader.getSenderId()),
				fromNullableString(deliveryHeader.getOriginalSenderId(), "originalSenderId"),
				fromNullableString(deliveryHeader.getDeclarationLocalReference(), "declarationLocalReference"),
				fromNullableCollection(deliveryHeader.getRecipientId(), "recipientId", recipientId -> recipientId.stream().collect(stringsToHashableList())),
				HashableString.from(deliveryHeader.getMessageId()),
				fromNullableString(deliveryHeader.getReferenceMessageId(), "referenceMessageId"),
				fromNullableString(deliveryHeader.getBusinessProcessId(), "businessProcessId"),
				fromNullableString(deliveryHeader.getOurBusinessReferenceId(), "ourBusinessReferenceId"),
				fromNullableString(deliveryHeader.getYourBusinessReferenceId(), "yourBusinessReferenceId"),
				fromNullableString(deliveryHeader.getUniqueIdBusinessTransaction(), "uniqueBusinessTransaction"),
				HashableString.from(deliveryHeader.getMessageType()),
				fromNullableString(deliveryHeader.getSubMessageType(), "subMessageType"),
				fromSendingApplication(deliveryHeader.getSendingApplication()),
				fromNullable(deliveryHeader.getPartialDelivery(), "partialDelivery", HashableEch0058Factory::fromPartialDelivery),
				fromNullableString(deliveryHeader.getSubject(), "subject"),
				fromNullableString(deliveryHeader.getComment(), "comment"),
				fromDate(deliveryHeader.getMessageDate()),
				fromNullableDate(deliveryHeader.getInitialMessageDate(), "initialMessageDate"),
				fromNullableDate(deliveryHeader.getEventDate(), "eventDate"),
				fromNullableDate(deliveryHeader.getModificationDate(), "modificationDate"),
				HashableString.from(deliveryHeader.getAction()),
				// caveat: attachments are not supported in the signature
				HashableString.from(Boolean.toString(deliveryHeader.isTestDeliveryFlag())),
				fromNullableBoolean(deliveryHeader.isResponseExpected(), "responseExpected"),
				fromNullableBoolean(deliveryHeader.isBusinessCaseClosed(), "businessCaseClosed"),
				fromNullableCollection(deliveryHeader.getNamedMetaData(), "namedMetaData",
						namedMetaData -> namedMetaData.stream().map(HashableEch0058Factory::fromNamedMetaData).collect(HashableList.toHashableList()))
				// caveat: extensions are not supported in the signature
		);
	}

	private static Hashable fromSendingApplication(final SendingApplicationType sendingApplication) {
		return HashableList.of(
				HashableString.from(sendingApplication.getManufacturer()),
				HashableString.from(sendingApplication.getProduct()),
				HashableString.from(sendingApplication.getProductVersion())
		);
	}

	private static Hashable fromPartialDelivery(final PartialDeliveryType partialDelivery) {
		return HashableList.of(
				HashableString.from(partialDelivery.getUniqueIdDelivery()),
				HashableBigInteger.from(BigInteger.valueOf(partialDelivery.getTotalNumberOfPackages())),
				HashableBigInteger.from(BigInteger.valueOf(partialDelivery.getNumberOfActualPackage()))
		);
	}

	private static Hashable fromNamedMetaData(final NamedMetaDataType namedMetaData) {
		return HashableList.of(
				HashableString.from(namedMetaData.getMetaDataName()),
				HashableString.from(namedMetaData.getMetaDataValue())
		);
	}
}
