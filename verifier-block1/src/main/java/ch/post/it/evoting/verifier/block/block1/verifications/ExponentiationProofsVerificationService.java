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

import static com.google.common.base.Preconditions.checkState;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.GroupVector;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ReturnCodeGenerationOutput;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ReturnCodeGenerationRequestPayload;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ReturnCodeGenerationResponsePayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ExponentiationProof;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.core.internal.exceptions.VerificationPreconditionException;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathNode;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathService;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;

/**
 * Both the partial choice code and confirmation key exponentiation proofs are very similar but require different inputs. Hence, the parameterized
 * type T. It can be one of the following.
 * <ul>
 * 	<li>{@link VerifyEncryptedCKExponentiationProofs.CKExponentiationProofVerificationInput }</li>
 * 	<li>{@link VerifyEncryptedPCCExponentiationProofs.PCCExponentiationProofVerificationInput }</li>
 * </ul>
 * <p>
 * This service contains functionality common to both proofs and the {@link ExponentiationProofsVerificationService.VerificationContext}
 */
@Service
public class ExponentiationProofsVerificationService<T extends ExponentiationProofsVerificationService.VerificationInput> {

	final ElectionDataExtractionService extractionService;

	final PathService pathService;

	public ExponentiationProofsVerificationService(ElectionDataExtractionService extractionService,
			PathService pathService) {
		this.extractionService = extractionService;
		this.pathService = pathService;
	}

