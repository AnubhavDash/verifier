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
package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.Block2TestSuite;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundleCertificates;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundleCreator;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.Map;
import java.util.Optional;

public class Test02 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test02.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(2);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test02.description"));
        def.setId(2);
        def.setName("checkSecureLogSignature");
        def.addTestTrait(TestTrait.PreDecryption);
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            Map<String, SecureLogBundleCertificates> mapCertificates = SecureLogBundleCertificates.loadAllHostsBundleCertificates(inputDirectory);

            File[] hosts = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block2TestSuite.PATH_SECURE_LOGS));
            TestFailureException ex = Flux.fromArray(hosts)
                    .onErrorStop()
                    .flatMap(hostDir -> Flux.fromArray(PathHelper.listDirectories(hostDir.toPath())))
                    .flatMap(instanceDir -> Flux.fromArray(PathHelper.listDirectories(instanceDir.toPath())))
                    .map(SecureLogEntry.loadLogDirectory)
                    .flatMap(flux -> SecureLogBundleCreator.from(flux, mapCertificates))
                    .map(b -> {
                        if (b.validateSignature()) {
                            return Optional.<TestFailureException>empty();
                        } else {
                            return Optional.of(new TestFailureException(b.getEndCheckPoint().getRaw()));
                        }
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .blockFirst();

            if (ex != null) {
                throw ex;
            }
            result.setStatus(Status.OK);
        } catch (TestFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test02.nok.message", e.getArgs()));
        } catch (Exception e) {
            LOGGER.error("an unexpected error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }
}
