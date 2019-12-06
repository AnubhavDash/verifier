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
package ch.post.it.evoting.verifier.block.block3.verifications;

import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.block.block3.loader.online.OnlineMixingProofLoader;
import ch.post.it.evoting.verifier.block.block3.scytl.loader.OnlineDataLoader;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.Verification;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.decrypt.DecryptVerifier;
import com.scytl.products.ov.mixnet.commons.exceptions.VerifierException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckDecryptionProofOnline extends Verification {

    private static final Logger LOGGER = Logger.getLogger(CheckDecryptionProofOnline.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(3);
        def.setCategory(Category.COMPLETENESS);
        def.setId(27);
        def.setName("checkDecryptionProofOnline");
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "test27.description"));
        def.addVerificationTrait(VerificationTrait.PreDecryption);
        return def;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        try {
            File[] ballotBoxes = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3VerificationSuite.PATH_BALLOTBOXES));
            File[] ccMixingKeys = PathHelper.getFiles(inputDirectory.toPath().resolve(Block3VerificationSuite.PATH_CC_MIXING_KEYS).toFile(), "cc.*_mixing_.*key.*\\.json");

            for (File ballotBox : ballotBoxes) {
                final File[] onlineMixings = ballotBox.listFiles(((dir, name) -> name.matches(".*ccn_m.?\\.json")));
                if(onlineMixings.length != 3 ){
                    throw new VerifierException("the number of control components expected is 3 but actual is " + onlineMixings.length);
                }
                for (File onlineMixing : onlineMixings) {
                    //for this onlineMixing, so for this ccn , get the correct ccX_mixing_public_key.json file
                    File pkJsonFile = getPkJsonFile(onlineMixing.getName(), ccMixingKeys);
                    OnlineDataLoader onlineDataLoader = new OnlineMixingProofLoader(onlineMixing.toPath());
                    int verificationResultCode = DecryptVerifier.verifyOnline(pkJsonFile, onlineDataLoader);
                    if (verificationResultCode != 1 && verificationResultCode != -1) {
                        throw new VerificationFailureException("The verification failed", ballotBox.getName());
                    }
                }
            }
            result.setStatus(Status.OK);
        } catch (VerificationFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "test07.nok.message", ((VerificationFailureException) e).getArgs()[1]));
        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "test07.file.not.found.message", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
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
