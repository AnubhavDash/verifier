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
package ch.post.it.evoting.verifier.backend.verifications.tally.authenticity;

import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.security.SignatureException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.domain.signature.Alias;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.hashable.HashableContestResultsFactory;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.ChannelSecurityContextData;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.Results;

@Component
public class CheckSignatureTallyComponentDecrypt extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;
	private final SignatureVerification signatureVerification;

	protected CheckSignatureTallyComponentDecrypt(
			final ApplicationEventPublisher applicationEventPublisher,
			final ElectionDataExtractionService electionDataExtractionService,
			final SignatureVerification signatureVerification) {
		super(applicationEventPublisher);
		this.electionDataExtractionService = electionDataExtractionService;
		this.signatureVerification = signatureVerification;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.AUTHENTICITY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification.direct.trust.authenticity.description", "tallyComponentDecrypt"));
		definition.setId(204);
		definition.setName("CheckSignatureTallyComponentDecrypt");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final Results results = electionDataExtractionService.getTallyComponentDecrypt(inputDirectoryPath);

		final boolean verified = verifySignature(results);

		if (verified) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification.direct.trust.signature.fail", "tallyComponentDecrypt"));
		}

	}

	@VisibleForTesting
	boolean verifySignature(final Results results) {
		final byte[] signature = results.getSignature();

		checkState(signature != null, "The signature of the tally component decrypt file is null.");

		final Hashable hash = HashableContestResultsFactory.fromResults(results);
		final Hashable additionalContextData = ChannelSecurityContextData.tallyComponentDecrypt();

		try {
			return signatureVerification.verifySignature(Alias.SDM_TALLY.toString(), hash, additionalContextData, signature);
		} catch (final SignatureException e) {
			throw new IllegalStateException("Could not verify the signature of the tally component decrypt file.");
		}
	}
}

