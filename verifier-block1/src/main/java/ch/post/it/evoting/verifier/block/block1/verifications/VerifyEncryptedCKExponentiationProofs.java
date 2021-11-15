/*
 * Copyright 2021 Post CH Ltd
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

package ch.post.it.evoting.verifier.block.block1.verifications;

import static ch.post.it.evoting.verifier.block.block1.verifications.ExponentiationProofsVerificationService.exponentiationBases;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.GroupVector;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ReturnCodeGenerationInput;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ReturnCodeGenerationRequestPayload;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ReturnCodeGenerationResponsePayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ExponentiationProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProof;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.block.block1.verifications.ExponentiationProofsVerificationService.VerificationContext;
import ch.post.it.evoting.verifier.block.block1.verifications.ExponentiationProofsVerificationService.VerificationInput;
import ch.post.it.evoting.verifier.common.AbstractVerification;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationPreconditionException;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;

@Service
public class VerifyEncryptedCKExponentiationProofs extends AbstractVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyEncryptedCKExponentiationProofs.class);

	private final ExponentiationProofsVerificationService<CKExponentiationProofVerificationInput> exponentiationProofsVerificationService;
	private final ZeroKnowledgeProof zeroKnowledgeProof;

	public VerifyEncryptedCKExponentiationProofs(
			final ExponentiationProofsVerificationService<CKExponentiationProofVerificationInput> exponentiateService,
			final ZeroKnowledgeProof zeroKnowledgeProof, final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.exponentiationProofsVerificationService = exponentiateService;
		this.zeroKnowledgeProof = zeroKnowledgeProof;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(1);
		definition.setCategory(Category.EVIDENCE);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification22.description"));
		definition.setId(22);
		definition.setName("verifyEncryptedCKExponentiationProofs");
		definition.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		definition.addVerificationTrait(VerificationTrait.BLOCK_1);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		return exponentiationProofsVerificationService.verifyEncryptedExponentiationProofs(inputDirectoryPath, getVerificationDefinition(),
				this::massageData, this::verifyEncryptedCKExponentiationProofsVerificationCardSet);
	}

	@SuppressWarnings({ "java:S117", "java:S1612" })
	boolean verifyEncryptedCKExponentiationProofsVerificationCardSet(VerificationContext context,
			Stream<Stream<CKExponentiationProofVerificationInput>> inputs) {
		return inputs.parallel().flatMap(inputsFromJ -> inputsFromJ
						.parallel()
						.map(input -> verifyForControlComponentJVotingCardId(context, input)))
				.reduce(Boolean::logicalAnd).orElse(Boolean.FALSE);
	}

	/**
	 * Verifies the exponentiation proof of one voting card for one control component.
	 *
	 * @param context Contains election context
	 * @param input   Contains necessary input for verification
	 * @return true if the proofs is valid for the {@code VC_id} and control component {@code j}
	 */
	@SuppressWarnings({ "java:S117" })
	private boolean verifyForControlComponentJVotingCardId(VerificationContext context, CKExponentiationProofVerificationInput input) {

		LOGGER.info("Processing verificationCardId : {} for control component : {}", input.VC_id, input.j);

		GroupVector<GqElement, GqGroup> g = exponentiationBases(context.getGenerator(), input.C_ck);
		GroupVector<GqElement, GqGroup> y = exponentiationBases(input.Kc_j_id, input.C_expCK_j_id);

		List<String> i_aux = List.of(context.get_ee(), input.VC_id, "GenEncLongCodeShares", String.valueOf(input.j));

		boolean result = zeroKnowledgeProof.verifyExponentiation(g, y, input.pi_expCK_j_id, i_aux);

		if (result) {
			LOGGER.info("Encrypted confirmation key exponentiation proof verification successful for VerificationCardId {} and control component {}",
					input.VC_id, input.j);
		} else {
			LOGGER.error("Encrypted confirmation key exponentiation proof verification failed for VerificationCardId {} and control component {}",
					input.VC_id, input.j);
		}
		return result;
	}

	/**
	 * Prepares the data for verification, taking only what is needed from the {@link ReturnCodeGenerationRequestPayload} and the {@link
	 * ReturnCodeGenerationResponsePayload}
	 *
	 * @param returnCodeGenerationRequestPayload The initial request sent by the print office to the online control components
	 * @param controlComponentContributions      Contains the exponentiated encrypted confirmation keys and associated proofs
	 * @return a stream of {@link VerificationInput}
	 */
	@SuppressWarnings({ "java:S117", "java:S100" })
	private Stream<Stream<CKExponentiationProofVerificationInput>> massageData(
			ReturnCodeGenerationRequestPayload returnCodeGenerationRequestPayload,
			List<ReturnCodeGenerationResponsePayload> controlComponentContributions) {

		Map<String, ElGamalMultiRecipientCiphertext> VC_id_mapTo_C_ck = returnCodeGenerationRequestPayload.getReturnCodeGenerationInputs()
				.stream()
				.collect(Collectors.toMap(ReturnCodeGenerationInput::getVerificationCardId,
						ReturnCodeGenerationInput::getEncryptedHashedSquaredConfirmationKey));

		return controlComponentContributions.stream()
				.map(controlComponentContribution -> controlComponentContribution
						.getReturnCodeGenerationOutputs()
						.stream()
						.map(controlComponentContributionOutput -> {

							String VC_id = controlComponentContributionOutput.getVerificationCardId();
							ElGamalMultiRecipientCiphertext C_ck = VC_id_mapTo_C_ck.get(VC_id);

							if (C_ck == null) {
								throw new VerificationPreconditionException(String.format("No C_ck found for VC_id : %s", VC_id));
							}

							int j = controlComponentContribution.getNodeId();

							if (controlComponentContributionOutput.getVoterVoteCastReturnCodeGenerationPublicKey().size() != 1) {
								throw new VerificationPreconditionException("The public key should only have one GqElement");
							}

							GqElement Kc_j_id = controlComponentContributionOutput.getVoterVoteCastReturnCodeGenerationPublicKey().get(0);

							ElGamalMultiRecipientCiphertext C_expCK_j_id = controlComponentContributionOutput.getExponentiatedEncryptedConfirmationKey();
							var pi_expCK_j_id = controlComponentContributionOutput.getEncryptedConfirmationKeyExponentiationProof();

							return new CKVerificationInputBuilder()
									.setJ(j)
									.setVC_id(VC_id)
									.setC_ck(C_ck)
									.setKc_j_id(Kc_j_id)
									.setC_expCK_j_id(C_expCK_j_id)
									.setPi_expCK_j_id(pi_expCK_j_id)
									.createCKVerificationInput();
						}));
	}

	@SuppressWarnings({ "java:S116", "java:S117" })
	static class CKExponentiationProofVerificationInput implements VerificationInput {
		private final Integer j;
		private final String VC_id;
		private final ElGamalMultiRecipientCiphertext C_ck;
		private final GqElement Kc_j_id;
		private final ElGamalMultiRecipientCiphertext C_expCK_j_id;
		private final ExponentiationProof pi_expCK_j_id;

		public CKExponentiationProofVerificationInput(Integer j, String VC_id,
				ElGamalMultiRecipientCiphertext C_ck, GqElement Kc_j_id,
				ElGamalMultiRecipientCiphertext C_expCK_j_id, ExponentiationProof pi_expCK_j_id) {

			this.j = checkNotNull(j);
			this.VC_id = checkNotNull(VC_id);
			this.C_ck = checkNotNull(C_ck);
			this.Kc_j_id = checkNotNull(Kc_j_id);
			this.C_expCK_j_id = checkNotNull(C_expCK_j_id);
			this.pi_expCK_j_id = checkNotNull(pi_expCK_j_id);
		}

	}

	@SuppressWarnings({ "java:S116", "java:S117", "java:S100" })
	public static class CKVerificationInputBuilder {
		private Integer j;
		private String vc_id;
		private ElGamalMultiRecipientCiphertext c_ck;
		private GqElement kc_j_id;
		private ElGamalMultiRecipientCiphertext c_expCK_j_id;
		private ExponentiationProof pi_expCK_j_id;

		public CKVerificationInputBuilder setJ(Integer j) {
			this.j = j;
			return this;
		}

		public CKVerificationInputBuilder setVC_id(String vc_id) {
			this.vc_id = vc_id;
			return this;
		}

		public CKVerificationInputBuilder setC_ck(ElGamalMultiRecipientCiphertext c_ck) {
			this.c_ck = c_ck;
			return this;
		}

		public CKVerificationInputBuilder setKc_j_id(GqElement kc_j_id) {
			this.kc_j_id = kc_j_id;
			return this;
		}

		public CKVerificationInputBuilder setC_expCK_j_id(ElGamalMultiRecipientCiphertext c_expCK_j_id) {
			this.c_expCK_j_id = c_expCK_j_id;
			return this;
		}

		public CKVerificationInputBuilder setPi_expCK_j_id(ExponentiationProof pi_expCK_j_id) {
			this.pi_expCK_j_id = pi_expCK_j_id;
			return this;
		}

		public CKExponentiationProofVerificationInput createCKVerificationInput() {
			return new CKExponentiationProofVerificationInput(j, vc_id, c_ck, kc_j_id, c_expCK_j_id, pi_expCK_j_id);
		}
	}
}
