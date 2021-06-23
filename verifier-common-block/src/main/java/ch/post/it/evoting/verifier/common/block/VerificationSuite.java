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
