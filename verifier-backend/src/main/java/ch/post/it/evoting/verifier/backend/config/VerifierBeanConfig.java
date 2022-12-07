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
package ch.post.it.evoting.verifier.backend.config;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalFactory;
import ch.post.it.evoting.cryptoprimitives.hashing.Hash;
import ch.post.it.evoting.cryptoprimitives.hashing.HashFactory;
import ch.post.it.evoting.cryptoprimitives.mixnet.Mixnet;
import ch.post.it.evoting.cryptoprimitives.mixnet.MixnetFactory;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureFactory;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProofFactory;
import ch.post.it.evoting.verifier.backend.tools.KeystoreRepository;
import ch.post.it.evoting.verifier.backend.tools.XmlFileRepository;
import ch.post.it.evoting.verifier.backend.verifications.setup.consistency.VerifyPrimesMappingTableConsistencyAlgorithm;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.IntegerToWriteInAlgorithm;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.QuadraticResidueToWriteInAlgorithm;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.DecodeVotingOptionsAlgorithm;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.DecodeWriteInsAlgorithm;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.IsWriteInOptionAlgorithm;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.VerifyMixDecOfflineAlgorithm;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.VerifyVotingClientProofsAlgorithm;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixonline.GetMixnetInitialCiphertextsAlgorithm;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.Results;

@Configuration
public class VerifierBeanConfig {

	@Bean
	public Mixnet mixnet() {
		return MixnetFactory.createMixnet();
	}

	@Bean
	public ZeroKnowledgeProof zeroKnowledgeProof() {
		return ZeroKnowledgeProofFactory.createZeroKnowledgeProof();
	}

	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		return DomainObjectMapper.getNewInstance();
	}

	@Bean
	public Hash hash() {
		return HashFactory.createHash();
	}

	@Bean
	public ElGamal elGamal() {
		return ElGamalFactory.createElGamal();
	}

	@Bean
	public VerifyMixDecOfflineAlgorithm verifyMixDecOfflineAlgorithm(
			final ElGamal elGamal,
			final Mixnet mixnet,
			final ZeroKnowledgeProof zeroKnowledgeProof) {
		return new VerifyMixDecOfflineAlgorithm(elGamal, mixnet, zeroKnowledgeProof);
	}

	@Bean
	public VerifyVotingClientProofsAlgorithm verifyVotingClientProofsAlgorithm(final ZeroKnowledgeProof zeroKnowledgeProof) {
		return new VerifyVotingClientProofsAlgorithm(zeroKnowledgeProof);
	}

	@Bean
	public VerifyPrimesMappingTableConsistencyAlgorithm verifyPrimesMappingTableConsistencyAlgorithm() {
		return new VerifyPrimesMappingTableConsistencyAlgorithm();
	}

	@Bean
	public GetMixnetInitialCiphertextsAlgorithm getMixnetInitialCiphertextsAlgorithm(final ElGamal elGamal) {
		return new GetMixnetInitialCiphertextsAlgorithm(elGamal);
	}

	@Bean
	public DecodeVotingOptionsAlgorithm decodeVotingOptionsAlgorithm() {
		return new DecodeVotingOptionsAlgorithm();
	}

	@Bean
	public DecodeWriteInsAlgorithm decodeWriteInsAlgorithm(final IsWriteInOptionAlgorithm isWriteInOptionAlgorithm, final
			QuadraticResidueToWriteInAlgorithm quadraticResidueToWriteInAlgorithm) {
		return new DecodeWriteInsAlgorithm(isWriteInOptionAlgorithm, quadraticResidueToWriteInAlgorithm);
	}

	@Bean
	public IsWriteInOptionAlgorithm isWriteInOptionAlgorithm() {
		return new IsWriteInOptionAlgorithm();
	}

	@Bean
	public QuadraticResidueToWriteInAlgorithm quadraticResidueToWriteInAlgorithm(final IntegerToWriteInAlgorithm integerToWriteInAlgorithm) {
		return new QuadraticResidueToWriteInAlgorithm(integerToWriteInAlgorithm);
	}

	@Bean
	public IntegerToWriteInAlgorithm integerToWriteInAlgorithm() {
		return new IntegerToWriteInAlgorithm();
	}

	@Bean
	KeyStore keystore(final KeystoreRepository repository,
			@Value("${direct.trust.keystore.type}")
			final String keystoreType)
			throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
		final KeyStore keyStore = KeyStore.getInstance(keystoreType);
		keyStore.load(repository.getKeyStore(), repository.getKeystorePassword());
		return keyStore;
	}

	@Bean
	SignatureVerification keystoreService(final KeyStore keyStore) {
		return SignatureFactory.getInstance()
				.createSignatureVerification(keyStore);
	}

	@Bean
	XmlFileRepository<ch.post.it.verifier.backend.domain.xmlns.evotingconfig.Configuration> configurationXmlFileRepository() {
		return new XmlFileRepository<>();
	}

	@Bean
	XmlFileRepository<Delivery> deliveryXmlFileRepository() {
		return new XmlFileRepository<>();
	}

	@Bean
	XmlFileRepository<Results> resultsXmlFileRepository() {
		return new XmlFileRepository<>();
	}
}
