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

import ch.post.it.evoting.verifier.block.block3.BGOnlineVerificationProcessor;
import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.block.block3.scytl.TestType;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.util.AbstractMap;

public class CheckMultiExponentationArgumentOnline extends AbstractVerification {

    private static final Logger LOGGER = Logger.getLogger(CheckMultiExponentationArgumentOnline.class);
    private final BGOnlineVerificationProcessor processor = BGOnlineVerificationProcessor.getInstanceAndRegister(this);

    @Override
    public VerificationDefinition getVerificationDefinition() {

        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(3);
        def.setCategory(Category.COMPLETENESS);
        def.setId(26);
        def.setName("checkMultiExponentationArgumentOnline");
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification26.description"));
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) {
        VerificationResult result = new VerificationResult();

        try {
            processor.register(this);
            PathNode ballotBoxesPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOXES_DIR, inputDirectoryPath);
            processor.executeProcess(ballotBoxesPathNode.getPath());

            AbstractMap.SimpleEntry<Status, String> status = processor.getStatus(TestType.MultiExponentiationProof);
            result.setStatus(status.getKey());
            result.setMessage(TranslationHelper.getSameMessageMultiLanguage(status.getValue()));
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        } finally {
            processor.unregister(this);
        }

        return result;
    }
}
