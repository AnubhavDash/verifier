/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block3.verifications;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.scytl.products.ov.mixnet.commons.exceptions.VerifierException;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;

import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.block.block3.loader.online.OnlineMixingProofLoader;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class CheckCommitmentParametersOnline extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(3);
		def.setCategory(Category.INTEGRITY);
		def.setId(28);
		def.setName("checkCommitmentParametersOnline");
		def.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification28.description"));
		def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		PathNode ballotBoxIdDirectoriesPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
		for (Path ballotBoxIdDirectoryPath : ballotBoxIdDirectoriesPathNode.getRegexPaths()) {

			// Get Online mixing files
			PathNode onlineMixingPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX_ONLINE_MIXING, ballotBoxIdDirectoryPath);
			if (onlineMixingPathNode.getRegexPaths().size() != 3) {
				throw new VerifierException(
						"the number of control components expected is 3 but actual is " + onlineMixingPathNode.getRegexPaths().size());
			}
			if (onlineMixingPathNode.getRegexPaths().size() == 0) {
				throw new FileNotFoundException("online mixing not found");
			}

			for (Path onlineMixingPath : onlineMixingPathNode.getRegexPaths()) {
				OnlineMixingProofLoader loader = new OnlineMixingProofLoader(onlineMixingPath);
				CommitmentParams commitmentParams = loader.getCommitmentParams();
				BigInteger p = loader.getZpGroup().getP();

				List<BigInteger> errors = Arrays.stream(commitmentParams.getG())
						.map(groupElement -> groupElement.getValue())
						.filter(bi -> !MathHelper.isEulerCriterionValid(bi, p)).collect(Collectors.toList());

				if (!errors.isEmpty()) {
					throw buildVerificationFailureException(
							"Commitment parameters verification failed",
							Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification28.nok.message",
							errors.toString()
					);

				}
			}
		}

		result.setStatus(Status.OK);
		return result;
	}
}
