/*
 * (c) Copyright 2024 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalFactory;
import ch.post.it.evoting.cryptoprimitives.hashing.Argon2;
import ch.post.it.evoting.cryptoprimitives.hashing.Argon2Factory;
import ch.post.it.evoting.cryptoprimitives.hashing.Argon2Profile;
import ch.post.it.evoting.cryptoprimitives.hashing.Hash;
import ch.post.it.evoting.cryptoprimitives.hashing.HashFactory;
import ch.post.it.evoting.cryptoprimitives.math.Base64;
import ch.post.it.evoting.cryptoprimitives.math.BaseEncodingFactory;
import ch.post.it.evoting.cryptoprimitives.math.Random;
import ch.post.it.evoting.cryptoprimitives.math.RandomFactory;
import ch.post.it.evoting.cryptoprimitives.mixnet.Mixnet;
import ch.post.it.evoting.cryptoprimitives.mixnet.MixnetFactory;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureFactory;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProofFactory;
import ch.post.it.evoting.evotinglibraries.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.channelsecurity.StreamableSymmetricEncryptionDecryptionService;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.electioneventcontext.GetHashElectionEventContextAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.proofofcorrectkeygeneration.VerifyCCSchnorrProofsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.proofofcorrectkeygeneration.VerifyKeyGenerationSchnorrProofsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.votingoptions.FactorizeAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.votingoptions.GetHashContextAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.votingoptions.PrimesMappingTableAlgorithms;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.writeins.DecodeWriteInsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.writeins.IntegerToWriteInAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.writeins.IsWriteInOptionAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.writeins.QuadraticResidueToWriteInAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.ProcessPlaintextsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyMixDecOfflineAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyVotingClientProofsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixonline.GetMixnetInitialCiphertextsAlgorithm;
import ch.post.it.evoting.evotinglibraries.xml.XmlFileRepository;
import ch.post.it.evoting.evotinglibraries.xml.XmlNormalizer;
import ch.post.it.evoting.verifier.backend.tools.KeystoreRepository;
import ch.post.it.evoting.verifier.backend.verifications.setup.consistency.VerifyPrimesMappingTableConsistencyAlgorithm;

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
	public Base64 base64() {
		return BaseEncodingFactory.createBase64();
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
	public VerifyVotingClientProofsAlgorithm verifyVotingClientProofsAlgorithm(final ZeroKnowledgeProof zeroKnowledgeProof,
			final GetHashContextAlgorithm getHashContextAlgorithm, final PrimesMappingTableAlgorithms primesMappingTableAlgorithms) {
		return new VerifyVotingClientProofsAlgorithm(zeroKnowledgeProof, getHashContextAlgorithm, primesMappingTableAlgorithms);
	}

	@Bean
	public VerifyPrimesMappingTableConsistencyAlgorithm verifyPrimesMappingTableConsistencyAlgorithm() {
		return new VerifyPrimesMappingTableConsistencyAlgorithm();
	}

	@Bean
	public GetMixnetInitialCiphertextsAlgorithm getMixnetInitialCiphertextsAlgorithm(final Hash hash, final Base64 base64, final ElGamal elGamal) {
		return new GetMixnetInitialCiphertextsAlgorithm(hash, base64, elGamal);
	}

	@Bean
	public ProcessPlaintextsAlgorithm processPlaintextsAlgorithm(final ElGamal elGamal,
			final FactorizeAlgorithm factorizeAlgorithm,
			final DecodeWriteInsAlgorithm decodeWriteInsAlgorithm,
			final PrimesMappingTableAlgorithms primesMappingTableAlgorithms) {
		return new ProcessPlaintextsAlgorithm(elGamal, factorizeAlgorithm, decodeWriteInsAlgorithm, primesMappingTableAlgorithms);
	}

	@Bean
	public PrimesMappingTableAlgorithms primesMappingTableAlgorithms() {
		return new PrimesMappingTableAlgorithms();
	}

	@Bean
	public GetHashContextAlgorithm getHashContextAlgorithm(final Base64 base64, final Hash hash,
			final PrimesMappingTableAlgorithms primesMappingTableAlgorithms) {
		return new GetHashContextAlgorithm(base64, hash, primesMappingTableAlgorithms);
	}

	@Bean
	public GetHashElectionEventContextAlgorithm getHashElectionEventContextAlgorithm(final Base64 base64, final Hash hash) {
		return new GetHashElectionEventContextAlgorithm(base64, hash);
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
	public FactorizeAlgorithm factorizeAlgorithm() {
		return new FactorizeAlgorithm();
	}

	@Bean
	public VerifyCCSchnorrProofsAlgorithm verifyCCSchnorrProofsAlgorithm(final ZeroKnowledgeProof zeroKnowledgeProof) {
		return new VerifyCCSchnorrProofsAlgorithm(zeroKnowledgeProof);
	}

	@Bean
	public VerifyKeyGenerationSchnorrProofsAlgorithm verifyKeyGenerationSchnorrProofsAlgorithm(
			final VerifyCCSchnorrProofsAlgorithm verifyCCSchnorrProofsAlgorithm,
			final GetHashElectionEventContextAlgorithm getHashElectionEventContextAlgorithm) {
		return new VerifyKeyGenerationSchnorrProofsAlgorithm(verifyCCSchnorrProofsAlgorithm, getHashElectionEventContextAlgorithm);
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
	XmlFileRepository<ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration> configurationXmlFileRepository() {
		return new XmlFileRepository<>();
	}

	@Bean
	XmlFileRepository<ch.ech.xmlns.ech_0222._1.Delivery> ech0222DeliveryXmlFileRepository() {
		return new XmlFileRepository<>();
	}

	@Bean
	XmlNormalizer xmlNormalizer() {
		return new XmlNormalizer();
	}

	@Bean
	Argon2 argon2Standard() {
		return Argon2Factory.createArgon2(Argon2Profile.STANDARD);
	}

	@Bean
	public Random random() {
		return RandomFactory.createRandom();
	}

	@Bean
	StreamableSymmetricEncryptionDecryptionService streamedEncryptionDecryptionService(final Random random, final Argon2 argon2) {
		return new StreamableSymmetricEncryptionDecryptionService(random, argon2);
	}
}
