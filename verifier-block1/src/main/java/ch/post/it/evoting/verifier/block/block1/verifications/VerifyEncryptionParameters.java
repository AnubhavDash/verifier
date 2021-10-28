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
package ch.post.it.evoting.verifier.block.block1.verifications;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.nio.file.Path;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalService;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionParameters;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class VerifyEncryptionParameters extends AbstractVerification {

	private final ElGamalService elGamalService = new ElGamalService();

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(1);
		definition.setCategory(Category.INTEGRITY);
		definition.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification01.description"));
		definition.setId(1);
		definition.setName("verifyEncryptionParameters");
		definition.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		definition.addVerificationTrait(VerificationTrait.BLOCK_1);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) throws Exception {
		final var result = new VerificationResult();

		// Deserialize file.
		final var pathNode = pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);
		final var encryptionParameters = Deserializer.fromJson(pathNode.getPath(), EncryptionParameters.class);

		// Extract parameters.
		final BigInteger p = encryptionParameters.getP();
		final BigInteger q = encryptionParameters.getQ();
		final BigInteger g = encryptionParameters.getG();
		final String seed = encryptionParameters.getSeed();

		if (!verifyEncryptionParameters(p, q, g, seed)) {
			throw buildVerificationFailureException(
					"The provided encryption parameters do not match the computed ones.",
					Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification01.nok.message"
			);
		}

		result.setStatus(Status.OK);
		return result;
	}

	/**
	 * Verifies that the given encryption parameters are equal to the ones re-computed using the {@code seed}.
	 *
	 * @param pHat p&#770;, the p to validate. Must be non null.
	 * @param qHat q&#770;, the q to validate. Must be non null and satisfy p&#770; = 2 * q&#770; + 1.
	 * @param gHat g&#770;, the g to validate. Must be non null.
	 * @param seed the seed used to generate p&#770;, q&#770; and g&#770;.
	 * @return {@code true} if the provided parameters matches the re-computed ones, {@code false} otherwise.
	 * @throws NullPointerException     if any parameter is null.
	 * @throws IllegalArgumentException if p&#770; &#8800; 2 * q&#770; + 1.
	 */
	private boolean verifyEncryptionParameters(final BigInteger pHat, final BigInteger qHat, final BigInteger gHat, final String seed) {
		checkNotNull(pHat);
		checkNotNull(qHat);
		checkNotNull(gHat);
		checkNotNull(seed);
		checkArgument(pHat.compareTo(qHat.shiftLeft(1).add(BigInteger.ONE)) == 0, "p must be equal to 2 * q + 1.");

		// Operation.
		final var gqGroup = elGamalService.getEncryptionParameters(seed);
		final BigInteger p = gqGroup.getP();
		final BigInteger q = gqGroup.getQ();
		final BigInteger g = gqGroup.getGenerator().getValue();

		return p.equals(pHat) && q.equals(qHat) && g.equals(gHat);
	}
}
