/**
 * This file is part of Verifier Swiss Post.
 * <p>
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block3.verifications;

import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.block.block3.loader.online.OnlineMixingProofLoader;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.products.ov.mixnet.commons.exceptions.VerifierException;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CheckCommitmentParametersOnline extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(3);
        def.setCategory(Category.INTEGRITY);
        def.setId(28);
        def.setName("checkCommitmentParametersOnline");
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification28.description"));
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        return def;
    }

    @Override
    public VerificationResult verify(File inputDirectory) throws Exception {
        VerificationResult result = new VerificationResult();

        File[] ballotBoxes = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3VerificationSuite.PATH_BALLOTBOXES));
        for (File ballotBox : ballotBoxes) {
            final File[] onlineMixings = ballotBox.listFiles(((dir, name) -> name.matches(".*ccn_m.?\\.json")));
            if (onlineMixings.length != 3) {
                throw new VerifierException("the number of control components expected is 3 but actual is " + onlineMixings.length);
            }
            if (onlineMixings.length == 0) {
                throw new FileNotFoundException("online mixing not found");
            } else {
                for (File file : onlineMixings) {

                    OnlineMixingProofLoader loader = new OnlineMixingProofLoader(file.toPath());
                    CommitmentParams commitmentParams = loader.getCommitmentParams();
                    BigInteger p = loader.getZpGroup().getP();

                    List<BigInteger> errors = Arrays.stream(commitmentParams.getG())
                            .map(groupElement -> groupElement.getValue())
                            .filter(bi -> !MathHelper.isEulerCriterionValid(bi, p)).collect(Collectors.toList());

                    if (!errors.isEmpty()) {
                        throw buildVerificationFailureException(
                                "Commitment parameters verification failed",
                                Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                                "verification28.nok.message",
                                errors.toString()
                        );

                    }
                }
            }
        }

        result.setStatus(Status.OK);
        return result;
    }
}
