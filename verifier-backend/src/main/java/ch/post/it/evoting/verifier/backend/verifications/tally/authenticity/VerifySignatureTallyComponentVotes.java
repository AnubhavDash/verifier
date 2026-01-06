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
package ch.post.it.evoting.verifier.backend.verifications.tally.authenticity;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.security.SignatureException;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableByteArray;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
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
		definition.setId("07.04");
		definition.setName("VerifySignatureTallyComponentVotes");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final Stream<TallyComponentVotesPayload> tallyComponentVotesPayloads = electionDataExtractionService.getTallyComponentVotesPayloads(
				inputDirectoryPath);

		final boolean verified = tallyComponentVotesPayloads
				.parallel()
				.map(this::verifySignatureTallyComponentVotes)
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
	boolean verifySignatureTallyComponentVotes(final TallyComponentVotesPayload input) {

		// Input.
		final TallyComponentVotesPayload message = checkNotNull(input);
		final String ee = message.getElectionEventId();
		final String bb = message.getBallotBoxId();
		final ImmutableByteArray s = checkNotNull(input.getSignature()).signatureContents();

		// Operation.
		try {
			return signatureVerification.verifySignature(
					Alias.SDM_TALLY.toString(),
					// The TallyComponentVotesPayload method toHashableForm recursively hashes payload as specified.
					message,
					ChannelSecurityContextData.tallyComponentVotes(ee, bb),
					s);

		} catch (final SignatureException _) {
			throw new IllegalStateException(
					String.format("Could not verify the signature of the  tally component votes payload. [electionEventId: %s, ballotBoxId: %s]",
							ee, bb));
		}
	}
}
