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
package ch.post.it.evoting.verifier.block.block4.verifications;

import static ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper.getFromResourceBundle;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.evoting.xmlns.decrypt._1.Results;
import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.plugin.contract.AbstractVerification;
import ch.post.it.evoting.verifier.plugin.contract.Category;
import ch.post.it.evoting.verifier.plugin.contract.VerificationDefinition;
import ch.post.it.evoting.verifier.plugin.contract.VerificationTrait;
import ch.post.it.evoting.verifier.core.internal.dto.revised.BallotBox;
import ch.post.it.evoting.verifier.core.internal.dto.revised.CandidateList;
import ch.post.it.evoting.verifier.core.internal.dto.revised.CountingCircle;
import ch.post.it.evoting.verifier.core.internal.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.core.internal.exceptions.VerificationPreconditionException;
import ch.post.it.evoting.verifier.core.internal.tools.Deserializer;
import ch.post.it.evoting.verifier.core.internal.tools.TypeConverter;
import ch.post.it.evoting.verifier.core.internal.tools.path.PathService;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerifierEvent;

@Component
public class CheckOptionsMapping extends AbstractVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(CheckOptionsMapping.class);

	private final PathService pathService;

	public CheckOptionsMapping(final PathService pathService, final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(4);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification01.description"));
		definition.setId(1);
		definition.addVerificationTrait(VerificationTrait.FINAL_DECRYPTION);
		definition.setName("checkOptionsMapping");
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		// Get data config.
		final var dataConfigPathNode = pathService.buildFromRootPath(StructureKey.DATA_CONFIG_UPDATED, inputDirectoryPath);
		final ElectionEvent electionEvent;
		try {
			electionEvent = Deserializer.fromJson(dataConfigPathNode.getPath(), ElectionEvent.class);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize election event.", e);
		}
		final List<BallotBox> ballotBoxes = electionEvent.getBallotBoxes();

		// Get eVoting decrypt result.
		final var eVotingDecryptResultPathNode = pathService.buildFromRootPath(StructureKey.EVOTING_DECRYPT_RESULT, inputDirectoryPath);
		final Results decryptResult;
		try {
			decryptResult = Deserializer.fromXml(eVotingDecryptResultPathNode.getPath(), Results.class);
		} catch (IOException | JAXBException | XMLStreamException e) {
			throw new VerificationPreconditionException("Failed to deserialize evoting decrypt results.");
		}

		for (final BallotBox ballotBox : ballotBoxes) {
			// Prepare ballot box ids
			final var ballotBoxId = TypeConverter.UUIDToStringWithoutDash(ballotBox.getId());
			final var ballotBoxAuthId = TypeConverter.UUIDToStringWithoutDash(ballotBox.getAuthId());

			for (final CountingCircle countingCircle : ballotBox.getCountingCircles()) {

				final String countingCircleId = countingCircle.getId();
				// 1 Generate map<prime, alias>.
				// Votations.
				final Map<BigInteger, String> primeAliasMap = countingCircle.getDomainsOfInfluence().stream()
						.flatMap(doi -> doi.getVotes().stream())
						.flatMap(v -> v.getQuestions().stream())
						.flatMap(q -> q.getOptions().stream()
								.map(option -> new AbstractMap.SimpleEntry<>(option.getPrimeNumber(), option.getAlias().toString())))
						.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
				// Lists.
				primeAliasMap.putAll(countingCircle.getDomainsOfInfluence().stream()
						.flatMap(doi -> doi.getElections().stream())
						.flatMap(e -> e.getLists().stream())
						.collect(Collectors.toMap(CandidateList::getPrimeNumber, CandidateList::getAlias))
				);
				// Candidates.
				primeAliasMap.putAll(countingCircle.getDomainsOfInfluence().stream()
						.flatMap(doi -> doi.getElections().stream())
						.flatMap(e -> e.getLists().stream())
						.flatMap(l -> l.getCandidatePositions().stream())
						.flatMap(cp -> cp.getPrimeNumbers().stream()
								.map(prime -> new AbstractMap.SimpleEntry<>(prime, cp.getCandidateListId().toString())))
						.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
				);
				primeAliasMap.putAll(countingCircle.getDomainsOfInfluence().stream()
						.flatMap(doi -> doi.getElections().stream())
						.flatMap(e -> e.getCandidates().stream())
						.flatMap(c -> c.getPrimeNumbers().stream().map(prime -> new AbstractMap.SimpleEntry<>(prime, c.getAlias())))
						.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
				);

				// 2 Generate map<prime, count>, but before retrieve the ballotbox file.
				final var ballotBoxIdDirectoriesPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
				final Path ballotBoxIdDirectoryPath;
				ballotBoxIdDirectoryPath = ballotBoxIdDirectoriesPathNode.getRegexPath(ballotBoxId);
				final Map<String, Long> primesCountMap = getCorrectFileAndExtractPrimesCount(ballotBoxIdDirectoryPath);

				// 3 Generate map<alias, count>.
				final Map<String, Long> aliasCountMap = decryptResult.getBallotsBox().stream()
						.filter(bb -> ballotBoxAuthId.equals(bb.getBallotBoxIdentification()))
						.flatMap(theBb -> theBb.getCountingCircle().stream())
						.filter(cc -> countingCircleId.equals(cc.getCountingCircleIdentification()))
						.flatMap(theCc -> theCc.getDomainOfInfluence().stream())
						.flatMap(doi -> doi.getVote().stream())
						.flatMap(v -> v.getBallot().stream())
						.flatMap(b -> b.getChosenAnswerIdentification().stream())
						.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

				aliasCountMap.putAll(decryptResult.getBallotsBox().stream()
						.filter(bb -> ballotBoxAuthId.equals(bb.getBallotBoxIdentification()))
						.flatMap(theBb -> theBb.getCountingCircle().stream())
						.filter(cc -> countingCircleId.equals(cc.getCountingCircleIdentification()))
						.flatMap(theCc -> theCc.getDomainOfInfluence().stream())
						.flatMap(doi -> doi.getElection().stream())
						.flatMap(e -> e.getBallot().stream())
						.flatMap(b -> {
							final List<Stream<String>> coll = new LinkedList<>();
							if (b.getChosenCandidateListIdentification() != null) {
								coll.add(b.getChosenCandidateListIdentification().stream());
							}
							if (b.getChosenCandidateIdentification() != null) {
								coll.add(b.getChosenCandidateIdentification().stream());
							}
							if (b.getChosenWriteInsCandidateValue() != null) {
								coll.add(b.getChosenWriteInsCandidateValue().stream().map(s -> "#" + s));
							}
							if (b.getChosenListIdentification() != null) {
								coll.add(Stream.of(b.getChosenListIdentification()));
							}
							return coll.stream().flatMap(Function.identity());
						})
						.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));

				// Finally do the check.
				final List<String> failedOptions = aliasCountMap.entrySet().stream()
						.filter(entry -> {
							final String alias = entry.getKey();
							final Long aliasCount = entry.getValue();
							if (alias.startsWith("#")) {
								final Long nb = primesCountMap.entrySet().stream()
										.filter(e -> e.getKey().endsWith(alias))
										.mapToLong(e -> e.getValue() != null ? e.getValue() : 0L)
										.sum();
								return nb.equals(aliasCount);
							} else {
								Long nb = primeAliasMap.entrySet().stream()
										.filter(e -> e.getValue().equals(alias))
										.map(Map.Entry::getKey)
										.map(BigInteger::toString)
										.mapToLong(p -> primesCountMap.get(p) != null ? primesCountMap.get(p) : 0L)
										.sum();
								return nb.equals(aliasCount);
							}
						})
						.map(Map.Entry::getKey)
						.collect(Collectors.toList());

				if (!failedOptions.isEmpty()) {
					LOGGER.warn("The occurrences are different in decryptedBallots.csv and evoting-decrypt.csv for the following options: {}",
							failedOptions);
					return VerificationResultEvent.failure(this, getVerificationDefinition(),
							getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification01.nok.message"));
				}
			}
		}

		return VerificationResultEvent.success(this, getVerificationDefinition());
	}

	private Map<String, Long> getCorrectFileAndExtractPrimesCount(Path ballotBoxIdDirectoryPath) {
		final var decompressedVotesPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.DECOMPRESSED_VOTES, ballotBoxIdDirectoryPath);
		final Iterable<List<String>> iterable = Deserializer.fromCsv(decompressedVotesPathNode.getPath(), ";", Arrays::asList);

		return StreamSupport.stream(iterable.spliterator(), false)
				.flatMap(Collection::stream)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}

}
