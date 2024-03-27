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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.security.SignatureException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.domain.returncodes.ControlComponentCodeSharesPayload;
import ch.post.it.evoting.evotinglibraries.domain.returncodes.SetupComponentVerificationData;
import ch.post.it.evoting.evotinglibraries.domain.returncodes.SetupComponentVerificationDataPayload;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component
public class VerifySignatureVerificationDataAndCodeProofs extends AbstractVerification {

	private final SignatureVerification signatureVerification;
	private final VerifyEncryptedPCCExponentiationProofsAlgorithm verifyEncryptedPCCExponentiationProofsAlgorithm;
	private final VerifyEncryptedCKExponentiationProofsAlgorithm verifyEncryptedCKExponentiationProofsAlgorithm;
	private final ElectionDataExtractionService electionDataExtractionService;

	protected VerifySignatureVerificationDataAndCodeProofs(final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService electionDataExtractionService,
			final SignatureVerification signatureVerification,
			final VerifyEncryptedPCCExponentiationProofsAlgorithm verifyEncryptedPCCExponentiationProofsAlgorithm,
			final VerifyEncryptedCKExponentiationProofsAlgorithm verifyEncryptedCKExponentiationProofsAlgorithm) {
		super(resultPublisherService);
		this.electionDataExtractionService = electionDataExtractionService;
		this.signatureVerification = signatureVerification;
		this.verifyEncryptedPCCExponentiationProofsAlgorithm = verifyEncryptedPCCExponentiationProofsAlgorithm;
		this.verifyEncryptedCKExponentiationProofsAlgorithm = verifyEncryptedCKExponentiationProofsAlgorithm;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.EVIDENCE);
		definition.setDescription(TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
				"setup.verification521.description"));
		definition.setId("05.21");
		definition.setName("VerifySignatureVerificationDataAndCodeProofs");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final List<Path> verificationCardSets = electionDataExtractionService.getSetupVerificationCardSetPaths(inputDirectoryPath);

		final ElectionEventContext electionEventContext = electionDataExtractionService.getElectionEventContext(inputDirectoryPath);

		final ConcurrentMap<String, Integer> numberOfVotingOptionsMap = electionEventContext.verificationCardSetContexts().stream()
				.parallel()
				.collect(Collectors.toConcurrentMap(VerificationCardSetContext::getVerificationCardSetId,
						VerificationCardSetContext::getNumberOfVotingOptions));

		final boolean result = verificationCardSets.stream()
				.map(path -> {
					final int chunkCount = electionDataExtractionService.determineVerificationCardSetChunkCount(path);
					return IntStream.range(0, chunkCount)
							.boxed()
							.flatMap(chunk -> {
								final SetupComponentVerificationDataPayload setupComponentVerificationData = electionDataExtractionService.getSetupComponentVerificationDataPayloadChunk(
										path, chunk);
								final List<ControlComponentCodeSharesPayload> controlComponentCodeShares = electionDataExtractionService.getControlComponentCodeSharesPayloadChunkOrderByNodeId(
										path, chunk);

								final int numberOfVotingOptions = numberOfVotingOptionsMap.get(
										setupComponentVerificationData.getVerificationCardSetId());

								final List<String> verificationCardIds = setupComponentVerificationData.getSetupComponentVerificationData().stream()
										.map(SetupComponentVerificationData::verificationCardId)
										.toList();

								final BooleanSupplier s1 = () -> verifySignatureSetupComponentVerificationData(setupComponentVerificationData);

								final BooleanSupplier s2 = () -> controlComponentCodeShares.stream()
										.parallel()
										.map(this::verifySignatureControlComponentCodeSharesPayload)
										.reduce(Boolean::logicalAnd)
										.orElse(false);

								final BooleanSupplier v1 = verifyEncryptedPCCExponentiationProofs(
										setupComponentVerificationData,
										controlComponentCodeShares,
										verificationCardIds,
										numberOfVotingOptions);

								final BooleanSupplier v2 = verifyEncryptedCKExponentiationProofs(
										setupComponentVerificationData,
										controlComponentCodeShares,
										verificationCardIds,
										numberOfVotingOptions);

								return Stream.of(s1, s2, v1, v2);
							})
							.parallel()
							.map(BooleanSupplier::getAsBoolean)
							.reduce(Boolean::logicalAnd)
							.orElse(false);
				})
				.parallel()
				.reduce(Boolean::logicalAnd)
				.orElse(false);

		if (result) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
							"setup.verification521.nok.message"));
		}
	}

	private BooleanSupplier verifyEncryptedCKExponentiationProofs(final SetupComponentVerificationDataPayload setupComponentVerificationData,
			final List<ControlComponentCodeSharesPayload> controlComponentCodeSharesPayloads, final List<String> verificationCardIds,
			final int numberOfVotingOptions) {

		final VerifyEncryptedExponentiationProofsInput input = new VerifyEncryptedExponentiationProofsInput.Builder()
				.setElectionEventId(setupComponentVerificationData.getElectionEventId())
				.setVerificationCardSetIds(List.of(setupComponentVerificationData.getVerificationCardSetId()))
				.build();

		final List<ContextAndInputForVerificationCardSetAndControlComponent> contextAndInputs = controlComponentCodeSharesPayloads.stream()
				.map(controlComponentCodeSharesPayload -> {

					final VerifyEncryptedExponentiationProofsVerificationCardSetContext verificationCardSetContext = new VerifyEncryptedExponentiationProofsVerificationCardSetContext.Builder()
							.setEncryptionGroup(controlComponentCodeSharesPayload.getEncryptionGroup())
							.setJ(controlComponentCodeSharesPayload.getNodeId())
							.setElectionEventId(controlComponentCodeSharesPayload.getElectionEventId())
							.setVerificationCardIds(verificationCardIds)
							.setNumberOfVotingOptions(numberOfVotingOptions)
							.build();

					final VerifyEncryptedExponentiationProofsVerificationCardSetInput verificationCardSetInput = new VerifyEncryptedExponentiationProofsVerificationCardSetInput(
							setupComponentVerificationData.getSetupComponentVerificationData(),
							controlComponentCodeSharesPayload.getControlComponentCodeShares());
					return new ContextAndInputForVerificationCardSetAndControlComponent(verificationCardSetContext, verificationCardSetInput);
				}).toList();
		return () -> verifyEncryptedCKExponentiationProofsAlgorithm.verifyEncryptedCKExponentiationProofs(input, contextAndInputs);
	}

	private BooleanSupplier verifyEncryptedPCCExponentiationProofs(final SetupComponentVerificationDataPayload setupComponentVerificationData,
			final List<ControlComponentCodeSharesPayload> controlComponentCodeSharesPayloads, final List<String> verificationCardIds,
			final int numberOfVotingOptions) {

		final VerifyEncryptedExponentiationProofsInput input = new VerifyEncryptedExponentiationProofsInput.Builder()
				.setElectionEventId(setupComponentVerificationData.getElectionEventId())
				.setVerificationCardSetIds(List.of(setupComponentVerificationData.getVerificationCardSetId())).build();

		final List<ContextAndInputForVerificationCardSetAndControlComponent> contextAndInputs = controlComponentCodeSharesPayloads.stream()
				.map(controlComponentCodeSharesPayload -> {
							final VerifyEncryptedExponentiationProofsVerificationCardSetContext verificationCardSetContext = new VerifyEncryptedExponentiationProofsVerificationCardSetContext.Builder()
									.setEncryptionGroup(controlComponentCodeSharesPayload.getEncryptionGroup())
									.setJ(controlComponentCodeSharesPayload.getNodeId())
									.setElectionEventId(controlComponentCodeSharesPayload.getElectionEventId())
									.setVerificationCardIds(verificationCardIds)
									.setNumberOfVotingOptions(numberOfVotingOptions)
									.build();

							final VerifyEncryptedExponentiationProofsVerificationCardSetInput verificationCardSetInput = new VerifyEncryptedExponentiationProofsVerificationCardSetInput(
									setupComponentVerificationData.getSetupComponentVerificationData(),
									controlComponentCodeSharesPayload.getControlComponentCodeShares());

							return new ContextAndInputForVerificationCardSetAndControlComponent(verificationCardSetContext, verificationCardSetInput);
						}
				).toList();

		return () -> verifyEncryptedPCCExponentiationProofsAlgorithm.verifyEncryptedPCCExponentiationProofs(input, contextAndInputs);
	}

	@VisibleForTesting
	boolean verifySignatureSetupComponentVerificationData(final SetupComponentVerificationDataPayload setupComponentVerificationDataPayload) {
		final String electionEventId = setupComponentVerificationDataPayload.getElectionEventId();
		final String verificationCardSetId = setupComponentVerificationDataPayload.getVerificationCardSetId();
		final CryptoPrimitivesSignature signature = setupComponentVerificationDataPayload.getSignature();

		checkState(signature != null,
				"The signature of the setup component verification data payload is null. [electionEventId: %s, verificationCardSetId: %s]",
				electionEventId, verificationCardSetId);

		final Hashable additionalContextData = ChannelSecurityContextData.setupComponentVerificationData(electionEventId, verificationCardSetId);

		try {
			return signatureVerification.verifySignature(Alias.SDM_CONFIG.toString(), setupComponentVerificationDataPayload, additionalContextData,
					signature.signatureContents());
		} catch (final SignatureException e) {
			throw new IllegalStateException(
					String.format(
							"Could not verify the signature of the setup component verification data payload. [electionEventId: %s, verificationCardSetId: %s]",
							electionEventId, verificationCardSetId));
		}
	}

	@VisibleForTesting
	boolean verifySignatureControlComponentCodeSharesPayload(final ControlComponentCodeSharesPayload controlComponentCodeSharesPayload) {
		final int nodeId = controlComponentCodeSharesPayload.getNodeId();
		final String electionEventId = controlComponentCodeSharesPayload.getElectionEventId();
		final String verificationCardSetId = controlComponentCodeSharesPayload.getVerificationCardSetId();
		final CryptoPrimitivesSignature signature = controlComponentCodeSharesPayload.getSignature();

		checkState(signature != null,
				"The signature of the control component code shares payload is null. [nodeId: %s, electionEventId: %s, verificationCardSetId: %s]",
				nodeId, electionEventId, verificationCardSetId);

		final Hashable additionalContextData = ChannelSecurityContextData.controlComponentCodeShares(nodeId, electionEventId, verificationCardSetId);

		try {
			return signatureVerification.verifySignature(Alias.getControlComponentByNodeId(nodeId).toString(), controlComponentCodeSharesPayload,
					additionalContextData, signature.signatureContents());
		} catch (final SignatureException e) {
			throw new IllegalStateException(
					String.format(
							"Could not verify the signature of the control component code shares payload. [nodeId: %s, electionEventId: %s, verificationCardSetId: %s]",
							nodeId, electionEventId, verificationCardSetId));
		}
	}
}
