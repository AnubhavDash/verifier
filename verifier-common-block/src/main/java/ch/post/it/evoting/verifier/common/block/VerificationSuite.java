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

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.VerifierBlock;

public abstract class VerificationSuite implements VerifierBlock {
	private static final Logger LOGGER = Logger.getLogger(VerificationSuite.class);
	private final String packagePrefix;
	private List<AbstractVerification> verifications;
	@Autowired
	private ApplicationContext appContext;

	protected VerificationSuite(String packagePrefix) {
		this.packagePrefix = packagePrefix;
	}

	@PostConstruct
	private void postConstruct() {
		Reflections reflections = new Reflections(packagePrefix);
		verifications = reflections.getSubTypesOf(AbstractVerification.class).stream().map(c -> {
			try {
				AbstractVerification verification = appContext.getAutowireCapableBeanFactory().createBean(c);
				return verification;
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				throw new RuntimeException("Unable to instantiate the verifications", e);
			}
		}).filter(i -> !i.getVerificationDefinition().isDeactivated()) // Exclude deactivated verifications
				.collect(Collectors.toList());
	}

	@Override
	public Stream<VerificationDefinition> getVerifications() {
		return verifications.stream().map(AbstractVerification::getVerificationDefinition);
	}

	@Override
	public Stream<VerificationResult> process(Path inputDirectoryPath, Set<VerificationTrait> options) {
		return verifications.stream().map(t -> {
			VerificationDefinition def = t.getVerificationDefinition();
			VerificationResult result = new VerificationResult(def);
			// Do skip the test if there are any defined restrictions
			// and the test trait does not match the restriction
			if ((options != null && !options.isEmpty()) && !def.containsAnyVerificationTrait(options)) {
				result.setStatus(Status.NA);
				return result;
			} else {
				VerificationResult verificationResult = t.executeVerification(inputDirectoryPath);
				result.setStatus(verificationResult.getStatus());
				result.setMessage(verificationResult.getMessage());
				return result;
			}
		});
	}
}
