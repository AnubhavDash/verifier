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
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.domain.signature.Alias;
import ch.post.it.evoting.cryptoprimitives.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.ChannelSecurityContextData;

@Component
public class VerifySignatureTallyComponentVotes extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;

	private final SignatureVerification signatureVerification;

	protected VerifySignatureTallyComponentVotes(
			final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService electionDataExtractionService,
			final SignatureVerification signatureVerification) {
		super(resultPublisherService);
		this.electionDataExtractionService = electionDataExtractionService;
		this.signatureVerification = signatureVerification;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.AUTHENTICITY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification.direct.trust.authenticity.description", "TallyComponentVotes"));
		definition.setId(203);
		definition.setName("VerifySignatureTallyComponentVotes");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final Stream<TallyComponentVotesPayload> tallyComponentVotesPayloads = electionDataExtractionService.getTallyComponentVotesPayloads(inputDirectoryPath);

		final boolean verified = tallyComponentVotesPayloads
				.parallel()
				.map(this::verifySignature)
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (verified) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification.direct.trust.signature.fail", "TallyComponentShuffle"));
		}

	}

	@VisibleForTesting
	boolean verifySignature(final TallyComponentVotesPayload tallyComponentVotesPayload) {
		final String electionEventId = tallyComponentVotesPayload.getElectionEventId();
		final String ballotBoxId = tallyComponentVotesPayload.getBallotBoxId();
		final CryptoPrimitivesSignature signature = tallyComponentVotesPayload.getSignature();

		checkState(signature != null, "The signature of the tally component votes payload is null. [electionEventId: %s, ballotBoxId: %s]",
				electionEventId, ballotBoxId);

		final Hashable additionalContextData = ChannelSecurityContextData.tallyComponentVotes(electionEventId, ballotBoxId);

		try {
			return signatureVerification.verifySignature(Alias.SDM_TALLY.toString(), tallyComponentVotesPayload,
					additionalContextData, signature.signatureContents());
		} catch (final SignatureException e) {
			throw new IllegalStateException(
					String.format("Could not verify the signature of the  tally component votes payload. [electionEventId: %s, ballotBoxId: %s]",
							electionEventId, ballotBoxId));
		}
	}
}
