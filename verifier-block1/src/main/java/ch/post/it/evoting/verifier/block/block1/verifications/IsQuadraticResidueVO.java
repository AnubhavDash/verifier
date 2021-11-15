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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.AbstractVerification;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.dto.revised.CandidateList;
import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionParameters;
import ch.post.it.evoting.verifier.common.block.dto.revised.VoteOption;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;

@Component
public class IsQuadraticResidueVO extends AbstractVerification {

	private final PathService pathService;

	public IsQuadraticResidueVO(final PathService pathService, final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(1);
		definition.setCategory(Category.INTEGRITY);
		definition.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification06.description"));
		definition.setId(6);
		definition.setName("isQuadraticResidue([vo])");
		definition.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		definition.addVerificationTrait(VerificationTrait.BLOCK_1);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		final var encryptParams = pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);
		final EncryptionParameters encryptionParameters;
		try {
			encryptionParameters = Deserializer.fromJson(encryptParams.getPath(), EncryptionParameters.class);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize encryption parameters.", e);
		}
		BigInteger p = encryptionParameters.getP();

		final var dataConfigPathNode = pathService.buildFromRootPath(StructureKey.DATA_CONFIG_UPDATED, inputDirectoryPath);
		final ElectionEvent electionEvent;
		try {
			electionEvent = Deserializer.fromJson(dataConfigPathNode.getPath(), ElectionEvent.class);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize data config updated.", e);
		}

		//votations
		Collection<BigInteger> errors = electionEvent.getBallotBoxes().parallelStream()
				.flatMap(bb -> bb.getCountingCircles().stream())
				.flatMap(cc -> cc.getDomainsOfInfluence().stream())
				.flatMap(doi -> doi.getVotes().stream())
				.flatMap(v -> v.getQuestions().stream())
				.flatMap(q -> q.getOptions().stream())
				.filter(o -> !MathHelper.isEulerCriterionValid(o.getPrimeNumber(), p))
				.map(VoteOption::getPrimeNumber)
				.collect(Collectors.toList());

		//lists
		errors.addAll(
				electionEvent.getBallotBoxes().parallelStream()
						.flatMap(bb -> bb.getCountingCircles().stream())
						.flatMap(cc -> cc.getDomainsOfInfluence().stream())
						.flatMap(doi -> doi.getElections().stream())
						.flatMap(e -> e.getLists().stream())
						.filter(l -> !MathHelper.isEulerCriterionValid(l.getPrimeNumber(), p))
						.map(CandidateList::getPrimeNumber)
						.collect(Collectors.toList()));

		//candidates
		errors.addAll(
				electionEvent.getBallotBoxes().parallelStream()
						.flatMap(bb -> bb.getCountingCircles().stream())
						.flatMap(cc -> cc.getDomainsOfInfluence().stream())
						.flatMap(doi -> doi.getElections().stream())
						.flatMap(e -> e.getLists().stream())
						.flatMap(l -> l.getCandidatePositions().stream())
						.flatMap(cp -> cp.getPrimeNumbers().stream())
						.filter(v -> !MathHelper.isEulerCriterionValid(v, p))
						.collect(Collectors.toList()));

		//candidates without lists
		errors.addAll(
				electionEvent.getBallotBoxes().stream()
						.flatMap(bb -> bb.getCountingCircles().stream())
						.flatMap(cc -> cc.getDomainsOfInfluence().stream())
						.flatMap(doi -> doi.getElections().stream())
						.flatMap(e -> e.getCandidates().stream())
						.flatMap(c -> c.getPrimeNumbers().stream())
						.filter(v -> !MathHelper.isEulerCriterionValid(v, p))
						.collect(Collectors.toList()));

		if (!errors.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification06.nok.message",
							errors.toString()));
		}

		return VerificationResultEvent.success(this, getVerificationDefinition());
	}
}
