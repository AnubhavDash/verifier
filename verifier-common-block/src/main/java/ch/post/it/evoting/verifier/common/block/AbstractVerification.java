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
package ch.post.it.evoting.verifier.common.block;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.config.SpringConfig;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureWrappedException;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;

public abstract class AbstractVerification {

    private static final Logger LOGGER = Logger.getLogger(AbstractVerification.class);
    private static final String RESOURCE_BUNDLE_NAME = "common/resources";

    protected PathService pathService;

    // Force having a non-arg constructor.
    public AbstractVerification() {
        // Init beans manually because the verifications are "new'ed".
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        pathService = context.getBean(PathService.class);
    }

    public abstract VerificationDefinition getVerificationDefinition();

    public final VerificationResult executeVerification(Path inputDirectoryPath) {
        VerificationResult result = new VerificationResult();

        try {
            result = verify(inputDirectoryPath);
        }
        // Wrapped business exception.
        catch (VerificationFailureWrappedException e) {
            if (e.getCause() instanceof VerificationFailureException) {
                logVerificationFailure(result, (VerificationFailureException) e.getCause());
            } else if (e.getCause() instanceof FileNotFoundException) {
                logIOException(result, (FileNotFoundException) e.getCause());
            } else if (e.getCause() instanceof NoSuchFileException) {
                logIOException(result, (NoSuchFileException) e.getCause());
            } else {
                logUnexpectedException(result, (Exception) e.getCause());
            }
        }
        // Business exception.
        catch (VerificationFailureException e) {
            logVerificationFailure(result, e);
        }
        // File exception.
        catch (FileNotFoundException | NoSuchFileException e) {
            logIOException(result, e);
        }
        // Unexpected error.
        catch (Exception e) {
            logUnexpectedException(result, e);
        }
        return result;
    }

    public abstract VerificationResult verify(Path inputDirectoryPath) throws Exception;

    protected final VerificationFailureException buildVerificationFailureException(String message, String resourceBundleName,
                                                                                   String messageKey, String... details) {
        return new VerificationFailureException(
                message,
                TranslationHelper.getFromResourceBundle(resourceBundleName, messageKey, details)
        );
    }

    public PathService getPathService() {
        return pathService;
    }

    private void logUnexpectedException(VerificationResult result, Exception e) {
        LOGGER.error("Unexpected error", e);
        result.setStatus(Status.NOK);
        result.setMessage(TranslationHelper.getFromResourceBundle(RESOURCE_BUNDLE_NAME, "common.error.unexpected.message"));
    }

    private void logIOException(VerificationResult result, IOException e) {
        LOGGER.error("a FileNotFoundException error occurred", e);
        result.setStatus(Status.NOK);
        result.setMessage(TranslationHelper.getFromResourceBundle(RESOURCE_BUNDLE_NAME, "common.error.file.not.found.message",
                e.getCause().getLocalizedMessage()));
    }

    private void logVerificationFailure(VerificationResult result, VerificationFailureException e) {
        LOGGER.info(e.getMessage());
        result.setStatus(Status.NOK);
        result.setMessage(e.getFailureMessage());
    }

}
