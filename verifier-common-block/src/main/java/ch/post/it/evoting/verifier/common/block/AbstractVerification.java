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
package ch.post.it.evoting.verifier.common.block;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public abstract class AbstractVerification {

    private static final Logger LOGGER = Logger.getLogger(AbstractVerification.class);
    private static final String RESOURCE_BUNDLE_NAME = "common/resources";

    public AbstractVerification() {
        //force having a non-arg constructor
    }

    public abstract VerificationDefinition getVerificationDefinition();

    public final VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult();
        try {
            result = verify(inputDirectory);
        }
        // Business exception
        catch (VerificationFailureException e) {
            LOGGER.info(e.getMessage());
            result.setStatus(Status.NOK);
            result.setMessage(e.getFailureMessage());
        }
        // File exception
        catch (FileNotFoundException | NoSuchFileException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(RESOURCE_BUNDLE_NAME, "common.error.file.not.found.message", e.getCause().getLocalizedMessage()));
        }
        // Unexpected error
        catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(RESOURCE_BUNDLE_NAME, "common.error.unexpected.message"));
        }
        return result;
    }

    protected abstract VerificationResult verify(File inputDirectory) throws Exception;

    protected final VerificationFailureException buildVerificationFailureException(String message, String resourceBundleName, String messageKey, String... details) {
        return new VerificationFailureException(
                message,
                TranslationHelper.getFromResourceBundle(resourceBundleName, messageKey, details)
        );
    }
}
