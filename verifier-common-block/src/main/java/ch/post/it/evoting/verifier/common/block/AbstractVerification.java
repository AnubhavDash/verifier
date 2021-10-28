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
package ch.post.it.evoting.verifier.common.block;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureWrappedException;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;

import lombok.Getter;

public abstract class AbstractVerification {

	private static final Logger LOGGER = Logger.getLogger(AbstractVerification.class);
	private static final String RESOURCE_BUNDLE_NAME = "common/resources";

	@Autowired
	@Getter
	protected PathService pathService;

	// Force having a non-arg constructor.
	public AbstractVerification() {
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

	/**
	 * Constructs a VerificationFailureException.
	 *
	 * @param message			 the internal exception message to be used
	 * @param resourceBundleName the name of the properties file containing the public messages
	 * @param messageKey		 the property name for the public exception message. The message can be a format string.
	 * @param details			 arguments referenced by the format specifiers if the message is a format string. Optional.
	 * @return a {@link VerificationFailureException}
	 */
	protected static VerificationFailureException buildVerificationFailureException(String message, String resourceBundleName, String messageKey,
			String... details) {
		return new VerificationFailureException(message, TranslationHelper.getFromResourceBundle(resourceBundleName, messageKey, details));
	}

	private void logUnexpectedException(VerificationResult result, Exception e) {
		LOGGER.error("Unexpected error", e);
		result.setStatus(Status.UNEXPECTED_ERROR);
		result.setMessage(TranslationHelper.getFromResourceBundle(RESOURCE_BUNDLE_NAME, "common.error.unexpected.message"));
	}

	private void logIOException(VerificationResult result, IOException e) {
		LOGGER.error("a FileNotFoundException error occurred", e);
		result.setStatus(Status.FILE_ERROR);
		result.setMessage(TranslationHelper
				.getFromResourceBundle(RESOURCE_BUNDLE_NAME, "common.error.file.not.found.message", e.getLocalizedMessage()));
	}

	private void logVerificationFailure(VerificationResult result, VerificationFailureException e) {
		LOGGER.info(e.getMessage());
		result.setStatus(Status.NOK);
		result.setMessage(e.getFailureMessage());
	}

}
