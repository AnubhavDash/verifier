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
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.Verification;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.block.block2.securelog.HostMappingElement;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.util.Map;
import java.util.regex.Pattern;

public class CheckNumberChoiceReturnCodes extends Verification {

    private static final Logger LOGGER = Logger.getLogger(CheckNumberChoiceReturnCodes.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(2);
        def.setCategory(Category.CONSISTENCY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test03.description"));
        def.setId(3);
        def.setName("checkNumberChoiceReturnCodes");
        def.addVerificationTrait(VerificationTrait.PreDecryption);
        return def;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        try {
            //Get the voterInformation.csv Files and count
            VoterInformationStruct voterInformation = VoterInformationDataExtractor.getInfo(inputDirectory);

            // create host/CC mapping
            Map<String, String> hostCcMapping = HostMappingElement.loadHostMapping(inputDirectory);

            final Pattern pattern = Pattern.compile("\\|GENPCC\\|-\\|.*\\|" + voterInformation.getEeid() + "\\|");

            //count in the logs
            Map<String, Long> countByCC = SecureLogEntry.loadRegularLogs(inputDirectory, pattern)
                    .groupBy(s1 -> hostCcMapping.containsKey(s1.getHost()) ? hostCcMapping.get(s1.getHost()) : s1.getHost())
                    .flatMap(group -> {
                        String ccName = group.key();
                        return group.count().flux().map(count -> Tuples.of(ccName, count));
                    }).collectMap(Tuple2::getT1, Tuple2::getT2).block();

            if (countByCC == null) {
                throw new RuntimeException("no values found while counting log foreach control component");
            }
            if (countByCC.size() != 4) {
                throw new RuntimeException("more than 4 different CC found : " + countByCC.keySet());
            }
            long nbDistinctValues = countByCC.values().stream().distinct().count();
            if (nbDistinctValues == 0 && voterInformation.getCount() == 0L) {
                LOGGER.info("no GENPCC log found for the defined electionEventId : " + voterInformation.getEeid());
                result.setStatus(Status.NOK);
            } else if (nbDistinctValues != 1) {
                throw new VerificationFailureException("count of log for partial choice code generation is not the same for each control component", countByCC.values().toString());
            } else {
                //finally check the count with csv files count
                Long logCount = countByCC.values().stream().findFirst().get();
                if (logCount.equals(voterInformation.getCount())) {
                    result.setStatus(Status.OK);
                } else {
                    throw new VerificationFailureException("the number of log entries does not match with the number of voters", "" + logCount + " and " + voterInformation.getCount());
                }

            }

        } catch (NoSuchFileException e) {
            LOGGER.error("a NoSuchFileException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test03.file.not.found.message", e.getFile()));
        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test03.file.not.found.message", e.getMessage()));

        } catch (VerificationFailureException e) {
            String[] args = e.getArgs();
            if (args.length >= 2) {
                LOGGER.error("Test failed, cause : " + args[0] + ". Details : " + args[1]);
            }
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test03.nok.message"));
        } catch (Exception e) {
            LOGGER.error("an unexpected error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }

}
