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

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.CandidateList;
import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionParameters;
import ch.post.it.evoting.verifier.common.block.dto.revised.VoteOption;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class IsQuadraticResidueVO extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(1);
		def.setCategory(Category.INTEGRITY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification06.description"));
		def.setId(6);
		def.setName("isQuadraticResidue([vo])");
		def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		def.addVerificationTrait(VerificationTrait.BLOCK_1);
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		final PathNode encryptParams = pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);
		EncryptionParameters encryptionParameters = Deserializer.fromJson(encryptParams.getPath(), EncryptionParameters.class);
		BigInteger p = encryptionParameters.getP();

		final PathNode dataConfigPathNode = pathService.buildFromRootPath(StructureKey.DATA_CONFIG_UPDATED, inputDirectoryPath);
		ElectionEvent electionEvent = Deserializer.fromJson(dataConfigPathNode.getPath(), ElectionEvent.class);

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
			throw buildVerificationFailureException(
					"Euler criterion does not equal to 1",
					Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification06.nok.message",
					errors.toString()
			);
		}

		result.setStatus(Status.OK);
		return result;
	}
}
