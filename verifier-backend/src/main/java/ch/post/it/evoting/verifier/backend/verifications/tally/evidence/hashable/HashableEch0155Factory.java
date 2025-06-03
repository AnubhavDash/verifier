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

import static ch.post.it.evoting.cryptoprimitives.hashing.HashableList.toHashableList;

import ch.ech.xmlns.ech_0155._5.AnswerOptionIdentificationType;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;

interface HashableEch0155Factory {

	static Hashable fromAnswerOptionIdentification(final AnswerOptionIdentificationType answerOptionIdentification) {
		return HashableList.of(
				HashableString.from(answerOptionIdentification.getAnswerIdentification()),
				HashableBigInteger.from(answerOptionIdentification.getAnswerSequenceNumber()),
				answerOptionIdentification.getAnswerTextInformation().stream()
						.map(HashableEch0155Factory::fromAnswerTextInformation)
						.collect(toHashableList())
		);
	}

	private static Hashable fromAnswerTextInformation(final AnswerOptionIdentificationType.AnswerTextInformation answerTextInformation) {
		return HashableList.of(
				HashableString.from(answerTextInformation.getLanguage()),
				HashableUtils.fromNullableString(answerTextInformation.getAnswerTextShort(), "answerTextShort"),
				HashableString.from(answerTextInformation.getAnswerText())
		);
	}

}