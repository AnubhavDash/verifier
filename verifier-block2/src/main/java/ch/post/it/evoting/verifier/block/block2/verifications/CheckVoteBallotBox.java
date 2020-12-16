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
package ch.post.it.evoting.verifier.block.block2.verifications;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationDataExtractor;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationStruct;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.Ballot;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class CheckVoteBallotBox extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(2);
		def.setCategory(Category.CONSISTENCY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "verification05.description"));
		def.setId(5);
		def.setName("checkVoteBallotBox");
		def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		VoterInformationStruct voterInformation = VoterInformationDataExtractor.getInfo(inputDirectoryPath);

		// Count in the logs
		final Pattern patternEncryptedOption = Pattern.compile(".*\\|000\\|(.*)\\|.*\\|.*\\|#encryptedOptions=\"(.*)\" #ccx_id=.*\n");
		final Pattern pattern = Pattern.compile("\\|VOTVAL\\|-\\|.*\\|" + voterInformation.getEeid() + "\\|");
		Map<String, String> mapSecureLogs = SecureLogEntry.loadRegularLogs(inputDirectoryPath, pattern).map(s1 -> {
			Matcher matcher = patternEncryptedOption.matcher(s1.getRaw());
			matcher.matches();
			return Tuples.of(matcher.group(1), matcher.group(2));
		}).collectMap(Tuple2::getT1, Tuple2::getT2).block();

		// for all ballotbox
		// get downloadedBallotBox.csv --> votingCardId, encryptedOptions
		// foreach mapDownloadedBallotBox
		// check that mapDownloadedBallotBox[votingCardId] == mapSecuredLogs[votingCardId]
		Map<String, String> mapDownloadedBallotBoxs = new HashMap<>();

		final PathNode ballotIdDirsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
		for (Path regexPath : ballotIdDirsPathNode.getRegexPaths()) {
			final PathNode ballotBoxFilePathNode = pathService.buildFromDynamicAncestorPath(StructureKey.DOWNLOADED_BALLOT_BOX, regexPath);

			try (Stream<String> lines = Files.lines(ballotBoxFilePathNode.getPath())) {
				Map<String, String> map = lines.map(l -> {
					try {
						return extractFromLine(l);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}).filter(entry -> entry.getKey() != null)
						.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
				if (!mergeMapWithoutDuplicates(mapDownloadedBallotBoxs, map)) {
					throw buildVerificationFailureException("Duplicate votingCardId in downloadedBallotBox files",
							Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "verification05.nok.duplicate.message");
				}
			}
		}

		// Check sizes
		if (mapDownloadedBallotBoxs.size() != mapSecureLogs.size()) {
			throw buildVerificationFailureException("The number of encrypted votes in the secure logs and downloaded boxes are not equal",
					Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "verification05.nok.numberVotes.mismatch.message");
		}

		mapDownloadedBallotBoxs.entrySet().stream().forEach(entry -> {
			String key = entry.getKey();
			String value = entry.getValue();
			if (mapSecureLogs.containsKey(key)) {
				if (!value.equals(mapSecureLogs.get(key))) {
					throw buildVerificationFailureException("EncryptedOptions is not the same in DownloadedBallotBox and SecureLogs",
							Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "verification05.nok.encryptedOptions.mismatch.message", key, value);
				}
			} else {
				throw buildVerificationFailureException("Unknown votingCardId", Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification05.nok.unknow.votingCardId.message", key);
			}
		});

		result.setStatus(Status.OK);
		return result;
	}

	private static boolean mergeMapWithoutDuplicates(Map<String, String> outputMap, Map<String, String> inputMap) {
		try {
			inputMap.forEach((key, value) -> {
				if (outputMap.containsKey(key)) {
					throw new IllegalArgumentException("outputMap already contains key");
				} else {
					outputMap.put(key, value);
				}
			});
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private static AbstractMap.SimpleEntry<String, String> extractFromLine(String line) throws IOException {
		if (!line.isEmpty() && line.contains("}}|")) {
			line = line.substring(0, line.indexOf("}}|") + 2);
			Ballot ballot = Deserializer.fromJson(TypeConverter.stringToByte(line), Ballot.class);
			String joinedValues = ballot.getVote().getEncryptedOptions().stream().map(String::valueOf).collect(Collectors.joining(";"));
			return new AbstractMap.SimpleEntry(ballot.getVote().getVotingCardId().toString().replaceAll("-", ""), joinedValues);
		} else {
			return new AbstractMap.SimpleEntry(null, null);
		}
	}

}
