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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTableEntry.VALID_XML_TOKEN_PATTERN;
import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.stereotype.Service;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.post.it.evoting.cryptoprimitives.domain.validations.FailedValidationException;
import ch.post.it.evoting.cryptoprimitives.hashing.Hash;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.verifier.backend.hashable.HashableEch0110Factory;
import ch.post.it.evoting.verifier.backend.hashable.HashableResultsFactory;
import ch.post.it.evoting.verifier.backend.tools.ContestResultsMapper;
import ch.post.it.evoting.verifier.backend.tools.DeliveryMapper;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.Results;

@Service
public class VerifyTallyFilesAlgorithm {

	private final Hash hash;

	public VerifyTallyFilesAlgorithm(final Hash hash) {
		this.hash = hash;
	}

	/**
	 * Verifies the correctness of the evoting-decrypt and eCH-0110 files.
	 *
	 * @param inputDirectoryPath the dataset path.
	 * @param electionEventId    the associated election event id.
	 * @param input              the {@link VerifyTallyFilesInput} containing the configuration, evoting-decrypt and eCH-0110 files.
	 * @return {@code true} if the files are correct, {@code false} otherwise.
	 * @throws NullPointerException      if any input parameter is null.
	 * @throws FailedValidationException if {@code electionEventId} is invalid.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyTallyFiles(final Path inputDirectoryPath, final String electionEventId, final VerifyTallyFilesInput input) {
		checkNotNull(inputDirectoryPath);
		checkNotNull(input);
		validateUUID(electionEventId);

		// Context.
		final String ee = electionEventId;

		// Input.
		final Configuration configurationXML = input.getSetupComponentConfig();
		final Results evotingDecryptXML = input.getTallyComponentDecrypt();
		final Delivery eCH0110XML = input.getTallyComponentEch0110();
		final Map<String, List<List<String>>> L_decodedVotes_bb = input.getAllSelectedDecodedVotingOptions();

		checkArgument(L_decodedVotes_bb.values().stream()
						.flatMap(Collection::stream)
						.flatMap(Collection::stream).allMatch(v -> VALID_XML_TOKEN_PATTERN.matcher(v).matches()),
				"Voting options should match a valid xml xs:token");

		// Operation.
		final Results evotingDecryptXML_prime = ContestResultsMapper.toResults(configurationXML, L_decodedVotes_bb);
		final Delivery eCH0110XML_prime = DeliveryMapper.INSTANCE.map(ee, configurationXML, evotingDecryptXML_prime);

		// Ignore timestamp fields (use original timestamps in re-generated files).
		final XMLGregorianCalendar originalMessageDate = eCH0110XML.getDeliveryHeader().getMessageDate();
		eCH0110XML_prime.getDeliveryHeader().withMessageDate(originalMessageDate);
		final XMLGregorianCalendar originalCreationDateTime = eCH0110XML.getResultDelivery().getReportingBody().getCreationDateTime();
		eCH0110XML_prime.getResultDelivery().getReportingBody().withCreationDateTime(originalCreationDateTime);

		// Compare hash of fields.
		final Hashable hashableEvotingDecryptXML = HashableResultsFactory.fromResults(evotingDecryptXML);
		final Hashable hashableEvotingDecryptXML_prime = HashableResultsFactory.fromResults(evotingDecryptXML_prime);
		final Hashable hashableECH110XML = HashableEch0110Factory.fromDelivery(eCH0110XML);
		final Hashable hashableECH110XML_prime = HashableEch0110Factory.fromDelivery(eCH0110XML_prime);

		return Arrays.equals(hash.recursiveHash(hashableEvotingDecryptXML), hash.recursiveHash(hashableEvotingDecryptXML_prime))
				&& Arrays.equals(hash.recursiveHash(hashableECH110XML), hash.recursiveHash(hashableECH110XML_prime));
	}

}
