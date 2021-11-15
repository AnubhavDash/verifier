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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.AbstractVerification;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.dto.revised.PublicKey;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;
import ch.post.it.evoting.verifier.dto.ElectoralAuthority;

@Component
public class IsMemberOfGroupPKEA extends AbstractVerification {

	private final PathService pathService;

	public IsMemberOfGroupPKEA(final PathService pathService, final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(1);
		definition.setCategory(Category.INTEGRITY);
		definition.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification07.description"));
		definition.setId(7);
		definition.setName("isMemberOfGroup(pk_ea)");
		definition.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		definition.addVerificationTrait(VerificationTrait.BLOCK_1);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		final var electoralAuthPathNode = pathService.buildFromRootPath(StructureKey.ELECTORAL_PUBLIC_KEY, inputDirectoryPath);
		final ElectoralAuthority electoralAuthority;
		try {
			electoralAuthority = Deserializer.fromJson(electoralAuthPathNode.getPath(), ElectoralAuthority.class);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize electoral authority.", e);
		}

		final String publicKeyB64 = electoralAuthority.getPublicKey();
		final byte[] decoded = TypeConverter.base64ToByte(publicKeyB64);
		final PublicKey publicKey;
		try {
			publicKey = Deserializer.fromJson(decoded, PublicKey.class);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize electoral public key.", e);
		}

		final BigInteger p = publicKey.getGroup().getP();
		final List<BigInteger> elements = publicKey.getKeys();
		if (elements.isEmpty()) {
			return VerificationResultEvent.failure(this, getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification07.nok.message.no.elements"));
		} else {
			List<String> errors = elements.stream()
					.filter(bigInteger -> !MathHelper.isEulerCriterionValid(bigInteger, p))
					.map(TypeConverter::bigIntegerToB64String)
					.collect(Collectors.toList());

			if (!errors.isEmpty()) {
				return VerificationResultEvent.failure(this, getVerificationDefinition(),
						TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification07.nok.message",
								errors.toString()));
			}
		}

		return VerificationResultEvent.success(this, getVerificationDefinition());
	}

}
