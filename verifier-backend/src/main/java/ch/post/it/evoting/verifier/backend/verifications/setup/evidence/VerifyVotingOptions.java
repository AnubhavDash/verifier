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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTable;
import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTableEntry;
import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.EncryptionParametersPayload;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupElement;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component
public class VerifyVotingOptions extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;
	private final VerifyVotingOptionsAlgorithm verifyVotingOptionsAlgorithm;

	public VerifyVotingOptions(
			final ElectionDataExtractionService extractionService,
			final ApplicationEventPublisher applicationEventPublisher,
			final VerifyVotingOptionsAlgorithm verifyVotingOptionsAlgorithm) {
		super(applicationEventPublisher);
		this.extractionService = extractionService;
		this.verifyVotingOptionsAlgorithm = verifyVotingOptionsAlgorithm;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.EVIDENCE);
		definition.setDescription(TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
				"setup.verification502.description"));
		definition.setId(502);
		definition.setName("VerifyVotingOptions");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		// Get the encryption parameters.
		final EncryptionParametersPayload encryptionParametersPayload = extractionService.getEncryptionParametersPayload(inputDirectoryPath);

		// Get the primes from the file.
		final GroupVector<PrimeGqElement, GqGroup> smallPrimeGroupMembers = encryptionParametersPayload.getSmallPrimes();

		final GroupVector<PrimeGqElement, GqGroup> encodedVotingOptions = extractEncodedVotingOptions(inputDirectoryPath);

		if (verifyVotingOptionsAlgorithm.verifyVotingOptions(smallPrimeGroupMembers, encodedVotingOptions)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification502.nok.message"));
		}
	}

	/**
	 * The consistency checks of the primes mapping tables are done in another verification.
	 */
	private GroupVector<PrimeGqElement, GqGroup> extractEncodedVotingOptions(final Path inputDirectoryPath) {
		final ElectionEventContext electionEventContext = extractionService.getElectionEventContext(inputDirectoryPath);

		return electionEventContext.verificationCardSetContexts().stream()
				.map(VerificationCardSetContext::primesMappingTable)
				.map(PrimesMappingTable::getPTable)
				.flatMap(Collection::stream)
				.map(PrimesMappingTableEntry::encodedVotingOption)
				.distinct()
				.sorted(Comparator.comparing(GroupElement::getValue))
				.collect(GroupVector.toGroupVector());
	}

}

