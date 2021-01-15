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

import java.nio.file.Path;
import java.util.AbstractMap;

import org.apache.log4j.Logger;

import ch.post.it.evoting.verifier.block.block3.BGOfflineVerificationProcessor;
import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.block.block3.scytl.TestType;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class CheckSingleValueProductArgument extends AbstractVerification {

	private static final Logger LOGGER = Logger.getLogger(CheckSingleValueProductArgument.class);

	@Override
	public VerificationDefinition getVerificationDefinition() {

		VerificationDefinition verificationDefinition = new VerificationDefinition();
		verificationDefinition.setBlockId(3);
		verificationDefinition.setCategory(Category.COMPLETENESS);
		verificationDefinition.setId(5);
		verificationDefinition.setName("checkSingleValueProductArgument");
		verificationDefinition
				.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification05.description"));

		return verificationDefinition;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		final BGOfflineVerificationProcessor processor = BGOfflineVerificationProcessor.getInstanceAndRegister(this);
		try {
			PathNode ballotBoxesPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOXES_DIR, inputDirectoryPath);
			processor.executeProcess(ballotBoxesPathNode.getPath());

			AbstractMap.SimpleEntry<Status, String> status = processor.getStatus(TestType.SingleValueProductProof);
			result.setStatus(status.getKey());
			result.setMessage(TranslationHelper.getSameMessageMultiLanguage(status.getValue()));
		} finally {
			processor.unregister(this);
		}

		return result;
	}
}
