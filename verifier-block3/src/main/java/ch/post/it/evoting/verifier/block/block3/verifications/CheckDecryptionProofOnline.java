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
import ch.post.it.evoting.verifier.block.block3.scytl.loader.OnlineDataLoader;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.decrypt.DecryptVerifier;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckDecryptionProofOnline extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(3);
        def.setCategory(Category.COMPLETENESS);
        def.setId(27);
        def.setName("checkDecryptionProofOnline");
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification27.description"));
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        return def;
    }

    @Override
    public VerificationResult verify(File inputDirectory) throws Exception {
        VerificationResult result = new VerificationResult();

        File[] ballotBoxes = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3VerificationSuite.PATH_BALLOTBOXES));
        File[] ccMixingKeys = PathHelper.getFiles(inputDirectory.toPath().resolve(Block3VerificationSuite.PATH_CC_MIXING_KEYS).toFile(), "cc.*_mixing_.*key.*\\.json");

        for (File ballotBox : ballotBoxes) {
            final File[] onlineMixings = ballotBox.listFiles(((dir, name) -> name.matches(".*ccn_m.?\\.json")));
            if (onlineMixings.length != 3) {
                throw buildVerificationFailureException(
                        "the number of control components expected is 3 but actual is " + onlineMixings.length,
                        Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification07.nok.message",
                        ballotBox.getName()
                );
            }
            for (File onlineMixing : onlineMixings) {
                //for this onlineMixing, so for this ccn , get the correct ccX_mixing_public_key.json file
                File pkJsonFile = getPkJsonFile(onlineMixing.getName(), ccMixingKeys);
                OnlineDataLoader onlineDataLoader = new OnlineMixingProofLoader(onlineMixing.toPath());
                int verificationResultCode = DecryptVerifier.verifyOnline(pkJsonFile, onlineDataLoader);
                if (verificationResultCode != 1 && verificationResultCode != -1) {
                    throw buildVerificationFailureException(
                            "The verification failed",
                            Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                            "verification07.nok.message",
                            ballotBox.getName()
                    );
                }
            }
        }

        result.setStatus(Status.OK);
        return result;
    }

    private File getPkJsonFile(String name, File[] ccMixingKeys) {
        Pattern pattern = Pattern.compile(".*ccn_m(.?)\\.json");
        Matcher matcher = pattern.matcher(name);
        matcher.matches();
        String id = matcher.group(1);
        return Arrays.stream(ccMixingKeys)
                .filter(file -> file.getName().contains(String.format("cc%s_mixing", id)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("PublicKey file not found"));
    }
}
