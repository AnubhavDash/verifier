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
package ch.post.it.evoting.verifier.block.block1.verifications;

import java.nio.file.Path;

import ch.evoting.xmlns.config._4.Configuration;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.CredentialDataElement;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class CheckNumberCredentials extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(1);
		def.setCategory(Category.COMPLETENESS);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification31.description"));
		def.setId(31);
		def.setName("checkNumberCredentials()");
		def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		def.addVerificationTrait(VerificationTrait.BLOCK_1);
		return def;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) throws Exception {
		final VerificationResult result = new VerificationResult();

		// Number of voters.
		final PathNode configPathNode = pathService.buildFromRootPath(StructureKey.CONFIG_ANONYMIZED, inputDirectoryPath);
		final Configuration configuration = Deserializer.fromXml(configPathNode.getPath(), Configuration.class);
		final int votersCount = configuration.getRegister().getVoter().size();

		// Number of lines.
		int linesCount = 0;
		final PathNode votingCardIdPathNode = pathService.buildFromRootPath(StructureKey.VOTING_CARD_SETS_ID_DIR, inputDirectoryPath);
		for (final Path path : votingCardIdPathNode.getRegexPaths()) {
			final PathNode credDataPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.CREDENTIAL_DATA, path);
			final Iterable<CredentialDataElement> iterable = Deserializer
					.fromCsv(credDataPathNode.getRegexPaths(), Deserializer.toCredentialDataElement);
			for (final CredentialDataElement ignored : iterable) {
				linesCount++;
			}
		}

		if (votersCount == linesCount) {
			result.setStatus(Status.OK);
		} else {
			throw buildVerificationFailureException(
					"The number of credentials and the number of expected voters do not match",
					Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification31.nok.message",
					String.valueOf(linesCount), String.valueOf(votersCount)
			);
		}
		return result;
	}
}
