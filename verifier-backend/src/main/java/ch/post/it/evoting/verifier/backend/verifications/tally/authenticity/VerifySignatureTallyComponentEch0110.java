/*
 * Copyright 2022 Post CH Ltd
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
package ch.post.it.evoting.verifier.backend.verifications.tally.authenticity;

import static com.google.common.base.Preconditions.checkState;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.security.SignatureException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.MoreCollectors;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.ech.xmlns.ech_0155._4.ExtensionType;
import ch.post.it.evoting.cryptoprimitives.domain.signature.Alias;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.hashable.HashableEch0110Factory;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.protocol.domain.ChannelSecurityContextData;

@Component
public class VerifySignatureTallyComponentEch0110 extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;
	private final SignatureVerification signatureVerification;

	protected VerifySignatureTallyComponentEch0110(
			final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService electionDataExtractionService,
			final SignatureVerification signatureVerification) {
		super(resultPublisherService);
		this.electionDataExtractionService = electionDataExtractionService;
		this.signatureVerification = signatureVerification;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.AUTHENTICITY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification.direct.trust.authenticity.description", "TallyComponentEch0110"));
		definition.setId(205);
		definition.setName("VerifySignatureTallyComponentEch0110");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		final Delivery delivery = electionDataExtractionService.getTallyComponentEch0110(inputDirectoryPath);

		final boolean verified = verifySignature(delivery);

		if (verified) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification.direct.trust.signature.fail", "TallyComponentEch0110"));
		}
	}

	@VisibleForTesting
	boolean verifySignature(final Delivery delivery) {

		final ExtensionType extension = delivery.getResultDelivery().getExtension();

		checkState(extension != null, "The tally component eCH-0110 file does not contain the expected extension.");

		final Element signatureElement = extension.getAny().stream()
				.map(Element.class::cast)
				.filter(element -> element.getTagName().equals("signature"))
				.collect(MoreCollectors.onlyElement());

		final String signatureContent = signatureElement.getTextContent();

		checkState(signatureContent != null, "The signature of the tally component eCH-0110 file is null.");
		checkState(!signatureContent.isBlank(), "The signature of the tally component eCH-0110 file is blank.");

		final byte[] signature;
		try {
			signature = new ObjectMapper().readValue(String.format("\"%s\"", signatureContent), byte[].class);
		} catch (final JsonProcessingException e) {
			throw new UncheckedIOException("Could not deserialize signature.", e);
		}

		final Hashable hash = HashableEch0110Factory.fromDelivery(delivery);
		final Hashable additionalContextData = ChannelSecurityContextData.tallyComponentEch0110();

		try {
			return signatureVerification.verifySignature(Alias.SDM_TALLY.toString(), hash, additionalContextData, signature);
		} catch (final SignatureException e) {
			throw new IllegalStateException("Could not verify the signature of the tally component eCH-0110 file.");
		}
	}
}

