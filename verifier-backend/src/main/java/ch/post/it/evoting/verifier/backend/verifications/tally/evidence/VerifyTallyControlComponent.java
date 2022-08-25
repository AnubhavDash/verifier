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

import static ch.post.it.evoting.verifier.backend.tools.TranslationHelper.getFromResourceBundle;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.cryptoprimitives.domain.ControlComponentConstants;
import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.configuration.PrimesMappingTableEntry;
import ch.post.it.evoting.verifier.protocol.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.verifier.protocol.domain.tally.TallyComponentVotesPayload;

@Component
public class VerifyTallyControlComponent extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;
	private final VerifyTallyControlComponentBallotBoxAlgorithm verifyTallyControlComponentBallotBoxAlgorithm;

	protected VerifyTallyControlComponent(final ElectionDataExtractionService extractionService,
			final VerifyTallyControlComponentBallotBoxAlgorithm verifyTallyControlComponentBallotBoxAlgorithm,
			final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.extractionService = extractionService;
		this.verifyTallyControlComponentBallotBoxAlgorithm = verifyTallyControlComponentBallotBoxAlgorithm;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setId(501);
		definition.setCategory(Category.EVIDENCE);
		definition
				.setDescription(getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification501.description"));
		definition.setName("VerifyTallyControlComponent");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) {
		ElectionEventContextPayload electionEventContextPayload = extractionService.getElectionEventContextPayload(inputDirectoryPath);
		final ElGamalMultiRecipientPublicKey electoralBoardPublicKey = electionEventContextPayload.getElectionEventContext()
				.electoralBoardPublicKey();
		final String ee = electionEventContextPayload.getElectionEventContext().electionEventId();
		final List<String> bb = electionEventContextPayload.getElectionEventContext().verificationCardSetContexts().stream()
				.map(VerificationCardSetContext::ballotBoxId)
				.sorted(String::compareTo)
				.toList();

		final List<TallyComponentShufflePayload> tallyComponentShufflePayloads = extractionService.getTallyComponentShufflePayloads(
						inputDirectoryPath).stream()
				.sorted(Comparator.comparing(TallyComponentShufflePayload::getBallotBoxId))
				.toList();
		final List<TallyComponentVotesPayload> tallyComponentVotesPayloads = extractionService.getTallyComponentVotesPayloads(inputDirectoryPath)
				.stream()
				.sorted(Comparator.comparing(TallyComponentVotesPayload::getBallotBoxId))
				.toList();

		final GqGroup encryptionGroup = electionEventContextPayload.getEncryptionGroup();
		final List<VerificationCardSetContext> verificationCardSetContexts = electionEventContextPayload.getElectionEventContext()
				.verificationCardSetContexts();
		final int N_bb = bb.size();
		final List<Boolean> tallyVerif = IntStream.range(0, N_bb)
				.mapToObj(i -> {
					final String bb_i = bb.get(i);
					final VerificationCardSetContext verificationCardSetContext = verificationCardSetContexts.stream()
							.filter(vcsContext -> vcsContext.ballotBoxId().equals(bb_i))
							.findAny().orElseThrow(() -> new IllegalStateException("VerificationCardSetContext not found."));
					final TallyComponentShufflePayload tallyComponentShufflePayload = tallyComponentShufflePayloads.get(i);
					final ControlComponentShufflePayload lastControlComponentShufflePayload = extractionService.getControlComponentShufflePayloads(
							inputDirectoryPath, bb_i).get(ControlComponentConstants.NODE_IDS.last() - 1);

					final String vcs = verificationCardSetContext.verificationCardSetId();
					final Integer numberOfSelections = extractionService.getCombinedCorrectnessInformation(inputDirectoryPath, vcs)
							.getTotalNumberOfSelections();

					final SetupComponentTallyDataPayload setupComponentTallyDataPayload = extractionService.getSetupComponentTallyDataPayload(
							inputDirectoryPath, vcs);
					final GroupVector<PrimesMappingTableEntry, GqGroup> pTable = setupComponentTallyDataPayload.getPrimesMappingTable().getPTable();
					@SuppressWarnings("java:S117")
					final GroupVector<PrimeGqElement, GqGroup> p_tilde = pTable.stream().map(PrimesMappingTableEntry::encodedVotingOption)
							.collect(GroupVector.toGroupVector());
					final TallyComponentVotesPayload tallyComponentVotesPayload = tallyComponentVotesPayloads.get(i);
					@SuppressWarnings("java:S117")
					final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> L_votes = tallyComponentVotesPayload.getVotes().stream()
							.map(list -> list.stream()
									.map(p -> PrimeGqElement.PrimeGqElementFactory.fromValue(p.getValueAsInt(), encryptionGroup))
									.collect(GroupVector.toGroupVector()))
							.collect(GroupVector.toGroupVector());

					final int numberOfAllowedWriteIns = verificationCardSetContexts.stream()
							.filter(vcsContext -> vcsContext.ballotBoxId().equals(bb_i))
							.collect(MoreCollectors.onlyElement())
							.numberOfWriteInFields();

					final VerifyTallyControlComponentBallotBoxContext context = new VerifyTallyControlComponentBallotBoxContext(encryptionGroup, ee,
							bb_i, electoralBoardPublicKey, p_tilde, numberOfSelections, numberOfAllowedWriteIns + 1);

					final VerifyTallyControlComponentBallotBoxInput input = new VerifyTallyControlComponentBallotBoxInput.Builder()
							.setPreviousPartiallyDecryptedVotes(lastControlComponentShufflePayload.getVerifiableDecryptions().getCiphertexts())
							.setVerifiableShuffle(tallyComponentShufflePayload.getVerifiableShuffle())
							.setVerifiablePlaintextDecryption(tallyComponentShufflePayload.getVerifiablePlaintextDecryption())
							.setSelectedEncodedVotingOptions(L_votes)
							.build();

					return verifyTallyControlComponentBallotBoxAlgorithm.verifyTallyControlComponentBallotBox(context, input);
				})
				.toList();

		if (tallyVerif.stream().allMatch(Boolean::booleanValue)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification501.nok.message"));
		}
	}
}
