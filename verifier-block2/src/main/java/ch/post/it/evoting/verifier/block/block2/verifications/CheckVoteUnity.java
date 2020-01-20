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

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationDataExtractor;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationStruct;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.block.block2.securelog.HostMappingElement;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CheckVoteUnity extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(2);
        def.setCategory(Category.EVIDENCE);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "verification06.description"));
        def.setId(6);
        def.setName("checkVoteUnity");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        VoterInformationStruct voterInformation = VoterInformationDataExtractor.getInfo(inputDirectoryPath);

        // create host/CC mapping
        Map<String, String> hostCcMapping = HostMappingElement.loadHostMapping(inputDirectoryPath);

        final Pattern patternVotingCardId = Pattern.compile(".*\\|000\\|(.*)\\|.*\\|.*\\|#encryptedOptions=\".*\" #ccx_id=.*\n");
        final Pattern pattern = Pattern.compile("\\|VOTVAL\\|-\\|.*\\|" + voterInformation.getEeid() + "\\|");
        Map<String, Map<String, Long>> nbVotingCardPerCC = SecureLogEntry.loadRegularLogs(inputDirectoryPath, pattern)
                .map(s1 -> {
                    Matcher matcher = patternVotingCardId.matcher(s1.getRaw());
                    matcher.matches();
                    String votingCardId = matcher.group(1);
                    return Tuples.of(s1.getHost(), votingCardId);
                })
                .groupBy(s1 -> hostCcMapping.containsKey(s1.getT1()) ? hostCcMapping.get(s1.getT1()) : s1.getT1())
                .flatMap(ccGroup -> {
                    return ccGroup.map(Tuple2::getT2).reduce(Collections.synchronizedMap(new HashMap<String, Long>()), (m, votingCardId) -> {
                        m.put(votingCardId, m.getOrDefault(votingCardId, 0L) + 1L);
                        return m;
                    }).map(m -> Tuples.of(ccGroup.key(), m));
                })
                .collectMap(Tuple2::getT1, Tuple2::getT2).block();

        List<String> problematicVotingCardIds = nbVotingCardPerCC.values().stream()
                .flatMap(m -> m.entrySet().stream())
                .filter(e -> e.getValue() > 1)
                .map(e -> e.getKey()).collect(Collectors.toList());

        if (!problematicVotingCardIds.isEmpty()) {
            throw buildVerificationFailureException(
                    "Voting Card Id contain multiple votes in the secure logs",
                    Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification06.nok.message",
                    problematicVotingCardIds.toArray(new String[]{})
            );
        }

        result.setStatus(Status.OK);
        return result;
    }
}
