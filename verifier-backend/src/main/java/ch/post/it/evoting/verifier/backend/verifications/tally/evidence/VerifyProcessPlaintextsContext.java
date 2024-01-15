/*
 * (c) Copyright 2024 Swiss Post Ltd.
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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;

/**
 * Represents the context values needed by the VerifyProcessPlaintexts algorithm.
 *
 * <ul>
 *     <li>(p, q, g), the encryption group. Not null.</li>
 *     <li>ee, the election event id. Not null and valid UUID.</li>
 *     <li>bb, a ballot box id. Not null and valid UUID.</li>
 *     <li>pTable, the primes mapping table. Not null.</li>
 * </ul>
 */
public record VerifyProcessPlaintextsContext(GqGroup encryptionGroup, String electionEventId, String ballotBoxId,
											 PrimesMappingTable primesMappingTable) {

	public VerifyProcessPlaintextsContext {
		checkNotNull(encryptionGroup);
		validateUUID(electionEventId);
		validateUUID(ballotBoxId);
		checkNotNull(primesMappingTable);

		checkArgument(primesMappingTable.getEncryptionGroup().equals(encryptionGroup),
				"The primes mapping table's entries must belong to the encryption group.");
	}
}
