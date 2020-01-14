/**
 * This file is part of Verifier Swiss Post.
 * <p>
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block3.verifications;

import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;

import java.io.File;

public class CheckIndependenceGenerators extends AbstractVerification {

    private static final Logger LOGGER = Logger.getLogger(CheckIndependenceGenerators.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(3);
        def.setCategory(Category.EVIDENCE);
        def.setId(9);
        def.setName("checkIndependenceGenerators");
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification09.description"));
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        return def;
    }

    @Override
    public VerificationResult verify(File inputDirectory) {
        VerificationResult result = new VerificationResult();

        try {
            result.setStatus(Status.NA);
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }

        return result;
    }
}
