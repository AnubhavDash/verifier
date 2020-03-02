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
import ch.post.it.evoting.verifier.block.block3.scytl.loader.OnlineDataLoader;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import com.scytl.decrypt.DecryptVerifier;

import java.nio.file.Path;
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
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();


        PathNode ccMixingKeys = pathService.buildFromRootPath(StructureKey.CC_MIXING_KEYS, inputDirectoryPath);
        PathNode ballotBoxIdDirectoriesPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
        for (Path ballotBoxIdDirectoryPath : ballotBoxIdDirectoriesPathNode.getRegexPaths()) {

            // Online mixing
            PathNode onlineMixingPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX_ONLINE_MIXING, ballotBoxIdDirectoryPath);
            if (onlineMixingPathNode.getRegexPaths().size() != 3) {
                throw buildVerificationFailureException(
                        "the number of control components expected is 3 but actual is " + onlineMixingPathNode.getRegexPaths().size(),
                        Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification07.nok.message",
                        ballotBoxIdDirectoryPath.getFileName().toString()
                );
            }

            for (Path onlineMixingPath : onlineMixingPathNode.getRegexPaths()) {
                //for this onlineMixing, so for this ccn, get the correct ccX_mixing_public_key.json file
                Path pkJsonFile = getPublicKeyPath(onlineMixingPath.getFileName().toString(), onlineMixingPathNode, ccMixingKeys);
                OnlineDataLoader onlineDataLoader = new OnlineMixingProofLoader(onlineMixingPath);
                int verificationResultCode = DecryptVerifier.verifyOnline(pkJsonFile.toFile(), onlineDataLoader);
                if (verificationResultCode != 1 && verificationResultCode != -1) {
                    throw buildVerificationFailureException(
                            "The verification failed",
                            Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                            "verification07.nok.message",
                            ballotBoxIdDirectoryPath.getFileName().toString()
                    );
                }
            }
        }

        result.setStatus(Status.OK);
        return result;
    }

    private Path getPublicKeyPath(String name, PathNode online, PathNode ccMixingKeys) {
        String onlineId = extractId(online.getQualifier(), name);
        return ccMixingKeys.getRegexPaths().stream()
                .filter(path -> onlineId.equals(extractId(ccMixingKeys.getQualifier(), path.getFileName().toString())))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("PublicKey file not found"));
    }

    private String extractId(String regex, String fileName) {
        Matcher onlineMatcher = Pattern.compile(regex).matcher(fileName);
        onlineMatcher.matches();
        return onlineMatcher.group(1);
    }
}
