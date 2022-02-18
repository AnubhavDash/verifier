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
package ch.post.it.evoting.verifier.block.block3.verifications;

import static ch.post.it.evoting.cryptoprimitives.Validations.allEqual;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetInitialPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetShufflePayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.plugin.contract.AbstractVerification;
import ch.post.it.evoting.verifier.plugin.contract.Category;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.plugin.contract.VerificationTrait;
import ch.post.it.evoting.verifier.core.internal.dto.revised.BallotBox;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.core.internal.tools.TypeConverter;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathService;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerifierEvent;

@Component
public class CheckConsistencyOnlineProofs extends AbstractVerification {

	private static final int SHUFFLE_PAYLOADS_PER_BALLOT_BOX = 3;
	private static final String SHUFFLE_PAYLOAD_PER_BALLOT_BOX_MESSAGE = "There must be exactly 3 shuffle payloads in the ballot box";

	private final PathService pathService;
	private final ElectionDataExtractionService extractionService;

	public CheckConsistencyOnlineProofs(final PathService pathService, final ElectionDataExtractionService extractionService, final
	ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		var verificationDefinition = new VerificationDefinition();
		verificationDefinition.setBlockId(3);
		verificationDefinition.setCategory(Category.CONSISTENCY);
		verificationDefinition.setId(41);
		verificationDefinition.setName("checkConsistencyOnlineProofs");
		verificationDefinition.setDescription(TranslationHelper
				.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification41.description"));
		verificationDefinition.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);

		return verificationDefinition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		final List<CheckConsistencyOnlineProofsInput> inputs = deserializeInputs(inputDirectoryPath);
		final boolean areEncryptionGroupsOk = inputs.stream()
				.map(this::checkEncryptionGroup)
				.reduce(Boolean::logicalAnd).orElse(Boolean.TRUE); // Default is TRUE because stream will be empty if no one voted electronically
		final boolean areRemainingPublicKeysOk = inputs.stream()
				.map(this::checkRemainingElectionPublicKeys)
				.reduce(Boolean::logicalAnd).orElse(Boolean.TRUE); // Default is TRUE because stream will be empty if no one voted electronically

		if (!areEncryptionGroupsOk) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification41.nok.encryption.group.message"));
		} else if (!areRemainingPublicKeysOk) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification41.nok.public.keys.message"));
		} else {
			return VerificationResultEvent.success(this, getVerificationDefinition());
		}
	}

	private List<CheckConsistencyOnlineProofsInput> deserializeInputs(final Path inputDirectoryPath) {
		final var encryptionGroup = extractionService.getEncryptionParameters(inputDirectoryPath);

		final var electionEvent = extractionService.getElectionEvent(inputDirectoryPath);
		final List<BallotBox> ballotBoxList = electionEvent.getBallotBoxes();
		final var ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxList.stream()
				.map(ballotBox -> TypeConverter.UUIDToStringWithoutDash(ballotBox.getId()))
				.map(ballotIdsPathNode::getRegexPath)
				.filter(ballotBoxDirectoryPath -> extractionService.getNumberOfVotes(ballotBoxDirectoryPath) > 0)
				.map(ballotBoxDirectoryPath -> {
						final var initialPayload = extractionService.getMixnetInitialPayload(ballotBoxDirectoryPath);
						final List<MixnetShufflePayload> shufflePayloads = extractionService.getMixnetShufflePayloads(ballotBoxDirectoryPath);
						shufflePayloads.sort(Comparator.comparingInt(MixnetShufflePayload::getNodeId));

						return new CheckConsistencyOnlineProofsInput(encryptionGroup, initialPayload, ImmutableList.copyOf(shufflePayloads));
				}).collect(Collectors.toList());
	}

	/**
	 * Checks whether the encryption groups of the shuffle payloads of a ballot box are the same as the encryption group of the initial payload of
	 * that ballot box.
	 *
	 * @param input the initial payload and the shuffle payloads of a ballot box.
	 * @return true if all shuffle payloads have the same encryption group as the initial payload, false otherwise
	 */
	@VisibleForTesting
	boolean checkEncryptionGroup(final CheckConsistencyOnlineProofsInput input) {
		checkNotNull(input);

		final var initialPayload = input.mixnetInitialPayload;
		final ImmutableList<MixnetShufflePayload> shufflePayloads = input.mixnetShufflePayloads;
		final var encryptionGroup = input.encryptionGroup;

		checkNotNull(initialPayload);
		checkNotNull(shufflePayloads);
		checkNotNull(encryptionGroup);

		checkArgument(shufflePayloads.size() == SHUFFLE_PAYLOADS_PER_BALLOT_BOX, SHUFFLE_PAYLOAD_PER_BALLOT_BOX_MESSAGE);
		final var initialPayloadEncryptionGroupOk = initialPayload.getEncryptionGroup().equals(encryptionGroup);
		final var shufflePayloadEncryptionGroupOk = shufflePayloads.stream().map(MixnetShufflePayload::getEncryptionGroup).allMatch(encryptionGroup::equals);

		return initialPayloadEncryptionGroupOk && shufflePayloadEncryptionGroupOk;
	}

	/**
	 * Checks the consistency of the remaining election public keys in the shuffle payloads.
	 * <p>
	 * The remaining election public key of the 1st shuffle payload, must be the election public key divided by the CCM_1 election public key, the
	 * remaining election public of the 2nd shuffle payload must be the remaining election public key of the 1st node divided by the CCM_2 election
	 * public key, etc, where the node election public keys are compressed to the size of the remaining public keys.
	 * </p>
	 *
	 * @param input the initial payload and the shuffle payloads of a ballot box
	 * @return true if consistency check passes, false otherwise
	 */
	@VisibleForTesting
	boolean checkRemainingElectionPublicKeys(final CheckConsistencyOnlineProofsInput input) {
		checkNotNull(input);

		final MixnetInitialPayload initialPayload = input.mixnetInitialPayload;
		final ImmutableList<MixnetShufflePayload> shufflePayloads = input.mixnetShufflePayloads;

		checkNotNull(initialPayload);
		checkNotNull(shufflePayloads);

		checkArgument(shufflePayloads.size() == SHUFFLE_PAYLOADS_PER_BALLOT_BOX, SHUFFLE_PAYLOAD_PER_BALLOT_BOX_MESSAGE);

		checkArgument(
				allEqual(shufflePayloads.stream().map(MixnetShufflePayload::getRemainingElectionPublicKey), ElGamalMultiRecipientPublicKey::size),
				"All remaining election public keys must have the same size");
		checkArgument(allEqual(shufflePayloads.stream().map(MixnetShufflePayload::getPreviousRemainingElectionPublicKey),
						ElGamalMultiRecipientPublicKey::size),
				"All previous remaining election public keys must have the same size");
		checkArgument(allEqual(shufflePayloads.stream().map(MixnetShufflePayload::getNodeElectionPublicKey), ElGamalMultiRecipientPublicKey::size),
				"All node election public keys must have the same size");
		final int remainingPublicKeySize = shufflePayloads.get(0).getRemainingElectionPublicKey().size();
		final int previousRemainingPublicKeySize = shufflePayloads.get(0).getPreviousRemainingElectionPublicKey().size();
		final int nodeElectionPublicKeySize = shufflePayloads.get(0).getNodeElectionPublicKey().size();
		checkArgument(remainingPublicKeySize == previousRemainingPublicKeySize,
				"The previous remaining election public key and the remaining public key must have the same size");
		checkArgument(remainingPublicKeySize <= nodeElectionPublicKeySize,
				"The remaining election public key must not have more elements than the node election public key");

		ElGamalMultiRecipientPublicKey previousPayloadRemainingPublicKey = initialPayload.getElectionPublicKey();
		for (MixnetShufflePayload shufflePayload : shufflePayloads) {
			final ElGamalMultiRecipientPublicKey previousRemainingElectionPublicKey = shufflePayload.getPreviousRemainingElectionPublicKey();
			if (!previousPayloadRemainingPublicKey.equals(previousRemainingElectionPublicKey)) {
				return false;
			}
			final ElGamalMultiRecipientPublicKey remainingElectionPublicKey = shufflePayload.getRemainingElectionPublicKey();
			final ElGamalMultiRecipientPublicKey nodeElectionPublicKey = shufflePayload.getNodeElectionPublicKey().compress(
					remainingElectionPublicKey.size());

			final boolean areKeysConsistent = IntStream.range(0, remainingElectionPublicKey.size())
					.mapToObj(i -> previousRemainingElectionPublicKey.get(i).multiply(nodeElectionPublicKey.get(i).invert())
							.equals(remainingElectionPublicKey.get(i)))
					.reduce(Boolean::logicalAnd).orElse(Boolean.FALSE);

			if (!areKeysConsistent) {
				return false;
			}

			previousPayloadRemainingPublicKey = remainingElectionPublicKey;
		}

		return true;
	}

	@VisibleForTesting
	static class CheckConsistencyOnlineProofsInput {
		final GqGroup encryptionGroup;
		final MixnetInitialPayload mixnetInitialPayload;
		final ImmutableList<MixnetShufflePayload> mixnetShufflePayloads;

		CheckConsistencyOnlineProofsInput(final GqGroup encryptionGroup, final MixnetInitialPayload mixnetInitialPayload,
				final ImmutableList<MixnetShufflePayload> mixnetShufflePayloads) {
			checkNotNull(encryptionGroup);
			checkNotNull(mixnetInitialPayload);
			checkNotNull(mixnetShufflePayloads);
			this.encryptionGroup = encryptionGroup;
			this.mixnetInitialPayload = mixnetInitialPayload;
			this.mixnetShufflePayloads = mixnetShufflePayloads;
		}
	}
}
