/**
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
import org.apache.log4j.Logger;

import java.io.File;
import java.util.AbstractMap;

public class CheckProductArgumentOnline extends AbstractVerification {

    private static final Logger LOGGER = Logger.getLogger(CheckProductArgumentOnline.class);
    private final BGOnlineVerificationProcessor processor = BGOnlineVerificationProcessor.getInstanceAndRegister(this);

    @Override
    public VerificationDefinition getVerificationDefinition() {

        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(3);
        def.setCategory(Category.COMPLETENESS);
        def.setId(22);
        def.setName("checkProductArgumentOnline");
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification22.description"));
        def.addVerificationTrait(VerificationTrait.PreDecryption);
        return def;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        try {
            processor.register(this);
            processor.executeProcess(inputDirectory.toPath().resolve(Block3VerificationSuite.PATH_BALLOTBOXES));

            AbstractMap.SimpleEntry<Status, String> status = processor.getStatus(TestType.ProductProof);
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