	/**
	 * Given an input directory and a reference to the appropriate data massaging and verification proof will determine if either all partial choice
	 * code or confirmation key exponentiation proofs are valid.
	 * <p>
	 *
	 * @param inputDirectoryPath                                    Location of the root directory where the ballot boxes code generation request and
	 *                                                              control component contribution response JSON files can be found (in
	 *                                                              subdirectories) .
	 * @param definition                                            The definition of the verification calling the service.
	 * @param massageData                                           Function to extract only the necessary fields from the data transfer objects for
	 *                                                              verification.
	 * @param verifyEncryptedExponentiationProofVerificationCardSet See {@link java.util.function.BiPredicate} for function definition. This parameter
	 *                                                              is a reference to either the partial choice code or confirmation key
	 *                                                              exponentiation proof function. Both proofs take a {@link VerificationContext} and
	 *                                                              a {@link java.util.stream.Stream} of {@link java.util.stream.Stream} as input. The
	 *                                                              stream of stream input consists of one of the following input types.
	 *                                                              <ul>
	 *                                                              	<li>{@link VerifyEncryptedCKExponentiationProofs.CKExponentiationProofVerificationInput }</li>
	 *                                                              	<li>{@link VerifyEncryptedPCCExponentiationProofs.PCCExponentiationProofVerificationInput }</li>
	 *                                                              </ul>
	 *                                                              	Both proofs return a true or false validation and are the implementation of the VerifyEncryptedCKExponentiationProof and VerifyEncryptedPCCExponentiationProof
	 * @return the verification result event.
	 */
	public VerificationResultEvent verifyEncryptedExponentiationProofs(
			final Path inputDirectoryPath,
			final VerificationDefinition definition,
			final BiFunction<ReturnCodeGenerationRequestPayload, List<ReturnCodeGenerationResponsePayload>, Stream<Stream<T>>> massageData,
			final BiPredicate<VerificationContext, Stream<Stream<T>>> verifyEncryptedExponentiationProofVerificationCardSet) {

		final PathNode verificationCardSetIDPaths = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		Boolean result = verificationCardSetIDPaths.getRegexPaths()
				.stream()
				.parallel()
				.map(votingCardSetIDPath
						-> {

					List<ReturnCodeGenerationRequestPayload> allReturnCodeGenerationRequestPayloads = extractionService.deserializeReturnCodeGenerationRequestPayload(
							votingCardSetIDPath);
					List<List<ReturnCodeGenerationResponsePayload>> allControlComponentContributions = extractionService.deserializeControlComponentContributions(
							votingCardSetIDPath);

					Stream<ReturnCodeExponentiationRequestResponseChunk> returnCodeExponentiationRequestResponseChunks   = assembleRequestResponseChunks(allReturnCodeGenerationRequestPayloads,allControlComponentContributions);

					return returnCodeExponentiationRequestResponseChunks.parallel().map(
							chunk -> {
								var returnCodeGenerationRequestPayload = chunk.returnCodeGenerationRequestPayload();
								var controlComponentContributions = chunk.controlComponentContributions();
								final var context = getVerificationContext(returnCodeGenerationRequestPayload);

								if (!checkAllVerificationCardSetsContainAtLeastOneVerificationCard(controlComponentContributions)) {
									throw new VerificationPreconditionException(
											String.format("The verification cards set %s should contain at least one verification card.",
													returnCodeGenerationRequestPayload.getVerificationCardSetId()));
								}

								return verifyEncryptedExponentiationProofVerificationCardSet.test(context,
										massageData.apply(returnCodeGenerationRequestPayload, controlComponentContributions));
							}
					).reduce(Boolean::logicalAnd).orElse(Boolean.FALSE);
				}).reduce(Boolean::logicalAnd).orElse(Boolean.FALSE);

		if (Boolean.TRUE.equals(result)) {
			return VerificationResultEvent.success(this, definition);
		} else {
			return VerificationResultEvent.failure(this, definition,
					TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification.exponentiation.proofs.nok.message"));
		}
	}

	/**
	 * Each {@link ReturnCodeGenerationResponsePayload} has a 1:1 mapping with a verification cardset ID <p> and contains a list of {@link
	 * ReturnCodeGenerationOutput}'s. Each {@link ReturnCodeGenerationOutput} has a 1:1 mapping with a verification card ID. The constructor of {@link
	 * ReturnCodeGenerationOutput} requires non null {@link ExponentiationProof } 's <p> As a result guaranteeing the size of the  {@link
	 * ReturnCodeGenerationOutput} list is > 0 also guarantees each voting cardset contains at least one proof.
	 *
	 * @param controlComponentContributions List of control component contributions
	 * @return true of false
	 */
	static boolean checkAllVerificationCardSetsContainAtLeastOneVerificationCard(
			List<ReturnCodeGenerationResponsePayload> controlComponentContributions) {
		return controlComponentContributions
				.stream()
				.map(returnCodeResponsePayload -> returnCodeResponsePayload.getReturnCodeGenerationOutputs().size())
				.allMatch(size -> size > 0);
	}

	VerificationContext getVerificationContext(ReturnCodeGenerationRequestPayload returnCodeGenerationRequestPayload) {
		String ee = returnCodeGenerationRequestPayload.getElectionEventId();
		GqElement generator = returnCodeGenerationRequestPayload.getEncryptionGroup().getGenerator();

		return new VerificationContext(ee, generator);
	}

	public static GroupVector<GqElement, GqGroup> exponentiationBases(GqElement generator,
			ElGamalMultiRecipientCiphertext elGamalMultiRecipientCiphertext) {
		List<GqElement> gqElements = new ArrayList<>();
		gqElements.add(generator);
		gqElements.addAll(elGamalMultiRecipientCiphertext.stream().toList());
		return GroupVector.from(gqElements);
	}

	/**
	 * Maps to context from the specification
	 */
	@SuppressWarnings({ "java:S117", "java:S100" })
	static record  VerificationContext(String ee, GqElement generator){}

	/**
	 * Maps to input from the specification
	 */
	public interface VerificationInput {

	}

	public Stream<ReturnCodeExponentiationRequestResponseChunk> assembleRequestResponseChunks(
			List<ReturnCodeGenerationRequestPayload> returnCodeGenerationRequestPayload,
			List<List<ReturnCodeGenerationResponsePayload>> controlComponentContributions)  {

		Map<Integer, List<ReturnCodeGenerationResponsePayload>> chunkIdToControlComponentContributions = controlComponentContributions.stream()
				.filter(chunk -> chunk.stream().findFirst().isPresent())
				.collect(Collectors.toMap(chunk -> chunk.stream().findFirst().get().getChunkId(), Function.identity()));

		Set<Integer> controlComponentContributionsChunkIDs = chunkIdToControlComponentContributions.keySet();
		List<Integer> returnCodeGenerationRequestChunkIDs = returnCodeGenerationRequestPayload.stream()
				.map(ReturnCodeGenerationRequestPayload::getChunkId)
				.toList();

		Set<Integer> uniqueReturnCodeGenerationRequestChunkIDs = new HashSet<>(returnCodeGenerationRequestChunkIDs);

		checkState(uniqueReturnCodeGenerationRequestChunkIDs.size() == returnCodeGenerationRequestChunkIDs.size(), "ReturnCodeGenerationRequest chunks not unique");

		checkState(controlComponentContributionsChunkIDs.equals(uniqueReturnCodeGenerationRequestChunkIDs),
				"Mismatch between the returnCodeGenerationRequest and controlComponentContribution chunks IDs. ReturnCodeGenerationRequest chunks IDs:  %s ,ControlComponentContribution chunks IDs : %s",
				returnCodeGenerationRequestChunkIDs, controlComponentContributionsChunkIDs);

		return returnCodeGenerationRequestPayload.stream()
				.map(request -> new ReturnCodeExponentiationRequestResponseChunk(request, chunkIdToControlComponentContributions.get(request.getChunkId())));

	}


	static record ReturnCodeExponentiationRequestResponseChunk(ReturnCodeGenerationRequestPayload returnCodeGenerationRequestPayload,
															   List<ReturnCodeGenerationResponsePayload> controlComponentContributions ) {}
}
