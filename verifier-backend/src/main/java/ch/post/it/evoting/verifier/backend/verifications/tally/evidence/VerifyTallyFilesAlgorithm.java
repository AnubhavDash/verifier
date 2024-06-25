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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Objects;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.stereotype.Service;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.post.it.evoting.cryptoprimitives.hashing.Hash;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.evotinglibraries.domain.validations.FailedValidationException;
import ch.post.it.evoting.evotinglibraries.xml.XmlNormalizer;
import ch.post.it.evoting.evotinglibraries.xml.hashable.HashableEch0110Factory;
import ch.post.it.evoting.evotinglibraries.xml.hashable.HashableEch0222Factory;
import ch.post.it.evoting.evotinglibraries.xml.hashable.HashableResultsFactory;
import ch.post.it.evoting.evotinglibraries.xml.mapper.DeliveryMapper;
import ch.post.it.evoting.evotinglibraries.xml.mapper.RawDataDeliveryMapper;
import ch.post.it.evoting.evotinglibraries.xml.mapper.ResultDeliveryMapper;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingdecrypt.Results;

@Service
public class VerifyTallyFilesAlgorithm {

	private final Hash hash;
	private final XmlNormalizer xmlNormalizer;

	public VerifyTallyFilesAlgorithm(final Hash hash, final XmlNormalizer xmlNormalizer) {
		this.hash = hash;
		this.xmlNormalizer = xmlNormalizer;
	}

	/**
	 * Verifies the correctness of the evoting-decrypt, eCH-0110 and eCH-0222 files.
	 *
	 * @param electionEventId the associated election event id. Must be a valid UUID.
	 * @param input           the {@link VerifyTallyFilesInput} containing the configuration, evoting-decrypt, eCH-0110 and eCH-0222 files.
	 * @return {@code true} if the files are correct, {@code false} otherwise.
	 * @throws NullPointerException      if any input parameter is null.
	 * @throws FailedValidationException if {@code electionEventId} is invalid.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyTallyFiles(final String electionEventId, final VerifyTallyFilesInput input) {
		checkNotNull(input);
		validateUUID(electionEventId);

		// Context.
		final String ee = electionEventId;

		// Input.
		final Configuration configurationXML = input.getCantonConfig();
		final Results evotingDecryptXML = input.getTallyComponentDecrypt();
		final Delivery eCH0110XML = input.getTallyComponentEch0110();
		final ch.ech.xmlns.ech_0222._1.Delivery eCH0222XML = input.getTallyComponentEch0222();
		final Map<String, TallyComponentVotesPayload> L_decodedVotesbb = input.getTallyComponentVotesPayloads();

		// Operation.
		final Results evotingDecryptXML_prime = ResultDeliveryMapper.toResults(configurationXML, L_decodedVotesbb);

		final Delivery eCH0110XML_prime = DeliveryMapper.INSTANCE.map(ee, configurationXML, evotingDecryptXML,
				configurationXML.getAuthorizations().getAuthorization());
		final Delivery eCH0110XML_prime_normalized = xmlNormalizer.normalizeWriteInsEch0110(eCH0110XML_prime);

		final ch.ech.xmlns.ech_0222._1.Delivery eCH0222XML_prime = RawDataDeliveryMapper.createECH0222(ee, configurationXML, evotingDecryptXML);
		final ch.ech.xmlns.ech_0222._1.Delivery eCH0222XML_prime_normalized = xmlNormalizer.normalizeWriteInsEch0220(eCH0222XML_prime);

		// Ignore timestamp fields in eCH0110 and eCH0222 (use original timestamps in re-generated files).
		final XMLGregorianCalendar eCH0110OriginalMessageDate = eCH0110XML.getDeliveryHeader().getMessageDate();
		eCH0110XML_prime_normalized.getDeliveryHeader().withMessageDate(eCH0110OriginalMessageDate);
		final XMLGregorianCalendar eCH0110OriginalCreationDateTime = eCH0110XML.getResultDelivery().getReportingBody().getCreationDateTime();
		eCH0110XML_prime_normalized.getResultDelivery().getReportingBody().withCreationDateTime(eCH0110OriginalCreationDateTime);
		final XMLGregorianCalendar eCH0222OriginalMessageDate = eCH0222XML.getDeliveryHeader().getMessageDate();
		eCH0222XML_prime_normalized.getDeliveryHeader().withMessageDate(eCH0222OriginalMessageDate);
		final XMLGregorianCalendar eCH0222OriginalCreationDateTime = eCH0222XML.getRawDataDelivery().getReportingBody().getCreationDateTime();
		eCH0222XML_prime_normalized.getRawDataDelivery().getReportingBody().withCreationDateTime(eCH0222OriginalCreationDateTime);

		// Compare hash of fields.
		final Hashable hashableEvotingDecryptXML = HashableResultsFactory.fromResults(evotingDecryptXML);
		final Hashable hashableEvotingDecryptXML_prime = HashableResultsFactory.fromResults(evotingDecryptXML_prime);
		final Hashable hashableECH0110XML = HashableEch0110Factory.fromDelivery(eCH0110XML);
		final Hashable hashableECH0110XML_prime = HashableEch0110Factory.fromDelivery(eCH0110XML_prime_normalized);
		final Hashable hashableECH0222XML = HashableEch0222Factory.fromDelivery(eCH0222XML);
		final Hashable hashableECH0222XML_prime = HashableEch0222Factory.fromDelivery(eCH0222XML_prime_normalized);

		return Objects.equals(hash.recursiveHash(hashableEvotingDecryptXML), hash.recursiveHash(hashableEvotingDecryptXML_prime))
				&& Objects.equals(hash.recursiveHash(hashableECH0110XML), hash.recursiveHash(hashableECH0110XML_prime))
				&& Objects.equals(hash.recursiveHash(hashableECH0222XML), hash.recursiveHash(hashableECH0222XML_prime));
	}

}
