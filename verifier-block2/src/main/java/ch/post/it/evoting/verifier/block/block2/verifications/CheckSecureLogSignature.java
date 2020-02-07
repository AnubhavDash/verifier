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
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundle;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundleCertificates;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundleCreator;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import reactor.core.publisher.Flux;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class CheckSecureLogSignature extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(2);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "verification02.description"));
        def.setId(2);
        def.setName("checkSecureLogSignature");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) {
        VerificationResult result = new VerificationResult();

        Map<String, SecureLogBundleCertificates> mapCertificates = SecureLogBundleCertificates.loadAllHostsBundleCertificates(inputDirectoryPath);
        File[] hosts = PathHelper.listDirectories(inputDirectoryPath.resolve(Block2VerificationSuite.PATH_SECURE_LOGS));

        VerificationFailureException ex = Flux.fromArray(hosts)
                .onErrorStop()
                .flatMap(hostDir -> Flux.fromArray(PathHelper.listDirectories(hostDir.toPath())))
                .flatMap(instanceDir -> Flux.fromArray(PathHelper.listDirectories(instanceDir.toPath())))
                .map(SecureLogEntry.loadLogDirectory)
                .flatMap(flux -> SecureLogBundleCreator.from(flux, mapCertificates))
                .switchIfEmpty(Flux.<SecureLogBundle>empty().doOnComplete(() -> {throw new RuntimeException("No secureLog bundle found");}))
                .map(b -> Optional.ofNullable(b.validateSignature()
                        ? null
                        : buildVerificationFailureException(
                                "Checkpoint entry and attributes of the entry, the signature does not verify",
                                Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
                                "verification02.nok.message",
                                b.getEndCheckPoint().getRaw()
                        )
                ))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .blockFirst();

        if (ex != null)
            throw ex;

        result.setStatus(Status.OK);
        return result;
    }
}
