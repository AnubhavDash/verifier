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
package ch.post.it.evoting.verifier.block.block2.verifications;

import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationDataExtractor;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationStruct;
import ch.post.it.evoting.verifier.block.block2.securelog.HostMappingElement;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class CheckNumberVoteCastCodes extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(2);
		def.setCategory(Category.CONSISTENCY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification04.description"));
		def.setId(4);
		def.setName("checkNumberVoteCastCodes");
		def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		// Get the voterInformation.csv Files and count
		VoterInformationStruct voterInformation = VoterInformationDataExtractor.getInfo(inputDirectoryPath);

		// Create host/CC mapping
		Map<String, String> hostCcMapping = HostMappingElement.loadHostMapping(inputDirectoryPath);

		// Count in the logs
		final Pattern pattern = Pattern.compile("\\|GENPVCC\\|-\\|.*\\|" + voterInformation.getEeid() + "\\|");
		Map<String, Long> countByCC = SecureLogEntry.loadRegularLogs(inputDirectoryPath, pattern)
				.groupBy(s1 -> hostCcMapping.containsKey(s1.getHost()) ? hostCcMapping.get(s1.getHost()) : s1.getHost())
				.flatMap(group -> {
					String ccName = group.key();
					return group.count().flux().map(count -> Tuples.of(ccName, count));
				}).collectMap(Tuple2::getT1, Tuple2::getT2).block();

		// Count by control component must not be null
		if (countByCC == null) {
			throw buildVerificationFailureException(
					"No values found while counting log foreach control component",
					Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification04.nok.no.value.message"
			);
		}

		// Count by control component must be 4
		if (countByCC.size() != 4) {
			throw buildVerificationFailureException(
					"Number of component control is not 4",
					Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification04.nok.number.control.component.message",
					String.valueOf(countByCC.size())
			);
		}

		// Distinct values
		long nbDistinctValues = countByCC.values().stream().distinct().count();
		if (nbDistinctValues == 0 && voterInformation.getCount() == 0L) {
			throw buildVerificationFailureException(
					"No GENPVCC log found for the defined electionEventId",
					Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification04.nok.no.genpvccc.log.found.message",
					voterInformation.getEeid()
			);
		} else if (nbDistinctValues != 1) {
			throw buildVerificationFailureException(
					"Count of log for partial vote cast code generation is not the same for each control component",
					Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification04.nok.count.control.component.message"
			);
		} else {
			// Finally check the count with csv files count
			Long logCount = countByCC.values().stream().findFirst().get();
			if (!logCount.equals(voterInformation.getCount())) {
				throw buildVerificationFailureException(
						"The number of log entries does not match with the number of voters",
						Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification04.nok.logs.mismatch.voters.message",
						String.valueOf(logCount), String.valueOf(voterInformation.getCount())
				);
			}
		}

		result.setStatus(Status.OK);
		return result;
	}
}
