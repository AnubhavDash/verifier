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
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;

import ch.post.it.evoting.cryptoprimitives.domain.VotingOptionsConstants;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.plugin.contract.AbstractVerification;
import ch.post.it.evoting.verifier.plugin.contract.Category;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.plugin.contract.VerificationTrait;
import ch.post.it.evoting.verifier.core.internal.dto.revised.DomainOfInfluence;
import ch.post.it.evoting.verifier.core.internal.dto.revised.VoteOption;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerifierEvent;

@Component
public class VerifyVotingOptions extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;

	public VerifyVotingOptions(final ElectionDataExtractionService extractionService,
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
				"verification03.description"));
		definition.setId(3);
		definition.setName("verifyVotingOptions");
		definition.addVerificationTrait(VerificationTrait.CONFIGURATION);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		// Get the encryption parameters.
		final BigInteger p = extractionService.getEncryptionParameters(inputDirectoryPath).getP();

		// Get the primes from the file.
		final ImmutableList<BigInteger> smallPrimeGroupMembers = extractionService.getPrimes(inputDirectoryPath);

		final ImmutableList<BigInteger> encodedVotingOptions = extractEncodedVotingOptions(inputDirectoryPath);

		if (verifyVotingOptions(p, smallPrimeGroupMembers, encodedVotingOptions)) {
			return VerificationResultEvent.success(this, getVerificationDefinition());
		} else {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message"));
		}

	}

	private ImmutableList<BigInteger> extractEncodedVotingOptions(final Path inputDirectoryPath) {
		final var electionEvent = extractionService.getElectionEvent(inputDirectoryPath);
		Function<DomainOfInfluence, Stream<BigInteger>> questionOptionsPrimes = domainOfInfluence -> domainOfInfluence.getVotes()
				.stream()
				.flatMap(vote -> vote.getQuestions().stream())
				.flatMap(voteQuestion -> voteQuestion.getOptions().stream())
				.map(VoteOption::getPrimeNumber);

		Function<DomainOfInfluence, Stream<BigInteger>> candidateOptionsPrimes = domainOfInfluence -> domainOfInfluence.getElections()
				.stream()
				.flatMap(election -> election.getCandidates().stream())
				.flatMap(candidate -> candidate.getPrimeNumbers().stream());

		Function<DomainOfInfluence, Stream<BigInteger>> writeInsPrimes = domainOfInfluence -> domainOfInfluence.getElections()
				.stream()
				.flatMap(election -> election.getWriteIns().stream());

		return electionEvent.getBallotBoxes()
				.stream()
				.flatMap(bb -> bb.getCountingCircles().stream())
				.flatMap(countingCircle -> countingCircle.getDomainsOfInfluence().stream())
				.flatMap(domainOfInfluence -> Streams.concat(
						questionOptionsPrimes.apply(domainOfInfluence),
						candidateOptionsPrimes.apply(domainOfInfluence),
						writeInsPrimes.apply(domainOfInfluence))
				).distinct()
				.sorted(BigInteger::compareTo)
				.collect(ImmutableList.toImmutableList());
	}

	/**
	 * Verifies the correctness of the voting options.
	 * <p>
	 * The voting options must correspond to the smallest prime group members and the product of the &phi; biggest voting options must be smaller than
	 * p.
	 * </p>
	 *
	 * @param smallPrimeGroupMembers <b>p</b>, a list of the &omega; smallest prime group members strictly greater than 3.
	 * @param encodedVotingOptions   <b>p</b>Tilde, a list of the voting options encoded as primes.
	 * @return true if the verification is successful, false otherwise
	 */
	@SuppressWarnings("java:S117")
	@VisibleForTesting
	boolean verifyVotingOptions(final BigInteger groupModulus, final ImmutableList<BigInteger> smallPrimeGroupMembers,
			final ImmutableList<BigInteger> encodedVotingOptions) {
		checkNotNull(groupModulus);
		checkNotNull(smallPrimeGroupMembers);
		checkNotNull(encodedVotingOptions);

		final long omega = VotingOptionsConstants.MAXIMUM_NUMBER_OF_VOTING_OPTIONS;
		final long phi = VotingOptionsConstants.MAXIMUM_NUMBER_OF_SELECTABLE_VOTING_OPTIONS;

		final BigInteger p = groupModulus;
		final ImmutableList<BigInteger> p_vector = smallPrimeGroupMembers;
		final ImmutableList<BigInteger> p_tilde = encodedVotingOptions;
		final int n = p_tilde.size();

		checkArgument(p_vector.size() == omega, String.format("The list of smallest prime group members must contain %d elements", omega));

		checkArgument(phi <= omega, "The supported number of selections must not be greater than the supported number of voting options");
		checkArgument(0 < n, "The number of voting options must be strictly greater than 0");
		checkArgument(n <= omega, "The number of voting options must not be greater than the supported number of voting options");

		// Operation.
		final Set<BigInteger> p_prime = Set.copyOf(p_vector.subList(0, n));
		final Set<BigInteger> p_tilde_prime = Set.copyOf(p_tilde);
		final boolean verifA = p_prime.equals(p_tilde_prime);
		final boolean verifB = p_vector.stream().skip(omega - phi).reduce(BigInteger.ONE, BigInteger::multiply).compareTo(p) < 0;

		return verifA && verifB;
	}
}
