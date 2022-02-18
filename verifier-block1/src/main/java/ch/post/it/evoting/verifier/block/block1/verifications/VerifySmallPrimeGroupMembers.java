/*
 * Copyright 2022 Post CH Ltd
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
package ch.post.it.evoting.verifier.block.block1.verifications;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.stream.IntStream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import ch.post.it.evoting.cryptoprimitives.domain.VotingOptionsConstants;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupElement;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.plugin.contract.AbstractVerification;
import ch.post.it.evoting.verifier.plugin.contract.Category;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.plugin.contract.VerificationTrait;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerifierEvent;

@Component
public class VerifySmallPrimeGroupMembers extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;

	VerifySmallPrimeGroupMembers(final ElectionDataExtractionService extractionService,
			final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(1);
		definition.setCategory(Category.INTEGRITY);
		definition.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification02.description"));
		definition.setId(2);
		definition.setName("verifySmallPrimeGroupMembers");
		definition.addVerificationTrait(VerificationTrait.CONFIGURATION);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		// Get the encryption parameters.
		final var group = extractionService.getEncryptionParameters(inputDirectoryPath);

		// Get the primes from the file.
		final ImmutableList<BigInteger> smallPrimeGroupMembers = extractionService.getPrimes(inputDirectoryPath);

		if (verifySmallPrimeGroupMembers(group, smallPrimeGroupMembers)) {
			return VerificationResultEvent.success(this, getVerificationDefinition());
		} else {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification02.nok.message"));
		}
	}

	/**
	 * Verifies that the given prime numbers correspond to the small prime group members.
	 *
	 * @param group                  the group to which the prime numbers belong.
	 * @param smallPrimeGroupMembers <b>p</b>, a list of the &omega; smallest prime group members strictly greater than 3, in ascending order.
	 * @return true if the given prime numbers are indeed the smallest &omega; prime group members, false otherwise
	 */
	@SuppressWarnings("java:S117")
	private boolean verifySmallPrimeGroupMembers(final GqGroup group, final ImmutableList<BigInteger> smallPrimeGroupMembers) {
		checkNotNull(group);
		checkNotNull(smallPrimeGroupMembers);

		final int omega = VotingOptionsConstants.MAXIMUM_NUMBER_OF_VOTING_OPTIONS;

		checkArgument(smallPrimeGroupMembers.size() == omega, String.format("The list of small prime group members must contain %d elements", omega));
		checkArgument(IntStream.range(1, omega)
				.mapToObj(i -> smallPrimeGroupMembers.get(i - 1).compareTo(smallPrimeGroupMembers.get(i)) < 0)
				.allMatch(Boolean::booleanValue), "The list of small prime group members must be sorted in ascending order");
		final ImmutableList<BigInteger> p = smallPrimeGroupMembers;

		final ImmutableList<BigInteger> p_prime = group.getSmallPrimeGroupMembers(omega).stream()
				.map(GroupElement::getValue)
				.collect(ImmutableList.toImmutableList());
		return p_prime.equals(p);
	}
}
