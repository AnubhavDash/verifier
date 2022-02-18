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
package ch.post.it.evoting.verifier.block.block4.verifications;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;

import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetFinalPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetShufflePayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.plugin.contract.AbstractVerification;
import ch.post.it.evoting.verifier.plugin.contract.Category;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.plugin.contract.VerificationTrait;
import ch.post.it.evoting.verifier.core.internal.dto.revised.PublicKey;
import ch.post.it.evoting.verifier.core.internal.tools.Deserializer;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.core.internal.tools.TypeConverter;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathService;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerifierEvent;

@Component
public class CheckConsistencyOfflineProofs extends AbstractVerification {

	private final PathService pathService;
	private final ElectionDataExtractionService extractionService;
	private final ObjectMapper objectMapper;

	public CheckConsistencyOfflineProofs(final PathService pathService, final ElectionDataExtractionService extractionService,
			final ObjectMapper objectMapper, final
	ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
		this.extractionService = extractionService;
		this.objectMapper = objectMapper;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		var verificationDefinition = new VerificationDefinition();
		verificationDefinition.setBlockId(4);
		verificationDefinition.setCategory(Category.CONSISTENCY);
		verificationDefinition.setId(41);
		verificationDefinition.setName("checkConsistencyOfflineProofs");
		verificationDefinition.setDescription(TranslationHelper
				.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification41.description"));
		verificationDefinition.addVerificationTrait(VerificationTrait.FINAL_DECRYPTION);

		return verificationDefinition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		final List<CheckConsistencyOfflineProofsInput> inputs = deserializeInputs(inputDirectoryPath);
		final boolean areEncryptionGroupsOk = inputs.stream()
				.map(this::checkEncryptionGroup)
				.reduce(Boolean.TRUE, Boolean::logicalAnd); // stream will be empty if no one voted electronically
		final boolean areRemainingPublicKeysOk = inputs.stream()
				.map(this::checkRemainingElectionPublicKey)
				.reduce(Boolean.TRUE, Boolean::logicalAnd); // stream will be empty if no one voted electronically

		if (!areEncryptionGroupsOk) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification41.nok.encryption.group.message"));
		} else if (!areRemainingPublicKeysOk) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification41.nok.public.keys.message"));
		} else {
			return VerificationResultEvent.success(this, getVerificationDefinition());
		}
	}

	private List<CheckConsistencyOfflineProofsInput> deserializeInputs(final Path inputDirectoryPath) {
		final var encryptionGroup = extractionService.getEncryptionParameters(inputDirectoryPath);
		final var electoralAuthorityPublicKeyPath = pathService.buildFromRootPath(StructureKey.ELECTORAL_AUTHORITY_PUBLIC_KEY, inputDirectoryPath);
		final ElectoralAuthorityPublicKey electoralAuthorityPublicKey;
		try {
			electoralAuthorityPublicKey = objectMapper.readValue(electoralAuthorityPublicKeyPath.getPath().toFile(),
					ElectoralAuthorityPublicKey.class);
		} catch (IOException e) {
			throw new UncheckedIOException("Could not read electoral authority public key", e);
		}
		final var electoralBoardPublicKey = transformToElGamalPublicKey(electoralAuthorityPublicKey);

		final var electionEvent = extractionService.getElectionEvent(inputDirectoryPath);
		final var ballotBoxList = electionEvent.getBallotBoxes();
		final var ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		return ballotBoxList.stream()
				.map(ballotBox -> TypeConverter.UUIDToStringWithoutDash(ballotBox.getId()))
				.map(ballotIdsPathNode::getRegexPath)
				.filter(ballotBoxDirectoryPath -> extractionService.getNumberOfVotes(ballotBoxDirectoryPath) > 0)
				.map(ballotBoxDirectoryPath -> {
					final var mixnetShufflePayloads = extractionService.getMixnetShufflePayloads(ballotBoxDirectoryPath);
					mixnetShufflePayloads.sort(Comparator.comparingInt(MixnetShufflePayload::getNodeId));
					final var lastMixnetShufflePayload = mixnetShufflePayloads.get(2);
					final var mixnetFinalPayload = extractionService.getMixnetFinalPayload(ballotBoxDirectoryPath);

					return new CheckConsistencyOfflineProofsInput(encryptionGroup, lastMixnetShufflePayload, mixnetFinalPayload,
							electoralBoardPublicKey);
				}).collect(Collectors.toList());
	}

	private ElGamalMultiRecipientPublicKey transformToElGamalPublicKey(final ElectoralAuthorityPublicKey electoralAuthorityPublicKey) {
		final PublicKey publicKey;
		try {
			publicKey = Deserializer.fromJson(Base64.getDecoder().decode(electoralAuthorityPublicKey.getPublicKey()), PublicKey.class);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize the public key", e);
		}
		final var encryptionGroup = publicKey.getGroup();
		final var keys = publicKey.getKeys();
		final var gqGroup = new GqGroup(encryptionGroup.getP(), encryptionGroup.getQ(), encryptionGroup.getG());
		final var keyElements = keys.stream()
				.map(keyValue -> GqElement.create(keyValue, gqGroup))
				.collect(Collectors.toList());

		return new ElGamalMultiRecipientPublicKey(keyElements);
	}

	/**
	 * Checks whether the encryption groups of the shuffle payloads of a ballot box are the same as the encryption group of the initial payload of
	 * that ballot box.
	 *
	 * @param input the initial payload and the shuffle payloads of a ballot box.
	 * @return true if all shuffle payloads have the same encryption group as the initial payload, false otherwise
	 */
	@VisibleForTesting
	boolean checkEncryptionGroup(final CheckConsistencyOfflineProofsInput input) {
		checkNotNull(input);

		final var lastMixnetShufflePayload = checkNotNull(input.lastMixnetShufflePayload);
		final var mixnetFinalPayload = checkNotNull(input.mixnetFinalPayload);
		final var encryptionGroup = checkNotNull(input.encryptionGroup);

		final var finalPayloadHasCorrectGroup = mixnetFinalPayload.getEncryptionGroup().equals(encryptionGroup);
		final var lastMixnetShufflePayloadHasCorrectGroup = lastMixnetShufflePayload.getEncryptionGroup().equals(encryptionGroup);

		return finalPayloadHasCorrectGroup && lastMixnetShufflePayloadHasCorrectGroup;
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
	boolean checkRemainingElectionPublicKey(final CheckConsistencyOfflineProofsInput input) {
		checkNotNull(input);

		final var lastMixnetShufflePayload = checkNotNull(input.lastMixnetShufflePayload);
		final var mixnetFinalPayload = checkNotNull(input.mixnetFinalPayload);
		final var electoralBoardPublicKey = checkNotNull(input.electoralBoardPublicKey);

		final var lastMixnetRemainingEqualsFinalPreviousRemaining = lastMixnetShufflePayload.getRemainingElectionPublicKey()
				.equals(mixnetFinalPayload.getPreviousRemainingElectionPublicKey());
		final var finalPreviousRemainingEqualsElectoralBoardPK = mixnetFinalPayload.getPreviousRemainingElectionPublicKey()
				.equals(electoralBoardPublicKey);

		return lastMixnetRemainingEqualsFinalPreviousRemaining && finalPreviousRemainingEqualsElectoralBoardPK;
	}

	@JsonPropertyOrder({ "electoralAuthorityId", "publicKey" })
	private static class ElectoralAuthorityPublicKey {

		@JsonProperty
		private String electoralAuthorityId;

		/**
		 * ElGamalPublicKey in its JSON representation encoded in BASE64
		 */
		@JsonProperty
		private String publicKey;

		@JsonCreator
		public ElectoralAuthorityPublicKey(
				@JsonProperty(value = "electoralAuthorityId", required = true)
				final String electoralAuthorityId,
				@JsonProperty(value = "publicKey", required = true)
				final String publicKey) {
			this.electoralAuthorityId = electoralAuthorityId;
			this.publicKey = publicKey;
		}

		public String getPublicKey() {
			return publicKey;
		}

	}

	@VisibleForTesting
	static class CheckConsistencyOfflineProofsInput {
		final GqGroup encryptionGroup;
		final MixnetShufflePayload lastMixnetShufflePayload;
		final MixnetFinalPayload mixnetFinalPayload;
		final ElGamalMultiRecipientPublicKey electoralBoardPublicKey;

		CheckConsistencyOfflineProofsInput(final GqGroup encryptionGroup, final MixnetShufflePayload lastMixnetShufflePayload,
				final MixnetFinalPayload mixnetFinalPayload, final ElGamalMultiRecipientPublicKey electoralBoardPublicKey) {
			this.encryptionGroup = checkNotNull(encryptionGroup);
			this.lastMixnetShufflePayload = checkNotNull(lastMixnetShufflePayload);
			this.mixnetFinalPayload = checkNotNull(mixnetFinalPayload);
			this.electoralBoardPublicKey = checkNotNull(electoralBoardPublicKey);
		}
	}
}
