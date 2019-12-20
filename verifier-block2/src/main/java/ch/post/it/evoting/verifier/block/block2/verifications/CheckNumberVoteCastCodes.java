/**
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

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationDataExtractor;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationStruct;
import ch.post.it.evoting.verifier.block.block2.securelog.HostMappingElement;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

public class CheckNumberVoteCastCodes extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(2);
        def.setCategory(Category.CONSISTENCY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "verification04.description"));
        def.setId(4);
        def.setName("checkNumberVoteCastCodes");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        return def;
    }

    @Override
    public VerificationResult verify(File inputDirectory) throws Exception {
        VerificationResult result = new VerificationResult();

        // Get the voterInformation.csv Files and count
        VoterInformationStruct voterInformation = VoterInformationDataExtractor.getInfo(inputDirectory);

        // Create host/CC mapping
        Map<String, String> hostCcMapping = HostMappingElement.loadHostMapping(inputDirectory);

        // Count in the logs
        final Pattern pattern = Pattern.compile("\\|GENPVCC\\|-\\|.*\\|" + voterInformation.getEeid() + "\\|");
        Map<String, Long> countByCC = SecureLogEntry.loadRegularLogs(inputDirectory, pattern)
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
