/*
 * (c) Copyright 2024 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.backend.verifications;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet.toImmutableSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.dto.Verification;
import ch.post.it.evoting.verifier.backend.processor.VerifierProcessor;

@SpringBootTest
@ActiveProfiles("test")
class VerificationIdCoherenceTest {

	@Autowired
	ApplicationContext applicationContext;
	@Autowired
	VerifierProcessor verifierProcessor;

	@Test
	void validateIdFormat() {
		// given
		final Pattern idPattern = Pattern.compile("^\\d{1,2}\\.\\d{2}$");
		final Collection<AbstractVerification> verificationBeans = applicationContext.getBeansOfType(AbstractVerification.class).values();

		// when
		final ImmutableSet<String> invalidIds = verificationBeans.stream()
				.map(AbstractVerification::getVerificationDefinition)
				.map(VerificationDefinition::getId)
				.filter(id -> !idPattern.matcher(id).matches())
				.collect(toImmutableSet());

		// then
		assertThat(invalidIds).isEmpty();
	}

	@Test
	void validateIdsUnique() {
		// given
		final Set<String> state = new HashSet<>();
		final Collection<AbstractVerification> verificationBeans = applicationContext.getBeansOfType(AbstractVerification.class).values();

		// when
		final ImmutableSet<String> duplicates = verificationBeans.stream()
				.map(AbstractVerification::getVerificationDefinition)
				.map(VerificationDefinition::getId)
				.filter(id -> !state.add(id))
				.collect(toImmutableSet());

		// then
		assertThat(duplicates).isEmpty();
	}

	@Test
	void validateSortingIsCorrect() {
		// given
		final ImmutableList<Double> idsAsDoubles = verifierProcessor.getVerifications("").stream()
				.map(Verification::getVerificationId)
				.map(Double::valueOf)
				.collect(toImmutableList());
		final ImmutableList<Double> expected = idsAsDoubles.stream()
				.sorted()
				.collect(toImmutableList());

		// when / then
		assertThat(idsAsDoubles).containsExactlyElementsOf(expected);
	}
}
