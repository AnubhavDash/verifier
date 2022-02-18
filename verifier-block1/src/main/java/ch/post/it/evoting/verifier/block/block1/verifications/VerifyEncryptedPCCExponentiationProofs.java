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
import ch.post.it.evoting.verifier.core.internal.exceptions.VerificationPreconditionException;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.plugin.contract.AbstractVerification;
import ch.post.it.evoting.verifier.plugin.contract.Category;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.plugin.contract.VerificationTrait;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerifierEvent;

@Service
public class VerifyEncryptedPCCExponentiationProofs extends AbstractVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyEncryptedPCCExponentiationProofs.class);

	private final ExponentiationProofsVerificationService<PCCExponentiationProofVerificationInput> exponentiationProofsVerificationService;
	private final ZeroKnowledgeProof zeroKnowledgeProof;

	public VerifyEncryptedPCCExponentiationProofs(
			final ExponentiationProofsVerificationService<PCCExponentiationProofVerificationInput> exponentiationProofsVerificationService,
			final ZeroKnowledgeProof zeroKnowledgeProof, final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.exponentiationProofsVerificationService = exponentiationProofsVerificationService;
		this.zeroKnowledgeProof = zeroKnowledgeProof;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(1);
		definition.setCategory(Category.EVIDENCE);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification21.description"));
		definition.setId(21);
		definition.setName("verifyEncryptedPCCExponentiationProofs");
		definition.addVerificationTrait(VerificationTrait.CONFIGURATION);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		return exponentiationProofsVerificationService.verifyEncryptedExponentiationProofs(inputDirectoryPath, getVerificationDefinition(),
				this::massageData, this::verifyEncryptedPCCExponentiationProofsVerificationCardSet);
	}

	@SuppressWarnings({ "java:S117", "java:S1612" })
	boolean verifyEncryptedPCCExponentiationProofsVerificationCardSet(VerificationContext context,
			Stream<Stream<PCCExponentiationProofVerificationInput>> inputs) {
		return inputs.parallel().flatMap(inputsFromJ -> inputsFromJ
						.parallel()
						.map(input -> verifyVerificationCardEncryptedPCCExponentiationProofForControlComponentJVotingCardId(context, input)))
				.reduce(Boolean::logicalAnd).orElse(Boolean.FALSE);
	}

	/**
	 * Verifies the exponentiation proof of one voting card.
	 *
	 * @param context Contains election context
	 * @param input   Contains necessary input for verification
	 * @return true if the proofs are valid for the {@code VC_id}
	 */
	@SuppressWarnings({ "java:S117" })
	private boolean verifyVerificationCardEncryptedPCCExponentiationProofForControlComponentJVotingCardId(VerificationContext context,
			PCCExponentiationProofVerificationInput input) {

		LOGGER.info("Processing verificationCardId : {} for control component : {}", input.vc_id, input.j);

		GroupVector<GqElement, GqGroup> g = exponentiationBases(context.generator(), input.c_pCC_id);
		GroupVector<GqElement, GqGroup> y = exponentiationBases(input.K_j_id, input.c_expPCC_j_id);

		List<String> i_aux = List.of(context.ee(), input.vc_id, "GenEncLongCodeShares", String.valueOf(input.j));

		boolean result = zeroKnowledgeProof.verifyExponentiation(g, y, input.pi_expPCC_j_id, i_aux);

		if (result) {
			LOGGER.info(
					"Encrypted partial choice return code exponentiation proof verification successful for VerificationCardId {} and control component {} ",
					input.vc_id, input.j);
		} else {
			LOGGER.error(
					"Encrypted partial choice return code exponentiation proof verification failed for VerificationCardId {} and control component {}",
					input.vc_id, input.j);
		}
		return result;
	}

	/**
	 * Prepares the data for verification, taking only what is needed from the {@link ReturnCodeGenerationRequestPayload} and the {@link
	 * ReturnCodeGenerationResponsePayload}
	 *
	 * @param returnCodeGenerationRequestPayload The initial request sent by the print office to the online control components
	 * @param controlComponentContributions      Contains the exponentiated encrypted partial choice return codes and associated proofs
	 * @return a stream of {@link VerificationInput}
	 */
	@SuppressWarnings({ "java:S117", "java:S100" })
	private Stream<Stream<PCCExponentiationProofVerificationInput>> massageData(
			ReturnCodeGenerationRequestPayload returnCodeGenerationRequestPayload,
			List<ReturnCodeGenerationResponsePayload> controlComponentContributions) {

		Map<String, ElGamalMultiRecipientCiphertext> VC_id_mapTo_c_pCC_id = returnCodeGenerationRequestPayload.getReturnCodeGenerationInputs()
				.stream()
				.collect(Collectors.toMap(ReturnCodeGenerationInput::getVerificationCardId,
						ReturnCodeGenerationInput::getEncryptedHashedSquaredPartialChoiceReturnCodes));

		return controlComponentContributions.stream()
				.map(controlComponentContribution -> controlComponentContribution
						.getReturnCodeGenerationOutputs()
						.stream()
						.map(controlComponentContributionOutput -> {

							String VC_id = controlComponentContributionOutput.getVerificationCardId();
							ElGamalMultiRecipientCiphertext c_pCC_id = VC_id_mapTo_c_pCC_id.get(VC_id);

							if (c_pCC_id == null) {
								throw new VerificationPreconditionException(String.format("No c_pCC_id found for VC_id : %s", VC_id));
							}

							int j = controlComponentContribution.getNodeId();

							if (controlComponentContributionOutput.getVoterVoteCastReturnCodeGenerationPublicKey().size() != 1) {
								throw new VerificationPreconditionException("The public key should only have one GqElement");
							}

							GqElement K_j_id = controlComponentContributionOutput.getVoterChoiceReturnCodeGenerationPublicKey().get(0);
							ElGamalMultiRecipientCiphertext c_expPCC_j_id = controlComponentContributionOutput.getExponentiatedEncryptedPartialChoiceReturnCodes();
							var pi_expPCC_j_id = controlComponentContributionOutput.getEncryptedPartialChoiceReturnCodeExponentiationProof();

							return new PCCVerificationInputBuilder()
									.setJ(j)
									.setVC_id(VC_id)
									.setc_pCC_id(c_pCC_id)
									.setK_j_id(K_j_id)
									.setc_expPCC_j_id(c_expPCC_j_id)
									.setPi_expPCC_j_id(pi_expPCC_j_id)
									.createPCCVerificationInput();

						}));
	}

	@SuppressWarnings({ "java:S116", "java:S117" })
	static class PCCExponentiationProofVerificationInput implements VerificationInput {
		private final Integer j;
		private final String vc_id;
		private final ElGamalMultiRecipientCiphertext c_pCC_id;
		private final GqElement K_j_id;
		private final ElGamalMultiRecipientCiphertext c_expPCC_j_id;
		private final ExponentiationProof pi_expPCC_j_id;

		public PCCExponentiationProofVerificationInput(Integer j, String vc_id,
				ElGamalMultiRecipientCiphertext c_pCC_id, GqElement K_j_id,
				ElGamalMultiRecipientCiphertext c_expPCC_j_id, ExponentiationProof pi_expPCC_j_id) {

			this.j = checkNotNull(j);
			this.vc_id = checkNotNull(vc_id);
			this.c_pCC_id = checkNotNull(c_pCC_id);
			this.K_j_id = checkNotNull(K_j_id);
			this.c_expPCC_j_id = checkNotNull(c_expPCC_j_id);
			this.pi_expPCC_j_id = checkNotNull(pi_expPCC_j_id);
		}

	}

	@SuppressWarnings({ "java:S116", "java:S117", "java:S100" })
	public static class PCCVerificationInputBuilder {
		private Integer j;
		private String vc_id;
		private ElGamalMultiRecipientCiphertext c_pCC_id;
		private GqElement K_j_id;
		private ElGamalMultiRecipientCiphertext c_expPCC_j_id;
		private ExponentiationProof pi_expPCC_j_id;

		public PCCVerificationInputBuilder setJ(Integer j) {
			this.j = j;
			return this;
		}

		public PCCVerificationInputBuilder setVC_id(String vc_id) {
			this.vc_id = vc_id;
			return this;
		}

		public PCCVerificationInputBuilder setc_pCC_id(ElGamalMultiRecipientCiphertext c_pCC_id) {
			this.c_pCC_id = c_pCC_id;
			return this;
		}

		public PCCVerificationInputBuilder setK_j_id(GqElement K_j_id) {
			this.K_j_id = K_j_id;
			return this;
		}

		public PCCVerificationInputBuilder setc_expPCC_j_id(ElGamalMultiRecipientCiphertext c_expPCC_j_id) {
			this.c_expPCC_j_id = c_expPCC_j_id;
			return this;
		}

		public PCCVerificationInputBuilder setPi_expPCC_j_id(ExponentiationProof pi_expPCC_j_id) {
			this.pi_expPCC_j_id = pi_expPCC_j_id;
			return this;
		}

		public PCCExponentiationProofVerificationInput createPCCVerificationInput() {
			return new PCCExponentiationProofVerificationInput(j, vc_id, c_pCC_id, K_j_id, c_expPCC_j_id, pi_expPCC_j_id);
		}
	}
}
