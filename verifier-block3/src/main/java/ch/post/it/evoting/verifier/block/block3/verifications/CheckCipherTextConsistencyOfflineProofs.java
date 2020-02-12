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
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineEncryptedBallotsLoader;
import ch.post.it.evoting.verifier.block.block3.loader.online.OnlineMixingProofLoader;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.util.List;

public class CheckCipherTextConsistencyOfflineProofs extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition verificationDefinition = new VerificationDefinition();
        verificationDefinition.setBlockId(3);
        verificationDefinition.setCategory(Category.COMPLETENESS);
        verificationDefinition.setId(11);
        verificationDefinition.setName("checkCipherTextConsistencyOfflineProofs");
        verificationDefinition.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification11.description"));

        return verificationDefinition;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        PathNode ballotBoxIdDirectoriesPathNode = pathService.buildPathNode(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
        for (Path ballotBoxIdDirectoryPath : ballotBoxIdDirectoriesPathNode.getRegexPaths()) {

            // Online mixing files
            PathNode onlineMixingPathNode = pathService.buildFromDynamicPathNode(StructureKey.BALLOT_BOX_ONLINE_MIXING, ballotBoxIdDirectoryPath);

            // Offline encrypted ballots files
            PathNode ballotBoxOfflineDirectoriesPathNode = pathService.buildFromDynamicPathNode(StructureKey.BALLOT_BOX_OFFLINE_DIR, ballotBoxIdDirectoryPath);
            for (Path ballotBoxDirectoryPath : ballotBoxOfflineDirectoriesPathNode.getRegexPaths()) {
                OfflineEncryptedBallotsLoader offlineEncryptedBallotsLoader = new OfflineEncryptedBallotsLoader(ballotBoxDirectoryPath, inputDirectoryPath);
                ElGamalEncryptedBallots offlineEncryptedBallots = offlineEncryptedBallotsLoader.getEncryptedBallots();

                boolean isNotEquivalent = true;
                for (Path onlineMixingPath : onlineMixingPathNode.getRegexPaths()) {
                    OnlineMixingProofLoader onlineMixingProofLoader = new OnlineMixingProofLoader(onlineMixingPath);
                    ElGamalEncryptedBallots onlineEncryptedBallots = onlineMixingProofLoader.getVotes();

                    // Business check
                    if (isOfflineOnlineEncryptedBallotsEquals(offlineEncryptedBallots.getBallots(), onlineEncryptedBallots.getBallots())) {
                        isNotEquivalent = false;
                        break;
                    }
                }

                if (isNotEquivalent) {
                    throw buildVerificationFailureException(
                            "Same vote not exist",
                            Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                            "verification11.nok.message"
                    );
                }
            }
        }


        result.setStatus(Status.OK);
        return result;
    }

    private boolean isOfflineOnlineEncryptedBallotsEquals(List<ElGamalEncryptedBallot> offlineList, List<ElGamalEncryptedBallot> onlineList) {
        return offlineList.size() == onlineList.size() && Flux.fromIterable(offlineList).zipWith(Flux.fromIterable(onlineList)).all(t -> t.getT1().equals(t.getT2())).block();
    }
}
