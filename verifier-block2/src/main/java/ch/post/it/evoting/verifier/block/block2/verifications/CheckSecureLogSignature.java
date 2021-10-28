/*
 * Copyright 2021 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.block.block2.verifications;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundle;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundleCertificates;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundleCreator;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

import reactor.core.publisher.Flux;

public class CheckSecureLogSignature extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(2);
		def.setCategory(Category.AUTHENTICITY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification02.description"));
		def.setId(2);
		def.setName("checkSecureLogSignature");
		def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		Map<String, SecureLogBundleCertificates> mapCertificates =
				SecureLogBundleCertificates.loadAllHostsBundleCertificates(inputDirectoryPath);

		final PathNode secureLogsPathNode = pathService.buildFromRootPath(StructureKey.SECURE_LOGS_DIR, inputDirectoryPath);

		VerificationFailureException ex = Flux.fromIterable(secureLogsPathNode.getSubDirectories())
				.onErrorStop()
				.flatMap(hostDir -> Flux.fromArray(PathHelper.listDirectories(hostDir)))
				.flatMap(instanceDir -> Flux.fromArray(PathHelper.listDirectories(instanceDir.toPath())))
				.map(SecureLogEntry.loadLogDirectory)
				.flatMap(flux -> SecureLogBundleCreator.from(flux, mapCertificates))
				.switchIfEmpty(Flux.<SecureLogBundle>empty().doOnComplete(() -> {
					throw new RuntimeException("No secureLog bundle found");
				}))
				.map(b -> Optional.ofNullable(b.validateSignature() ? null :
						buildVerificationFailureException(
								"Checkpoint entry and attributes of the entry, the signature does not verify",
								Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
								"verification02.nok.message",
								b.getEndCheckPoint().getRaw()
						)
				))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.blockFirst();

		if (ex != null) {
			throw ex;
		}

		result.setStatus(Status.OK);
		return result;
	}
}
