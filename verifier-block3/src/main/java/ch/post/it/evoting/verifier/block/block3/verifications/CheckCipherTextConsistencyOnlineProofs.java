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
package ch.post.it.evoting.verifier.block.block3.verifications;

import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.block.block3.loader.online.OnlineMixingProofLoader;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.Ballot;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.dto.DownloadedBallot;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckCipherTextConsistencyOnlineProofs extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition verificationDefinition = new VerificationDefinition();
        verificationDefinition.setBlockId(3);
        verificationDefinition.setCategory(Category.COMPLETENESS);
        verificationDefinition.setId(31);
        verificationDefinition.setName("checkCipherTextConsistencyOnlineProofs");
        verificationDefinition.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification31.description"));

        return verificationDefinition;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        PathNode ballotBoxIdDirectoriesPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

        for (Path ballotBoxIdDirectoryPath : ballotBoxIdDirectoriesPathNode.getRegexPaths()) {

            // Downloaded ballot
            PathNode downloadedBallotBoxPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.DOWNLOADED_BALLOT_BOX, ballotBoxIdDirectoryPath);
            List<GammaPhis> offlineGammaPhisList;
            try (Stream<String> lines = Files.lines(downloadedBallotBoxPathNode.getPath())) {
                offlineGammaPhisList = lines
                        .map(l -> {
                            try {
                                return extractFromLine(l);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(entry -> entry.getGamma() != null)
                        .collect(Collectors.toList());
            }

            // Online mixing
            PathNode onlineMixingPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX_ONLINE_MIXING, ballotBoxIdDirectoryPath);
            Map<String, Tuple2<List<GammaPhis>, List<GammaPhis>>> map = new HashMap<>();
            List<GammaPhis> onlinePreviousVotes;
            List<GammaPhis> onlineVotes;

            for (Path onlineMixingPath : onlineMixingPathNode.getRegexPaths()) {
                OnlineMixingProofLoader onlineMixingProofLoader = new OnlineMixingProofLoader(onlineMixingPath);

                onlinePreviousVotes = onlineMixingProofLoader.getEncryptedBallots().getBallots()
                        .stream()
                        .map(b -> new GammaPhis(b.getGamma().getValue().toString(),
                                b.getPhis().stream().map(p -> p.getValue().toString()).collect(Collectors.toList())))
                        .collect(Collectors.toList());

                onlineVotes = onlineMixingProofLoader.getVotes().getBallots()
                        .stream()
                        .map(b -> new GammaPhis(b.getGamma().getValue().toString(),
                                b.getPhis().stream().map(p -> p.getValue().toString()).collect(Collectors.toList())))
                        .collect(Collectors.toList());


                Tuple2<List<GammaPhis>, List<GammaPhis>> tuple = Tuples.of(onlinePreviousVotes, onlineVotes);

                map.put(onlineMixingPath.getFileName().toString(), tuple);
            }

            // All data is loaded
            List<GammaPhis> votesToCheck = offlineGammaPhisList;

            // Business check
            int nbControlled = 0;
            if (map.size() == 3) {
                while (nbControlled != 3) {
                    int nbNotFound = 0;
                    for (Map.Entry<String, Tuple2<List<GammaPhis>, List<GammaPhis>>> tuple : map.entrySet()) {
                        if (isDowloadedOnlineEncryptedBallotsEquals(votesToCheck, tuple.getValue().getT1())) {
                            votesToCheck = tuple.getValue().getT2();
                            nbControlled++;
                            break;
                        }
                        nbNotFound++;
                        if (nbNotFound == 3) {
                            throw buildVerificationFailureException(
                                    "Same vote not exist (vote non confirmé)",
                                    Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                                    "verification31.nok.message"
                            );
                        }
                    }
                }
            } else {
                throw buildVerificationFailureException(
                        "There aren't 3 online control component proof",
                        Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification31.nok.message"
                );
            }

        }

        result.setStatus(Status.OK);
        return result;
    }

    private boolean isDowloadedOnlineEncryptedBallotsEquals(List<GammaPhis> downloadedList, List<GammaPhis> onlineList) {
        return downloadedList.size() == onlineList.size() && Flux.fromIterable(downloadedList).all(e -> onlineList.contains(e)).block();
    }

    private GammaPhis extractFromLine(String line) throws IOException {
        int pipePosition = line.indexOf("}}|");
        // Take only confirmed votes searching votes without |||
        if (!line.isEmpty() && pipePosition != -1 && !line.substring(pipePosition + 2, pipePosition + 5).contains("|||")) {
            line = line.substring(0, pipePosition + 2);
            Ballot ballot = Deserializer.fromJson(TypeConverter.stringToByte(line), Ballot.class);
            List<BigInteger> encryptedOptions = ballot.getVote().getEncryptedOptions();
            String gamma = encryptedOptions.get(0).toString();
            List<String> phis = Arrays.asList(encryptedOptions.get(1).toString());
            return new GammaPhis(gamma, phis);
        } else {
            return new GammaPhis(null, null);
        }
    }

    static class GammaPhis {
        private String gamma;
        private List<String> phis;

        GammaPhis(String gamma, List<String> phis) {
            this.gamma = gamma;
            this.phis = phis;
        }

        public String getGamma() {
            return gamma;
        }

        public List<String> getPhis() {
            return phis;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (!(obj instanceof GammaPhis))
                return false;
            if (obj == this)
                return true;
            return (this.gamma.equals(((GammaPhis) obj).getGamma()) && this.phis.equals(((GammaPhis) obj).getPhis()));
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.gamma, this.phis);
        }
    }
}
